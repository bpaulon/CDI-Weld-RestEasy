package bcp.cdi.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class LogUtil {

  public static String identity(Object obj) {
    return obj.getClass().getSimpleName() + " [" + System.identityHashCode(obj) + "]";
  }
}
