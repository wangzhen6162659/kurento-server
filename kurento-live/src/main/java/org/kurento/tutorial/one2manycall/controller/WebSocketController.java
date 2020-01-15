package org.kurento.tutorial.one2manycall.controller;

import org.kurento.client.MediaPipeline;
import org.kurento.tutorial.one2manycall.CallHandler;
import org.kurento.tutorial.one2manycall.UserSession;
import org.kurento.tutorial.one2manycall.common.HyLambdaQueryWrapper;
import org.kurento.tutorial.one2manycall.common.Result;
import org.kurento.tutorial.one2manycall.dto.RoomResDTO;
import org.kurento.tutorial.one2manycall.dto.UserResDTO;
import org.kurento.tutorial.one2manycall.entity.Live;
import org.kurento.tutorial.one2manycall.service.LiveService;
import org.kurento.tutorial.one2manycall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class WebSocketController {
  @Autowired
  CallHandler callHandler;

  @Autowired
  UserService userService;

  @Autowired
  LiveService liveService;

  @RequestMapping(value = "/presentors", method = RequestMethod.GET)
  Result<List<RoomResDTO>> presentors(){
    Map<String, MediaPipeline> pipelines = callHandler.getPipelines();
    Map<String, UserSession> presenterUserSessions = callHandler.getPresenterUserSessions();
    List<RoomResDTO> ret = new ArrayList<>();
    pipelines.forEach((i, item) -> {
      Long id = Long.valueOf(i);
      UserResDTO user = null;
      Live live = null;
      UserSession userSession = presenterUserSessions.get(String.valueOf(id));
      if (userSession != null && userSession.getToken() != null){
        String token = userSession.getToken();
        user = userService.decode(token);
      }
      if (user != null){
        live = liveService.getByUser(user.getId());
      }
      ret.add(RoomResDTO
        .builder()
        .id(id)
        .nickName("房间" + id)
        .user(user)
        .live(live)
        .avatar("https://127.0.0.1/front/live/"+id)
        .build());
    });
    return Result.success(ret);
  }
}
