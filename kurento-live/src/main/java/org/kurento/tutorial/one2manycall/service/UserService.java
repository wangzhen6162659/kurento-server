package org.kurento.tutorial.one2manycall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.kurento.tutorial.one2manycall.dao.UserMapper;
import org.kurento.tutorial.one2manycall.dto.UserResDTO;
import org.kurento.tutorial.one2manycall.entity.User;
import org.springframework.stereotype.Service;

/**
 *
 * User 表数据服务层接口实现类
 *
 */

public interface UserService extends IService<User> {
  Page<User> selectUserPage(Page<User> page, Boolean state);

  UserResDTO decode(String token);
}
