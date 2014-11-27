package com.github.kimmokarlsson.somvis.som;

import java.util.Random;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.kimmokarlsson.somvis.util.Json;

/**
 * Constant string that cannot be evaluated, like name or address of the data point.
 * Always ignored.
 */
public class StringVariable extends AbstractVariable
{
    private StringValueRegistry registry;

    public StringVariable(StringValueRegistry reg, int index, String name)
    {
        super(name, Type.STRING, index);
        registry = reg;
        setIgnored(true);
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
        return registry.getSize();
    }

    @Override
    public void setMaxValue(double d)
    {
    }

    @Override
    public double getValue(Json json, JsonNode node)
    {
        String str = json.getString(node, name, "");
        return registry.addValue(str);
    }

    @Override
    public String getStringValue(double val)
    {
        return registry.getValue((int) val);
    }

    @Override
    public double getInitialValue()
    {
        return 0;
    }

    @Override
    public double getRandomValue(Random random)
    {
        return 0;
    }
}
