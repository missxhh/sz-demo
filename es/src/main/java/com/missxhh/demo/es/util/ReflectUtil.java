package com.missxhh.demo.es.util;

/**
 * @Description 反射工具类
 * @Author hjf
 * @Date 2019/8/2 9:27
 * @Param 
 * @return 
 **/
public class ReflectUtil {

    /**
     * 拼接在某属性的 set方法
     * @param fieldName
     * @return String
     */
    public static String parSetName(String fieldName) {
        if (null == fieldName || "".equals(fieldName)) {
            return null;
        }
        int startIndex = 0;
        if (fieldName.charAt(0) == '_')
            startIndex = 1;
        return "set" + fieldName.substring(startIndex, startIndex + 1).toUpperCase()
                + fieldName.substring(startIndex + 1);
    }
}
