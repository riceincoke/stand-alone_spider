package com.myspider.core.crawlColletor.webcollector.util;

import java.util.List;

public class ListUtils {
    public static <T> T getByIndex(List<T> list, int index){
        int realIndex = index;
        if (index < 0) {
            realIndex = list.size() + index;
        }
        return list.get(realIndex);
    }

}
