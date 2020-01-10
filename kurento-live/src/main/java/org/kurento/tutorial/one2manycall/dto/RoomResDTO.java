package org.kurento.tutorial.one2manycall.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class RoomResDTO {
  private Long id;
  private Long userId;
  private String nickName;
  private String userName;
  private String avatar;

  @Builder
  public RoomResDTO(Long id, String nickName, Long userId, String userName, String avatar) {
    this.id = id;
    this.nickName = nickName;
    this.userId = userId;
    this.userName = userName;
    this.avatar = avatar;
  }
}
