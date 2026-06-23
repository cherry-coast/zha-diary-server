package com.cherry.base.utils;

import com.cherry.base.domain.constant.NetworkConstant;
import com.cherry.base.domain.constant.StringConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;

import java.util.*;

/**
 * @author : ganxiongwen
 * Date: 2022/4/20 16:30
 * Description: 字符串工具类
 * ClassName: CherryStringUtil
 * Package: com.cherry.flow.common.util
 * Copyright (c) 2022,All Rights Reserved.
 */
@SuppressWarnings(value = "unused")
public class CherryStringUtil extends StringUtils {


    /**
     * 获取参数不为空值
     *
     * @param value defaultValue 要判断的value
     * @return value 返回值
     */
    public static <T> T nvl(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }


    /**
     * * 判断一个对象数组是否为空
     *
     * @param objects 要判断的对象数组 * @return true：为空 false：非空
     */
    public static boolean isEmpty(Object[] objects) {
        return isNull(objects) || (objects.length == 0);
    }

    /**
     * * 判断一个对象数组是否非空
     *
     * @param objects 要判断的对象数组
     * @return true：非空 false：空
     */
    public static boolean isNotEmpty(Object[] objects) {
        return !isEmpty(objects);
    }


    /**
     * * 判断一个字符串是否为空串
     *
     * @param str String
     * @return true：为空 false：非空
     */
    public static boolean isEmpty(String str) {
        return isNull(str) || StringConstant.EMPTY.equals(str.trim());
    }

    /**
     * * 判断一个字符串是否为非空串
     *
     * @param str String
     * @return true：非空串 false：空串
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * * 判断一个对象是否为空
     *
     * @param object Object
     * @return true：为空 false：非空
     */
    public static boolean isNull(Object object) {
        return object == null;
    }

    /**
     * * 判断一个对象是否非空
     *
     * @param object Object
     * @return true：非空 false：空
     */
    public static boolean isNotNull(Object object) {
        return !isNull(object);
    }

    /**
     * * 判断一个对象是否是数组类型（Java基本型别的数组）
     *
     * @param object 对象
     * @return true：是数组 false：不是数组
     */
    public static boolean isArray(Object object) {
        return isNotNull(object) && object.getClass().isArray();
    }

    /**
     * 去空格
     */
    public static String trim(String str) {
        return (str == null ? "" : str.trim());
    }

    /**
     * 截取字符串
     *
     * @param str 字符串
     * @param start 开始
     * @return 结果
     */
    public static String substring(final String str, int start) {
        if (str == null) {
            return StringConstant.EMPTY;
        }

        if (start < 0) {
            start = str.length() + start;
        }

        if (start < 0) {
            start = 0;
        }
        if (start > str.length()) {
            return StringConstant.EMPTY;
        }

        return str.substring(start);
    }

    /**
     * 截取字符串
     *
     * @param str 字符串
     * @param start 开始
     * @param end 结束
     * @return 结果
     */
    public static String substring(final String str, int start, int end) {
        if (str == null) {
            return StringConstant.EMPTY;
        }

        if (end < 0) {
            end = str.length() + end;
        }
        if (start < 0) {
            start = str.length() + start;
        }

        if (end > str.length()) {
            end = str.length();
        }

        if (start > end) {
            return StringConstant.EMPTY;
        }

        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }

        return str.substring(start, end);
    }

    /**
     * 是否为http(s)://开头
     *
     * @param link 链接
     * @return 结果
     */
    public static boolean isHttp(String link) {
        return StringUtils.startsWithAny(link, NetworkConstant.HTTP, NetworkConstant.HTTPS);
    }

    /**
     * 字符串转set
     *
     * @param str 字符串
     * @param sep 分隔符
     * @return set集合
     */
    public static Set<String> str2Set(String str, String sep) {
        return new HashSet<>(str2List(str, sep, true, false));
    }

    /**
     * 字符串转list
     *
     * @param str 字符串
     * @param sep 分隔符
     * @param filterBlank 过滤纯空白
     * @param trim 去掉首尾空白
     * @return list集合
     */
    public static List<String> str2List(String str, String sep, boolean filterBlank, boolean trim) {
        List<String> list = new ArrayList<>();
        if (StringUtils.isEmpty(str)) {
            return list;
        }

        // 过滤空白字符串
        if (filterBlank && StringUtils.isBlank(str)) {
            return list;
        }
        String[] split = str.split(sep);
        for (String string : split) {
            if (filterBlank && StringUtils.isBlank(string)) {
                continue;
            }
            if (trim) {
                string = string.trim();
            }
            list.add(string);
        }

        return list;
    }

    /**
     * 查找指定字符串是否包含指定字符串列表中的任意一个字符串同时串忽略大小写
     *
     * @param cs 指定字符串
     * @param searchCharSequences 需要检查的字符串数组
     * @return 是否包含任意一个字符串
     */
    public static boolean containsAnyIgnoreCase(CharSequence cs, CharSequence... searchCharSequences) {
        if (isEmpty(cs) || isEmpty(searchCharSequences)) {
            return false;
        }
        for (CharSequence testStr : searchCharSequences) {
            if (containsIgnoreCase(cs, testStr)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 驼峰转下划线命名
     */
    public static String toUnderScoreCase(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        // 前置字符是否大写
        boolean preCharIsUpperCase;
        // 当前字符是否大写
        boolean currentCharIsUpperCase;
        // 下一个字符是否大写
        boolean nextCharIsUpperCase = true;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (i > 0) {
                preCharIsUpperCase = Character.isUpperCase(str.charAt(i - 1));
            } else {
                preCharIsUpperCase = false;
            }

            currentCharIsUpperCase = Character.isUpperCase(c);

            if (i < (str.length() - 1)) {
                nextCharIsUpperCase = Character.isUpperCase(str.charAt(i + 1));
            }

            if (preCharIsUpperCase && currentCharIsUpperCase && !nextCharIsUpperCase) {
                sb.append(StringConstant.UNDERLINE);
            } else if ((i != 0 && !preCharIsUpperCase) && currentCharIsUpperCase) {
                sb.append(StringConstant.UNDERLINE);
            }
            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }

    /**
     * 是否包含字符串
     *
     * @param str 验证字符串
     * @param stres 字符串组
     * @return 包含返回true
     */
    public static boolean inStringIgnoreCase(String str, String... stres) {
        if (str != null && stres != null) {
            for (String s : stres) {
                if (str.equalsIgnoreCase(trim(s))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 删除最后一个字符串
     *
     * @param str 输入字符串
     * @param spit 以什么类型结尾的
     * @return 截取后的字符串
     */
    public static String lastStringDel(String str, String spit) {
        if (!StringUtils.isEmpty(str) && str.endsWith(spit)) {
            return str.subSequence(0, str.length() - 1).toString();
        }
        return str;
    }

    /**
     * 将下划线大写方式命名的字符串转换为驼峰式。如果转换前的下划线大写方式命名的字符串为空，则返回空字符串。 例如：HELLO_WORLD->HelloWorld
     *
     * @param name 转换前的下划线大写方式命名的字符串
     * @return 转换后的驼峰式命名的字符串
     */
    public static String convertToCamelCase(String name) {
        StringBuilder result = new StringBuilder();
        // 快速检查
        if (name == null || name.isEmpty()) {
            // 没必要转换
            return "";
        } else if (!name.contains(StringConstant.UNDERLINE)) {
            // 不含下划线，仅将首字母大写
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        // 用下划线将原始字符串分割
        String[] camels = name.split(StringConstant.UNDERLINE);
        for (String camel : camels) {
            // 跳过原始字符串中开头、结尾的下换线或双重下划线
            if (camel.isEmpty()) {
                continue;
            }
            // 首字母大写
            result.append(camel.substring(0, 1).toUpperCase());
            result.append(camel.substring(1).toLowerCase());
        }
        return result.toString();
    }

    /**
     * 驼峰式命名法 例如：user_name->userName
     */
    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        }
        if (!s.contains(StringConstant.UNDERLINE)) {
            return s;
        }
        s = s.toLowerCase();
        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == StringConstant.UNDERLINE.charAt(0)) {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 查找指定字符串是否匹配指定字符串列表中的任意一个字符串
     *
     * @param str 指定字符串
     * @param strings 需要检查的字符串数组
     * @return 是否匹配
     */
    public static boolean matches(String str, List<String> strings) {
        if (isEmpty(str) || CherryCollectionUtil.listIsEmpty(strings)) {
            return false;
        }
        for (String pattern : strings) {
            if (isMatch(pattern, str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断url是否与规则配置: ? 表示单个字符; * 表示一层路径内的任意字符串，不可跨层级; ** 表示任意层路径;
     *
     * @param pattern 匹配规则
     * @param url 需要匹配的url
     * @return boolean
     */
    public static boolean isMatch(String pattern, String url) {
        AntPathMatcher matcher = new AntPathMatcher();
        return matcher.match(pattern, url);
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj) {
        return (T)obj;
    }


    /**
     * 字符串编码
     * @param str 目标字符串
     * @return 编码后的字符串
     */
    public static String encodeString(String str) {
        if(CherryStringUtil.isEmpty(str)) {
            return "";
        }
        String salt = System.getProperty("system.salt.value");
        Random random = new Random();
        int index = random.nextInt(str.length());
        str = str
                .substring(0, index)
                .concat(salt)
                .concat(str.substring(index));
        return Base64
                .getEncoder()
                .encodeToString(str.getBytes());
    }


    /**
     * 字符串编码
     * @param str 目标字符串
     * @return 编码后的字符串
     */
    public static String decodeString(String str) {
        if(CherryStringUtil.isEmpty(str)) {
            return "";
        }
        String salt = System.getProperty("system.salt.value");
        return new String(Base64.getDecoder().decode(str)).replace(salt, "");
    }


    /**
     * 解析出url参数中的键值对
     * 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
     *
     * @param URL url地址
     * @return url请求参数部分
     * @author lzf
     */
    public static Map<String, String> urlSplit(String URL) {
        Map<String, String> mapRequest = new HashMap<>();
        String[] arrSplit;
        String strUrlParam = truncateUrlPage(URL);
        if (strUrlParam == null) {
            return mapRequest;
        }
        arrSplit = strUrlParam.split("&");
        for (String strSplit : arrSplit) {
            String[] arrSplitEqual;
            arrSplitEqual = strSplit.split("=");
            //解析出键值
            if (arrSplitEqual.length > 1) {
                //正确解析
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
            } else {
                if (!Objects.equals(arrSplitEqual[0], "")) {
                    //只有参数没有值，不加入
                    mapRequest.put(arrSplitEqual[0], "");
                }
            }
        }
        return mapRequest;
    }

    /**
     * 去掉url中的路径，留下请求参数部分
     *
     * @param strURL url地址
     * @return url请求参数部分
     * @author lzf
     */
    private static String truncateUrlPage(String strURL) {
        String strAllParam = null;
        String[] arrSplit;
        strURL = strURL.trim();
        arrSplit = strURL.split("[?]");
        if (strURL.length() > 1) {
            if (arrSplit.length > 1) {
                for (int i = 1; i < arrSplit.length; i++) {
                    strAllParam = arrSplit[i];
                }
            }
        }
        return strAllParam;
    }

    public static String checkStringsIsBlank(String... strings) {
        for (String string : strings) {
            if (isBlank(string)) {
                return string;
            }
        }
        return "";
    }
}
