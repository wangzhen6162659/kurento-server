package org.kurento.tutorial.one2manycall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.kurento.tutorial.one2manycall.common.HyLambdaQueryWrapper;
import org.kurento.tutorial.one2manycall.common.Result;
import org.kurento.tutorial.one2manycall.dto.UserReqDTO;
import org.kurento.tutorial.one2manycall.dto.UserResDTO;
import org.kurento.tutorial.one2manycall.dto.UserTokenResDTO;
import org.kurento.tutorial.one2manycall.entity.User;
import org.kurento.tutorial.one2manycall.service.UserService;
import org.kurento.tutorial.one2manycall.utils.EncodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.Objects;

@RestController
@RequestMapping("/api/user")
public class UserController {
  @Autowired
  private UserService userService;

  @RequestMapping(value = "/login", method = RequestMethod.POST)
  Result<UserTokenResDTO> login(@RequestBody UserReqDTO dto) {
    HyLambdaQueryWrapper<User> wrapper = new HyLambdaQueryWrapper<>();
    wrapper.eq(User::getNickname, dto.getNickname())
            .eq(User::getPassword, dto.getPassword());
    User one = userService.getOne(wrapper, false);
    if (one == null){
      return Result.fail("账号或密码错误！");
    }
    Result<String> encode = encode(one.getId());
    if (encode.getData() == null){
      return Result.fail("加密错误！");
    }
    String token = encode(one.getId()).getData();
    UserTokenResDTO res = UserTokenResDTO.builder().token(token).user(one).build();
    return Result.success(res);
  }

  @RequestMapping(value = "/register", method = RequestMethod.POST)
  Result<Boolean> register(@RequestBody UserReqDTO dto) {
    HyLambdaQueryWrapper<User> wrapper = new HyLambdaQueryWrapper<User>();
    User user = User.builder()
      .nickname(dto.getNickname())
      .password(dto.getPassword())
      .createTime(LocalDateTime.now())
      .build();
    userService.save(user);

    return Result.success(true);
  }

  /**
   * id解密，用户跳转是保密传参
   * @return
   */
  @RequestMapping(value = "/decode", method = RequestMethod.POST)
  public Result<UserResDTO> decode (@RequestParam String token) {
    try {
      return Result.success(userService.decode(token));
      //        if (System.currentTimeMillis() - Long.valueOf(tokenArr[1]) > (TOKEN_EFF)) {
      //            return Result.fail("超时,请重新登录!");
      //        }
    } catch (Exception e) {
      return Result.fail("解析失败");
    }
  }

  /**
   * id加密，用户跳转是保密传参
   * @return
   */
  @RequestMapping(value = "/user/encode", method = RequestMethod.GET)
  public Result<String> encode (Long userId) {
    String token = null;
    try {
      token = URLEncoder.encode(Objects.requireNonNull(EncodeUtil.AESEncode(String.valueOf(userId) + '&' + System.currentTimeMillis())), "utf-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return Result.success(token);
  }

  @RequestMapping("/page")
  public Result<Object> selectPage() {
    Page page = new Page(1, 10);          //1表示当前页，而10表示每页的显示显示的条目数
    page = userService.selectUserPage(page, true);
    return Result.success(page);
  }
}
