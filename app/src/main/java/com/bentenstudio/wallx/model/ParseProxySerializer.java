package com.bentenstudio.wallx.model;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class ParseProxySerializer implements JsonSerializer<ParseProxyObject> {
    @Override
    public JsonElement serialize(ParseProxyObject src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        String objectId = src.getObjectId();
        for (String key: src.getValues().keySet()){

        }
        return null;
    }
}
