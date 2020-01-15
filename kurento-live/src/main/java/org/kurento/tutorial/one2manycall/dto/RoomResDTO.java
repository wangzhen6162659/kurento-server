package org.kurento.tutorial.one2manycall.dto;

import lombok.Builder;
import lombok.Data;
import org.kurento.tutorial.one2manycall.entity.Live;
import org.kurento.tutorial.one2manycall.entity.User;

@Data
public class RoomResDTO {
  private Long id;
  private String nickName;
  private UserResDTO user;
  private Live live;
  private String avatar;

  @Builder
  public RoomResDTO(Long id, String nickName, UserResDTO user, Live live, String avatar) {
    this.id = id;
    this.nickName = nickName;
    this.user = user;
    this.live = live;
    this.avatar = avatar;
  }
}
