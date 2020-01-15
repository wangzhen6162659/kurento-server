package org.kurento.tutorial.one2manycall.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserResDTO {
  private Long id;
  private String nickname;
  private String password;
  private Boolean state;

  @Builder
  public UserResDTO(Long id, String nickname, String password, Boolean state) {
    this.id = id;
    this.nickname = nickname;
    this.password = password;
    this.state = state;
  }
}
