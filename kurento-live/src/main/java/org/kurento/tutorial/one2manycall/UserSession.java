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

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import org.kurento.client.IceCandidate;
import org.kurento.client.MediaPipeline;
import org.kurento.client.WebRtcEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.google.gson.JsonObject;

/**
 * User session.
 *
 * @author Boni Garcia (bgarcia@gsyc.es)
 * @since 5.0.0
 */
@Slf4j
public class UserSession {

  private static final Logger log = LoggerFactory.getLogger(UserSession.class);

  private final WebSocketSession session;
  private WebRtcEndpoint webRtcEndpoint;
  private MediaPipeline mediaPipeline;
  private String token;
  private String liveDesc;

  public UserSession(WebSocketSession session) {
    this.session = session;
  }

  public WebSocketSession getSession() {
    return session;
  }

  public void sendMessage(JsonObject message) throws IOException {
    log.info("Sending message from user with session Id '{}': {}", session.getId(), message);
    session.sendMessage(new TextMessage(message.toString()));
  }

  public WebRtcEndpoint getWebRtcEndpoint() {
    return webRtcEndpoint;
  }

  public void setWebRtcEndpoint(WebRtcEndpoint webRtcEndpoint) {
    this.webRtcEndpoint = webRtcEndpoint;
  }

  public void addCandidate(IceCandidate candidate) {
    webRtcEndpoint.addIceCandidate(candidate);
  }

  public MediaPipeline getMediaPipeline() {
    return mediaPipeline;
  }

  public void setMediaPipeline(MediaPipeline mediaPipeline) {
    this.mediaPipeline = mediaPipeline;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getLiveDesc() {
    return liveDesc;
  }

  public void setLiveDesc(String liveDesc) {
    this.liveDesc = liveDesc;
  }
}
