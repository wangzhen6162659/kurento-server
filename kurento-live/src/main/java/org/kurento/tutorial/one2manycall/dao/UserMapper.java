package org.kurento.tutorial.one2manycall.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.kurento.tutorial.one2manycall.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper extends BaseMapper<User> {
  @Select("selectUserList")
  List<User> selectUserList(IPage<User> page, @Param(value = "state") Boolean state);
}
