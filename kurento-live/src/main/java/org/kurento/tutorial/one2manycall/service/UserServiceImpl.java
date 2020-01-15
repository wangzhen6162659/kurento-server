package org.kurento.tutorial.one2manycall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.kurento.tutorial.one2manycall.dao.UserMapper;
import org.kurento.tutorial.one2manycall.dto.UserResDTO;
import org.kurento.tutorial.one2manycall.entity.User;
import org.kurento.tutorial.one2manycall.utils.EncodeUtil;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 *
 * User 表数据服务层接口实现类
 *
 */
@Service
public class UserServiceImpl  extends ServiceImpl<UserMapper, User> implements UserService {
  public Page<User> selectUserPage(Page<User> page, Boolean state) {
    page.setRecords(baseMapper.selectUserList(page,state));
    return page;
  }

  @Override
  public UserResDTO decode(String token) {
    token = EncodeUtil.AESDncode(URLDecoder.decode(token));
    assert token != null;
    UserResDTO resDTO = new UserResDTO();
    String[] tokenArr = token.split("&");

    if (tokenArr.length > 0) {
      User byId = getById(Long.valueOf(tokenArr[0]));
      resDTO = UserResDTO
        .builder()
        .id(byId.getId())
        .nickname(byId.getNickname())
        .state(byId.getState())
        .build();
      return resDTO;
    }
    return resDTO;
  }
}
