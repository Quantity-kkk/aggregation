package top.zwjkyq.commons.util;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Description:
 * Copyright: © 2019 CSNT. All rights reserved.
 * Company:CSNT
 *
 * @author kyq
 * @version 1.0
 * @Date 2020/10/21 10:36
 */
public class StringUtil {
    /**
     * 空对象判断
     *
     * @param obj
     * @return
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null || obj.toString().trim().equals("")) {
            return true;
        }
        return false;
    }

    /**
     * 非空对象判断
     *
     * @param obj
     * @return
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    /**
     * 生成uuid
     *
     * @return uuid
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String replaceAll(String src, String match, String as) {
        return !isEmpty(src) && !isEmpty(match) ? src.replace(match, as) : src;
    }

    /**
     * 右填充字符
     *
     * @param src
     * @param len
     * @param ch
     * @return
     */
    public static String padRight(String src, int len, char ch) {
        int diff = len - src.length();
        if (diff <= 0) {
            return src;
        }
        char[] charr = new char[len];
        System.arraycopy(src.toCharArray(), 0, charr, 0, src.length());
        for (int i = src.length(); i < len; i++) {
            charr[i] = ch;
        }
        return new String(charr);
    }

    /**
     * 根据分隔符合并List到字符串
     */
    public static String joinForSqlIn(List lstInput, String delim) {
        if (lstInput == null || lstInput.size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();

        for (Iterator it = lstInput.iterator(); it.hasNext(); ) {
            sb.append("'").append(replaceBlank(it.next())).append("'");

            if (it.hasNext()) {
                sb.append(delim);
            }
        }

        return sb.toString();
    }


    /**
     * 重构字符串，如果为null，返回空串
     */
    public static String replaceBlank(Object obj) {
        if (isEmpty(obj)) {
            return "";
        } else {
            return obj.toString();
        }
    }
}
