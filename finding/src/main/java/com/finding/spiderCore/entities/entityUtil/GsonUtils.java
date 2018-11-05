package com.finding.spiderCore.entities.entityUtil;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

//格式化json数据
public class GsonUtils {
    public static JsonParser jsonParser = new JsonParser();

    public static JsonElement parse(String json){
        return jsonParser.parse(json);
    }
}
