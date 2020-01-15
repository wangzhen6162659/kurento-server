package org.kurento.tutorial.one2manycall.common;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;
import java.io.Serializable;
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
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SuperEntity<T> implements Serializable,Cloneable {
  public static final String ID = "id";
  public static final String CREATE_TIME = "createTime";
  public static final String CREATE_USER = "createUser";

  @TableId(value = "id", type = IdType.INPUT)
  @NotNull(message = "id不能为空", groups = Update.class)
  protected T id;

  @TableField(value = "create_time", fill = FieldFill.INSERT)
  protected LocalDateTime createTime;

  @TableField(value = "create_user", fill = FieldFill.INSERT)
  protected T createUser;

  /**
   * 保存和缺省验证组
   */
  public interface Save extends Default {

  }

  /**
   * 更新和缺省验证组
   */
  public interface Update extends Default {

  }

  /**
   * 保存验证组
   */
  public interface OnlySave {

  }

  /**
   * 更新验证组
   */
  public interface OnlyUpdate {

  }

  /**
   * 查询验证组
   */
  public interface OnlyQuery {

  }

  @Override
  public Object clone() {
    //支持克隆  提高性能  仅仅是浅克隆
    try {
      return  super.clone();
    } catch (CloneNotSupportedException e) {
      //因为已经实现了接口 Cloneable  故不会出现此异常
      return null;
    }
  }
}
