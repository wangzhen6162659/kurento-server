package org.kurento.tutorial.one2manycall.dto;

import lombok.Builder;
import lombok.Data;
import org.kurento.tutorial.one2manycall.entity.User;

@Data
public class UserTokenResDTO {
  private String token;
  private User user;

  @Builder
  public UserTokenResDTO(String token, User user) {
    this.token = token;
    this.user = user;
  }
}
