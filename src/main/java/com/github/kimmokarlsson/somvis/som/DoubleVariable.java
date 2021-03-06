package com.github.kimmokarlsson.somvis.som;

import java.util.Random;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.kimmokarlsson.somvis.util.Json;

/**
 * 
 */
public class DoubleVariable extends AbstractVariable
{
    private double minValue;
    private double maxValue;
    private double initValue;

    /**
     * 
     * @param name
     * @param min
     * @param max
     */
    public DoubleVariable(int index, String name, double min, double max, double init)
    {
        super(name, Type.DOUBLE, index);
        minValue = min;
        maxValue = max;
        initValue = init;
    }

    @Override
    public double getValue(Json json, JsonNode line)
    {
        return json.getDouble(line, name, 0.0);
    }

    @Override
    public double getMinValue()
    {
        return minValue;
    }
    
    @Override
    public void setMinValue(double d)
    {
        minValue = d;
    }

    @Override
    public double getMaxValue()
    {
        return maxValue;
    }

    @Override
    public void setMaxValue(double d)
    {
        maxValue = d;
    }

    @Override
    public double getInitialValue()
    {
        return initValue;
    }

    @Override
    public String getStringValue(double val)
    {
        return Double.toString(val);
    }

    @Override
    public double getRandomValue(Random random)
    {
        return getMinValue() + (random.nextDouble() * (getMaxValue() - getMinValue()));
    }
}
