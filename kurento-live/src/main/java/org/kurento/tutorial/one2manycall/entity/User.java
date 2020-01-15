package org.kurento.tutorial.one2manycall.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.kurento.tutorial.one2manycall.common.Entity;
import org.kurento.tutorial.one2manycall.common.SuperEntity;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Accessors(chain = true)
@Data
@TableName("c_user")
@NoArgsConstructor
@AllArgsConstructor
public class User extends Entity<Long> {
  private static final long serialVersionUID = 1L;

  private String nickname;
  private String password;
  private Boolean state;

  @Builder

  public User(Long id, LocalDateTime createTime, Long createUser, LocalDateTime updateTime, Long updateUser, String nickname, String password, Boolean state) {
    super(id, createTime, createUser, updateTime, updateUser);
    this.nickname = nickname;
    this.password = password;
    this.state = state;
  }
}
