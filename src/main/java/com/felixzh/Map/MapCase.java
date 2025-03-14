package com.felixzh.Map;

import java.util.HashMap;

public class MapCase {
    public static void main(String[] args) {
        HashMap<Long, HashMap<String, String>> hashMapHashMap = new HashMap<>();
        HashMap<String, String> hashMap = hashMapHashMap.computeIfAbsent(1L, id -> new HashMap<>());
        hashMap.put("1", "1");

        System.out.println(hashMapHashMap);
        // {1={1=1}}
    }
}
