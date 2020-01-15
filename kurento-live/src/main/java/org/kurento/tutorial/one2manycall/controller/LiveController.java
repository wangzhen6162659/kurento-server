package org.kurento.tutorial.one2manycall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.kurento.tutorial.one2manycall.common.HyLambdaQueryWrapper;
import org.kurento.tutorial.one2manycall.common.Result;
import org.kurento.tutorial.one2manycall.common.ThreadLocalUtil;
import org.kurento.tutorial.one2manycall.dto.UserReqDTO;
import org.kurento.tutorial.one2manycall.dto.UserResDTO;
import org.kurento.tutorial.one2manycall.dto.UserTokenResDTO;
import org.kurento.tutorial.one2manycall.entity.Live;
import org.kurento.tutorial.one2manycall.entity.User;
import org.kurento.tutorial.one2manycall.service.LiveService;
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
@RequestMapping("/api/live")
public class LiveController {
  @Autowired
  private LiveService liveService;

  @RequestMapping("/page")
  public Result<Object> selectPage() {
    Page page = new Page(1, 10);          //1表示当前页，而10表示每页的显示显示的条目数
    page = liveService.selectLivePage(page, true);
    return Result.success(page);
  }

  @RequestMapping("/get")
  public Result<Object> get() {
    Long userId = ThreadLocalUtil.get().getUserId();
    return Result.success(liveService.getByUser(userId));
  }
}
