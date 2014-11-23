package com.github.kimmokarlsson.somvis.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Json
{
    private ObjectMapper mapper;

    public Json()
    {
        mapper = new ObjectMapper();
    }

    public ObjectNode newObject()
    {
        return mapper.createObjectNode();
    }

    public String toJsonString(Object object)
    {
        return toJson(object).toString();
    }

    public String toPrettyJsonString(Object object)
    {
        ObjectWriter w = mapper.writerWithDefaultPrettyPrinter();
        try
        {
            return w.writeValueAsString(toJson(object));
        }
        catch (JsonProcessingException e)
        {
            throw new RuntimeException(e);
        }
    }

    public JsonNode toJson(Object object)
    {
        return mapper.valueToTree(object);
    }

    public <T> T fromJson(JsonNode json, Class<T> clazz)
    {
        try
        {
            return mapper.treeToValue(json, clazz);
        }
        catch (JsonProcessingException e)
        {
            throw new RuntimeException(e);
        }
    }

    public JsonNode parse(String content)
    {
        try
        {
            return mapper.readTree(content);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public <T> T parse(String content, Class<T> clazz)
    {
        return fromJson(parse(content), clazz);
    }

    public boolean getBoolean(JsonNode json, String key, boolean defaultValue)
    {
        JsonNode node = json.get(key);
        if (node != null)
        {
            return node.asBoolean(defaultValue);
        }
        return defaultValue;
    }

    public String getString(JsonNode json, String key, String defaultValue)
    {
        JsonNode node = json.get(key);
        if (node != null)
        {
            return node.asText(defaultValue);
        }
        return defaultValue;
    }

    public int getInt(JsonNode json, String key, int defaultValue)
    {
        JsonNode node = json.get(key);
        if (node != null)
        {
            return node.asInt(defaultValue);
        }
        return defaultValue;
    }

    public double getDouble(JsonNode json, String key, double defaultValue)
    {
        JsonNode node = json.get(key);
        if (node != null)
        {
            return node.asDouble(defaultValue);
        }
        return defaultValue;
    }
}
