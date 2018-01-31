package com.search.util;

import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 工具类
 * @author Administrator
 *
 */
@Component
public class CommonUtil {

    //判断字符串是否是日期格式
    public static boolean isValidDate(String str) {
         boolean convertSuccess = true;
        // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
         SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
        try {
                // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
                format.setLenient(false);
                format.parse(str);
        } catch (ParseException e) {
                   // e.printStackTrace();
          // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
                convertSuccess = false;
        }
        return convertSuccess;
    }
}
