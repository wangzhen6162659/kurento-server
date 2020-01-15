package org.kurento.tutorial.one2manycall.common;

import org.apache.commons.lang3.StringUtils;
import org.kurento.tutorial.one2manycall.dto.UserResDTO;
import org.kurento.tutorial.one2manycall.utils.EncodeUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class BaseInterceptor extends HandlerInterceptorAdapter {

  @Override
  public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
    StringBuffer requestURL = httpServletRequest.getRequestURL();
    String api = httpServletRequest.getServletPath();
    if (openUrl().contains(api)){
      return true;
    }
    String token = httpServletRequest.getHeader("token");
    System.out.println("前置拦截器1 preHandle： 请求的uri为："+requestURL.toString());
    if (!StringUtils.isEmpty(token)){
      token = EncodeUtil.AESDncode(URLDecoder.decode(token));
      assert token != null;
      String[] tokenArr = token.split("&");
      Long userId = Long.valueOf(tokenArr[0]);
      BaseContextHandle build = BaseContextHandle.builder().userId(userId).build();
      ThreadLocalUtil.set(build);
    } else {
      return false;
    }
    return true;
  }

  @Override
  public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    System.out.println("拦截器1 postHandle： ");
  }

  @Override
  public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    ThreadLocalUtil.remove();
  }

  /**
   * 备注的url将不会验证登录
   * @return
   */
  public List<String> openUrl(){
    return new ArrayList<String>(){{
      add("/api/user/login");
    }};
  }
}
