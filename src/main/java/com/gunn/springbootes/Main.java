package com.gunn.springbootes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * @author ganjunhui
 * @date 2020/1/15 3:25 下午
 */
public class Main {

    public static void main(String[] args) {
        Set<String> set = new HashSet<>();
        set.add("a");
        set.add("b");
        new ArrayList<String>(set);

        Object[] objects = new Object[11];
        System.out.println(objects.length);

        System.out.println(10 >> 1);
    }
}
