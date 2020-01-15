package org.kurento.tutorial.one2manycall.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.kurento.tutorial.one2manycall.common.Entity;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Accessors(chain = true)
@Data
@TableName("c_live")
@NoArgsConstructor
@AllArgsConstructor
public class Live extends Entity<Long> {
  private static final long serialVersionUID = 1L;

  @TableField(value = "name_")
  private String name;
  @TableField(value = "desc_")
  private String desc;
  private Integer follow;
  private String tag;
  private Boolean state;

  @Builder
  public Live(Long id, LocalDateTime createTime, Long createUser, LocalDateTime updateTime, Long updateUser, String name, String desc, Integer follow, String tag, Boolean state) {
    super(id, createTime, createUser, updateTime, updateUser);
    this.name = name;
    this.desc = desc;
    this.follow = follow;
    this.tag = tag;
    this.state = state;
  }
}
