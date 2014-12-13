package com.github.kimmokarlsson.somvis.som;

import java.util.List;
import java.util.Random;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.kimmokarlsson.somvis.util.Json;

/**
 * 
 */
public class EnumVariable extends AbstractVariable
{
    private List<String> values;
    private String initValue;

    /**
     * 
     * @param name
     * @param values
     */
    public EnumVariable(int index, String name, List<String> values, String init)
    {
        super(name, Type.ENUM, index);
        this.values = values;
        this.initValue = init;
        if (values.indexOf(init) < 0)
        {
            throw new IllegalArgumentException("Initial value (\""+init+"\") is invalid for variable \""+name+"\"");
        }
    }

    @Override
    public double getValue(Json json, JsonNode node)
    {
        String s = json.getString(node, name, initValue);
        if (s == null || s.length() == 0)
        {
        	s = initValue;
        }
        int index = values.indexOf(s);
        if (index < 0)
        {
        	throw new IllegalStateException("Invalid value for enum \""+name+"\": \""+s+"\"!");
        }
        return index;
    }

    @Override
    public double getMinValue()
    {
        return 0;
    }

    @Override
    public void setMinValue(double d)
    {
    }

    @Override
    public double getMaxValue()
    {
        return values.size()-1;
    }

    @Override
    public void setMaxValue(double d)
    {
    }

    @Override
    public double getInitialValue()
    {
        return values.indexOf(initValue);
    }
    
    @Override
    public String getStringValue(double val)
    {
        return values.get((int)val);
    }

    @Override
    public double getRandomValue(Random random)
    {
        return random.nextInt(values.size());
    }
}
