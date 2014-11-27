package com.github.kimmokarlsson.somvis.util;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.kimmokarlsson.somvis.util.Json;

public class JsonTest extends Assert
{
    @SuppressWarnings("unused")
    private static class Item
    {
        private String id;
        private String name;
        private String description;
        private int value;
        public Item()
        {
        }
        public Item(String id, String name, String description, int value)
        {
            this.id = id;
            this.name = name;
            this.description = description;
            this.value = value;
        }
        public String getId()
        {
            return id;
        }
        public void setId(String id)
        {
            this.id = id;
        }
        public String getName()
        {
            return name;
        }
        public void setName(String name)
        {
            this.name = name;
        }
        public String getDescription()
        {
            return description;
        }
        public void setDescription(String description)
        {
            this.description = description;
        }
        public int getValue()
        {
            return value;
        }
        public void setValue(int value)
        {
            this.value = value;
        }
        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((description == null) ? 0 : description.hashCode());
            result = prime * result + ((id == null) ? 0 : id.hashCode());
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            result = prime * result + value;
            return result;
        }
        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Item other = (Item) obj;
            if (description == null)
            {
                if (other.description != null)
                    return false;
            }
            else if (!description.equals(other.description))
                return false;
            if (id == null)
            {
                if (other.id != null)
                    return false;
            }
            else if (!id.equals(other.id))
                return false;
            if (name == null)
            {
                if (other.name != null)
                    return false;
            }
            else if (!name.equals(other.name))
                return false;
            if (value != other.value)
                return false;
            return true;
        }
    }
    
    @Test
    public void parse() throws Exception
    {
        Json json = new Json();
        String input1 = "{\"id\":\"i1\",\"name\":\"n1\",\"description\":\"d1\",\"value\":1}";
        Item item1 = new Item("i1", "n1", "d1", 1);
        Item item2 = json.parse(input1, Item.class);
        assertEquals(item1, item2);
        String output1 = json.toJsonString(item1);
        assertEquals(input1, output1);
        
        JsonNode node = json.toJson(item1);
        assertEquals("val", json.getString(node, "str", "val"));
        assertEquals(true, json.getBoolean(node, "b", true));
        assertEquals(3, json.getInt(node, "three", 3));
        assertEquals(3.2, json.getDouble(node, "three-two", 3.2), 0.001);
    }
    
    @Test
    public void parseNulls() throws Exception
    {
        Json json = new Json();
        String input1 = "{\"id\":\"i1\",\"name\":null,\"value\":null}";
        Item item1 = new Item("i1", null, null, 0);
        Item item2 = json.parse(input1, Item.class);
        assertEquals(item1, item2);
        
        JsonNode node = json.parse(input1);
        json.fromJson(node, Item.class);
        assertNotNull(node.get("name"));
        assertNotNull(node.get("value"));
        assertEquals("null", node.get("value").asText());
        assertEquals(0, node.get("value").asInt());
    }
}
