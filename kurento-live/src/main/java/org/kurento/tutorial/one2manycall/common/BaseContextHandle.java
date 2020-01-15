package org.kurento.tutorial.one2manycall.common;

import lombok.Builder;
import lombok.Data;

@Data
public class BaseContextHandle {
  private Long userId;

  @Builder
  public BaseContextHandle(Long userId) {
    this.userId = userId;
  }
}
