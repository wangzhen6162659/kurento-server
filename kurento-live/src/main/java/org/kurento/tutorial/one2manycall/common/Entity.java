package org.kurento.tutorial.one2manycall.common;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * This is a Description
 *
 * @author tangyh
 * @date 2019/05/05
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class Entity<T> extends SuperEntity<T> {

  public static final String UPDATE_TIME = "updateTime";
  public static final String UPDATE_USER = "updateUser";

  @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
  protected LocalDateTime updateTime;

  @TableField(value = "update_user", fill = FieldFill.INSERT_UPDATE)
  protected T updateUser;

  public Entity(T id, LocalDateTime createTime, T createUser, LocalDateTime updateTime, T updateUser) {
    super(id, createTime, createUser);
    this.updateTime = updateTime;
    this.updateUser = updateUser;
  }

  public Entity() {
  }

}
