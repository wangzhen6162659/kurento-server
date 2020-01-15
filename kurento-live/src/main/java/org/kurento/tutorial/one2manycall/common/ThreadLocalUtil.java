package org.kurento.tutorial.one2manycall.common;

import com.alibaba.ttl.TransmittableThreadLocal;

public class ThreadLocalUtil {
  private ThreadLocalUtil() {
  }

  private static ThreadLocal<BaseContextHandle> LOCAL = new TransmittableThreadLocal<>();

  public static void set(BaseContextHandle requestHeaderBaseInfo) {
    LOCAL.set(requestHeaderBaseInfo);
  }

  public static BaseContextHandle get() {
    return LOCAL.get();
  }

  public static void remove() {
    LOCAL.remove();
  }

}
