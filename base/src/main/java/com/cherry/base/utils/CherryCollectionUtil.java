package com.cherry.base.utils;

import cn.hutool.core.collection.CollectionUtil;
import com.cherry.base.domain.constant.StringConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author cherry
 */
@Slf4j
@SuppressWarnings(value = "unused")
public class CherryCollectionUtil extends CollectionUtil{

    private final static Boolean removeNullFlag = Boolean.TRUE;

    /**
     * 判断一个Collection是否为空， 包含List，Set，Queue
     *
     * @param collection 要判断的Collection
     * @return true：为空 false：非空
     */
    public static boolean listIsEmpty(Collection<?> collection) {
        if (Objects.isNull(collection)) {
            return true;
        }
        if (removeNullFlag) {
            collection = removeNullObject(collection);
        }
        return CollectionUtil.isEmpty(collection);
    }

    /**
     * 判断一个Collection是否非空，包含List，Set，Queue
     *
     * @param collection 要判断的Collection
     * @return true：非空 false：空
     */
    public static boolean listIsNotEmpty(Collection<?> collection) {
        return !listIsEmpty(collection);
    }

    /**
     * 判断一个Map是否为空
     *
     * @param map 要判断的Map
     * @return true：为空 false：非空
     */
    public static boolean mapIsEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * 判断一个Map是否为空
     *
     * @param map 要判断的Map
     * @return true：非空 false：空
     */
    public static boolean mapIsNotEmpty(Map<?, ?> map) {
        return !mapIsEmpty(map);
    }

    /**
     * 判断一个Array是否为空
     *
     * @param objects 要判断的Object[]
     * @return true：空 false：非空
     */
    public static boolean arrayIsEmpty(Object[] objects) {
        return objects == null || objects.length == 0;
    }

    /**
     * single class conversion
     *
     * @param sourceObj   source object
     * @param targetClass target object
     * @return target object
     */
    public static <T> T convert(Object sourceObj, Class<T> targetClass) {
        if (sourceObj == null || targetClass == null) {
            return null;
        }
        T targetObj;
        try {
            targetObj = targetClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            log.error("An error occurred while copying and convert the list detail : {}", e.toString());
            return null;
        }
        BeanUtils.copyProperties(sourceObj, targetObj);
        return targetObj;
    }

    /**
     * copy List
     *
     * @param sourceList  source list
     * @param targetClass target class
     * @return target class list
     */
    public static <T> List<T> copyList(List<?> sourceList, Class<T> targetClass) {
        if (CherryCollectionUtil.listIsEmpty(sourceList) || targetClass == null) {
            return Collections.emptyList();
        }
        return sourceList.stream()
                .filter(Objects::nonNull)
                .map(sourceObj -> convert(sourceObj, targetClass))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    /**
     * Array formatting
     * removes left and right spaces from elements inside the array
     *
     * @param sourceArray source array
     * @return target array
     */
    public static String[] removeArrayElementSpaces(String[] sourceArray) {
        if (CherryCollectionUtil.arrayIsEmpty(sourceArray)) {
            return sourceArray;
        }
        return Arrays.stream(sourceArray)
                .toList()
                .stream()
                .map(String::trim)
                .toList()
                .toArray(new String[sourceArray.length]);
    }

    /**
     * an array of strings is converted into a list of strings
     *
     * @param sourceString original string
     * @return string list
     */
    public static List<String> stringArrayTransformToList(String sourceString) {
        if (CherryStringUtil.isBlank(sourceString)) {
            return new ArrayList<>();
        }
        String[] stringArray = removeArrayElementSpaces(sourceString.split(","));
        return new ArrayList<>(new ArrayList<>(Arrays.asList(stringArray)));
    }

    /**
     * cases are ignored when determining whether the string list contains target strings
     *
     * @param originalString original string
     * @param stringList     string list
     * @return judge the result, contain : true  Does not contain : false
     */
    public static boolean listContainsIgnoreCase(String originalString, List<String> stringList) {
        if (listIsEmpty(stringList) || CherryStringUtil.isBlank(originalString)) {
            return false;
        }
        for (String s : stringList) {
            if (originalString.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Convert an array of objects to a string, separated by a line break.
     *
     * @param array {@link Object[]}
     * @return {@link String}
     */
    public static String arrayToString(Object[] array) {
        if (array == null) {
            return StringConstant.EMPTY;
        }
        return Arrays
                .stream(array)
                .filter(Objects::nonNull)
                .map(Object::toString)
                .filter(CherryStringUtil::isNotBlank)
                .collect(Collectors.joining("\n"));
    }

    /**
     * reverses the order of the elements in the original list and returns to the new list
     *
     * @param originalList original list
     * @param <V>          the generic type parameter, which represents the type of the element in the list
     * @return reversed list
     */
    public static <V> List<V> reversedList(List<V> originalList) {
        List<V> reversedList = new ArrayList<>(originalList);
        Collections.reverse(reversedList);
        return reversedList;
    }

    /**
     * based on the initial values and step sizes specified,
     * the elements in the original list are sorted by values and stored in a map.
     *
     * @param originalList original list
     * @param initValue    initial value
     * @param step         step
     * @param <V>          the type of the original list element
     * @return the original list elements that are stored in the map after sorting by value
     */
    public static <V> Map<Integer, V> mapByIndexedValue(List<V> originalList, int initValue, int step) {
        Map<Integer, V> result = new HashMap<>(16);
        if (listIsEmpty(originalList)) {
            return result;
        }
        int innerInitValue = initValue;
        for (V v : originalList) {
            result.put(innerInitValue, v);
            innerInitValue += step;
        }
        return result;
    }


    public static List<?> removeNullObject(Collection<?> originalList) {
        return originalList.stream().filter(Objects::nonNull).toList();
    }

}
