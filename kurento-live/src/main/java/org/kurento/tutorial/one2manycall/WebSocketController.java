package org.kurento.tutorial.one2manycall;

import org.kurento.client.MediaPipeline;
import org.kurento.tutorial.one2manycall.common.Result;
import org.kurento.tutorial.one2manycall.dto.RoomResDTO;
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

  @RequestMapping(value = "/presentors", method = RequestMethod.GET)
  Result<List<RoomResDTO>> presentors(){
    Map<String, MediaPipeline> pipelines = callHandler.getPipelines();
    List<RoomResDTO> ret = new ArrayList<>();
    pipelines.forEach((i, item) -> {
      Long id = Long.valueOf(i);
      ret.add(RoomResDTO
        .builder()
        .id(id)
        .userId(id)
        .nickName("房间" + id)
        .userName("用户" + id)
        .avatar("https://127.0.0.1/front/live/"+id)
        .build());
    });
    return Result.success(ret);
  }
}
