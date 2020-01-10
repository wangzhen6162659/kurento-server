/*
 * (C) Copyright 2014 Kurento (http://kurento.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.kurento.tutorial.one2manycall;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.kurento.client.EventListener;
import org.kurento.client.IceCandidate;
import org.kurento.client.IceCandidateFoundEvent;
import org.kurento.client.KurentoClient;
import org.kurento.client.MediaPipeline;
import org.kurento.client.WebRtcEndpoint;
import org.kurento.jsonrpc.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Protocol handler for 1 to N video call communication.
 *
 * @author Boni Garcia (bgarcia@gsyc.es)
 * @since 5.0.0
 */
public class CallHandler extends TextWebSocketHandler {

  private static final Logger log = LoggerFactory.getLogger(CallHandler.class);
  private static final Gson gson = new GsonBuilder().create();

  private final ConcurrentHashMap<String, Map<String, UserSession>> viewers = new ConcurrentHashMap<>();

  @Autowired
  private KurentoClient kurento;

  private Map<String, MediaPipeline> pipelines = new HashMap<>();
  private Map<String, UserSession> presenterUserSessions = new HashMap<>();

  @Override
  public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    JsonObject jsonMessage = gson.fromJson(message.getPayload(), JsonObject.class);
    log.debug("Incoming message from session '{}': {}", session.getId(), jsonMessage);
    session.getAttributes().put("jsonMessage", jsonMessage);
    switch (jsonMessage.get("id").getAsString()) {
      case "presenter":
        try {
          presenter(session, jsonMessage);
        } catch (Throwable t) {
          handleErrorResponse(t, session, "presenterResponse", jsonMessage);
        }
        break;
      case "viewer":
        try {
          viewer(session, jsonMessage);
        } catch (Throwable t) {
          handleErrorResponse(t, session, "viewerResponse", jsonMessage);
        }
        break;
      case "onIceCandidate": {
        JsonObject candidate = jsonMessage.get("candidate").getAsJsonObject();

        UserSession user = null;
        UserSession presenterUserSession = getLivePresenter(jsonMessage);
        if (presenterUserSession != null) {
          if (presenterUserSession.getSession() == session) {
            user = presenterUserSession;
          } else {
            user = getLiveViewMap(jsonMessage).get(session.getId());
          }
        }
        if (user != null) {
          IceCandidate cand =
            new IceCandidate(candidate.get("candidate").getAsString(), candidate.get("sdpMid")
              .getAsString(), candidate.get("sdpMLineIndex").getAsInt());
          user.addCandidate(cand);
        }
        break;
      }
      case "interaction":
        try {
          interaction(session, jsonMessage);
        } catch (Throwable t) {
          handleErrorResponse(t, session, "interactionResponse", jsonMessage);
        }
        break;
      case "stop":
        stop(session, jsonMessage);
        break;
      default:
        break;
    }
  }

  private void handleErrorResponse(Throwable throwable, WebSocketSession session, String responseId, JsonObject jsonMessage)
    throws IOException {
    stop(session, jsonMessage);
    log.error(throwable.getMessage(), throwable);
    JsonObject response = new JsonObject();
    response.addProperty("id", responseId);
    response.addProperty("response", "rejected");
    response.addProperty("message", throwable.getMessage());
    session.sendMessage(new TextMessage(response.toString()));
  }

  //获取房间id
  private String getLiveId(JsonObject jsonMessage) {
    if (!jsonMessage.has("liveId")) {
      return null;
    }
    return String.valueOf(jsonMessage.get("liveId"));
  }

  //获取房间对应viewmap
  private Map<String, UserSession> getLiveViewMap(JsonObject jsonMessage) {
    String liveId = getLiveId(jsonMessage);
    if (!viewers.containsKey(liveId)) {
      viewers.put(liveId, new HashMap<>());
    }
    return viewers.get(liveId);
  }

  //获取房间对应主播
  private UserSession getLivePresenter(JsonObject jsonMessage) {
    String liveId = getLiveId(jsonMessage);
    if (!presenterUserSessions.containsKey(liveId)) {
      return null;
    }
    return presenterUserSessions.get(liveId);
  }

  //设置房间对应主播
  private UserSession setLivePresenter(WebSocketSession session, JsonObject jsonMessage) {
    String liveId = getLiveId(jsonMessage);
    presenterUserSessions.put(liveId, new UserSession(session));
    return presenterUserSessions.get(liveId);
  }

  //清空房间对应主播
  private void removeLivePresenter(JsonObject jsonMessage) {
    String liveId = getLiveId(jsonMessage);
    presenterUserSessions.remove(liveId);
  }

  private Map<String, UserSession> getAllUserByLiveId(JsonObject jsonMessage) {
    String liveId = getLiveId(jsonMessage);
    HashMap<String, UserSession> ret = new HashMap<>();
    ret.putAll(getLiveViewMap(jsonMessage));
    UserSession livePresenter = getLivePresenter(jsonMessage);
    ret.put("presenter", livePresenter);
    return ret;
  }

  private synchronized void presenter(final WebSocketSession session, JsonObject jsonMessage)
    throws IOException {
    if (getLivePresenter(jsonMessage) == null) {
      UserSession presenterUserSession = setLivePresenter(session, jsonMessage);

      MediaPipeline pipeline = kurento.createMediaPipeline();
      presenterUserSession.setWebRtcEndpoint(new WebRtcEndpoint.Builder(pipeline).build());
      presenterUserSession.setMediaPipeline(pipeline);

      WebRtcEndpoint presenterWebRtc = presenterUserSession.getWebRtcEndpoint();

      presenterWebRtc.addIceCandidateFoundListener(new EventListener<IceCandidateFoundEvent>() {

        @Override
        public void onEvent(IceCandidateFoundEvent event) {
          JsonObject response = new JsonObject();
          response.addProperty("id", "iceCandidate");
          response.add("candidate", JsonUtils.toJsonObject(event.getCandidate()));
          try {
            synchronized (session) {
              session.sendMessage(new TextMessage(response.toString()));
            }
          } catch (IOException e) {
            log.debug(e.getMessage());
          }
        }
      });

//      // Media logic
//      FaceOverlayFilter faceOverlayFilter = new FaceOverlayFilter.Builder(pipeline).build();
//
//      //String appServerUrl = System.getProperty("app.server.url",
//      //    MagicMirrorApp.DEFAULT_APP_SERVER_URL);
//      String appServerUrl = "http://files.openvidu.io";
//      faceOverlayFilter.setOverlayedImage(appServerUrl + "/img/mario-wings.png", -0.35F, -1.2F,
//        1.6F, 1.6F);
//
//      presenterWebRtc.connect(faceOverlayFilter);
//      faceOverlayFilter.connect(presenterWebRtc);

      // Media logic
//      KmsShowFaces showFaces = new KmsShowFaces.Builder(pipeline).build();
//      KmsDetectFaces detectFaces = new KmsDetectFaces.Builder(pipeline).build();
//
//      presenterWebRtc.connect(detectFaces);
//      detectFaces.connect(showFaces);
//      showFaces.connect(presenterWebRtc);


      //设置房间
      pipelines.put(getLiveId(jsonMessage), pipeline);

      String sdpOffer = jsonMessage.getAsJsonPrimitive("sdpOffer").getAsString();
      String sdpAnswer = presenterWebRtc.processOffer(sdpOffer);

      JsonObject response = new JsonObject();
      response.addProperty("id", "presenterResponse");
      response.addProperty("response", "accepted");
      response.addProperty("sdpAnswer", sdpAnswer);

      synchronized (session) {
        presenterUserSession.sendMessage(response);
      }
      presenterWebRtc.gatherCandidates();

    } else {
      JsonObject response = new JsonObject();
      response.addProperty("id", "presenterResponse");
      response.addProperty("response", "rejected");
      response.addProperty("message",
        "Another user is currently acting as sender. Try again later ...");
      session.sendMessage(new TextMessage(response.toString()));
    }
  }

  private synchronized void viewer(final WebSocketSession session, JsonObject jsonMessage)
    throws IOException {
    UserSession presenterUserSession = getLivePresenter(jsonMessage);
    if (presenterUserSession == null || presenterUserSession.getWebRtcEndpoint() == null) {
      JsonObject response = new JsonObject();
      response.addProperty("id", "viewerResponse");
      response.addProperty("response", "rejected");
      response.addProperty("message",
        "No active sender now. Become sender or . Try again later ...");
      session.sendMessage(new TextMessage(response.toString()));
    } else {
      if (viewers.containsKey(getLiveId(jsonMessage)) && viewers.get(getLiveId(jsonMessage)).containsKey(session.getId())) {
//        JsonObject response = new JsonObject();
//        response.addProperty("id", "viewerResponse");
//        response.addProperty("response", "rejected");
//        response.addProperty("message", "You are already viewing in this session. "
//            + "Use a different browser to add additional viewers.");
//        session.sendMessage(new TextMessage(response.toString()));

        getLiveViewMap(jsonMessage).remove(session.getId());
      }
      UserSession viewer = new UserSession(session);
      getLiveViewMap(jsonMessage).put(session.getId(), viewer);

      WebRtcEndpoint nextWebRtc = new WebRtcEndpoint.Builder(pipelines.get(getLiveId(jsonMessage))).build();

      nextWebRtc.addIceCandidateFoundListener(new EventListener<IceCandidateFoundEvent>() {

        @Override
        public void onEvent(IceCandidateFoundEvent event) {
          JsonObject response = new JsonObject();
          response.addProperty("id", "iceCandidate");
          response.add("candidate", JsonUtils.toJsonObject(event.getCandidate()));
          try {
            synchronized (session) {
              session.sendMessage(new TextMessage(response.toString()));
            }
          } catch (IOException e) {
            log.debug(e.getMessage());
          }
        }
      });

      viewer.setWebRtcEndpoint(nextWebRtc);
      presenterUserSession.getWebRtcEndpoint().connect(nextWebRtc);

      String sdpOffer = jsonMessage.getAsJsonPrimitive("sdpOffer").getAsString();
      String sdpAnswer = nextWebRtc.processOffer(sdpOffer);

      JsonObject response = new JsonObject();
      response.addProperty("id", "viewerResponse");
      response.addProperty("response", "accepted");
      response.addProperty("sdpAnswer", sdpAnswer);

      synchronized (session) {
        viewer.sendMessage(response);
      }
      nextWebRtc.gatherCandidates();
    }
  }

  private synchronized void interaction(final WebSocketSession session, JsonObject jsonMessage)
    throws IOException {
    String msg = jsonMessage.get("msg").getAsString();
    String user = jsonMessage.get("token").getAsString();
    LocalDateTime now = LocalDateTime.now();

    JsonObject response = new JsonObject();
    response.addProperty("id", "interactionResponse");
    response.addProperty("response", "accepted");
    response.addProperty("user", user);
    response.addProperty("msg", msg);
    response.addProperty("type", "interaction");
    response.addProperty("time", now.toString());

    synchronized (session) {
      getAllUserByLiveId(jsonMessage).forEach((i, ws) -> {
        try {
          ws.sendMessage(response);
        } catch (IOException e) {
          e.printStackTrace();
        }
      });
    }
  }

  private synchronized void stop(WebSocketSession session, JsonObject jsonMessage) throws IOException {
    String sessionId = session.getId();
    if (jsonMessage == null) {
      return;
    }
    UserSession presenterUserSession = getLivePresenter(jsonMessage);
    if (presenterUserSession != null && presenterUserSession.getSession().getId().equals(sessionId)) {
      for (UserSession viewer : getLiveViewMap(jsonMessage).values()) {
        JsonObject response = new JsonObject();
        response.addProperty("id", "stopCommunication");
        viewer.sendMessage(response);
      }

      log.info("Releasing media pipeline");
      if (pipelines.get(getLiveId(jsonMessage)) != null) {
        pipelines.get(getLiveId(jsonMessage)).release();
      }
      pipelines.remove(getLiveId(jsonMessage));
      removeLivePresenter(jsonMessage);
    } else if (getLiveViewMap(jsonMessage).containsKey(sessionId)) {
      if (getLiveViewMap(jsonMessage).get(sessionId).getWebRtcEndpoint() != null) {
        getLiveViewMap(jsonMessage).get(sessionId).getWebRtcEndpoint().release();
      }
      getLiveViewMap(jsonMessage).remove(sessionId);
    }
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    stop(session, (JsonObject) session.getAttributes().get("jsonMessage"));
  }

  public ConcurrentHashMap<String, Map<String, UserSession>> getViewers() {
    return viewers;
  }

  public Map<String, MediaPipeline> getPipelines() {
    return pipelines;
  }

  public void setPipelines(Map<String, MediaPipeline> pipelines) {
    this.pipelines = pipelines;
  }

  public Map<String, UserSession> getPresenterUserSessions() {
    return presenterUserSessions;
  }

  public void setPresenterUserSessions(Map<String, UserSession> presenterUserSessions) {
    this.presenterUserSessions = presenterUserSessions;
  }
}
