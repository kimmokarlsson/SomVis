package com.github.kimmokarlsson.somvis.util;

import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ArgsParser
{
    private Map<String, String> params;

    /**
     * Parse command-line arguments.
     * @param args command line arguments
     * @param minReqd minimum number of required arguments 
     * @throws ParseException on errors
     */
    public ArgsParser(String[] args, int minReqd) throws ParseException
    {
        if (args == null)
        {
            throw new ParseException("Null args!", 0);
        }
        else if (args.length == 0)
        {
            throw new ParseException("No args!", 0);
        }
        else if (args.length < minReqd)
        {
            throw new ParseException("Too few args!", 0);
        }
        else if (args.length % 2 == 1)
        {
            throw new ParseException("Odd number of arguments!", args.length);
        }
        
        params = new LinkedHashMap<>();
        for (int i = 1; i < args.length; i+=2)
        {
            String key = args[i-1];
            String value = args[i];
            if (!key.startsWith("-"))
            {
                throw new ParseException("Command-line switch should have dash-prefix!", i-1);
            }
            params.put(key, value);
        }
    }

    public Map<String,String> getParams()
    {
        return params;
    }

    public String getValue(String key)
    {
        String value = params.get(key);
        if (value == null || value.trim().length() == 0) {
            throw new IllegalArgumentException("No value found for param: "+key);
        }
        return value;
    }

    public int getIntValue(String key)
    {
        return Integer.parseInt(getValue(key));
    }

    public String getOptValue(String key)
    {
        return params.get(key);
    }
}
