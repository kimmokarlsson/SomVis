package com.github.kimmokarlsson.somvis.som;

import java.util.Random;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.kimmokarlsson.somvis.util.Json;

/**
 * 
 */
public class IntVariable extends AbstractVariable
{
    private int minValue;
    private int maxValue;
    private int initValue;

    /**
     * 
     * @param name
     * @param minValue
     * @param maxValue
     */
    public IntVariable(int index, String name, int minValue, int maxValue, int initValue)
    {
        super(name, Type.INT, index);
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.initValue = initValue;
    }

    @Override
    public double getValue(Json json, JsonNode node)
    {
        return json.getInt(node, name, 0);
    }

    @Override
    public double getMinValue()
    {
        return minValue;
    }
    
    @Override
    public void setMinValue(double d)
    {
        minValue = (int) d;
    }

    @Override
    public double getMaxValue()
    {
        return maxValue;
    }

    @Override
    public void setMaxValue(double d)
    {
        maxValue = (int) d;
    }

    @Override
    public double getInitialValue()
    {
        return initValue;
    }

    @Override
    public String getStringValue(double val)
    {
        return Integer.toString((int) val);
    }

    @Override
    public double getRandomValue(Random random)
    {
        if (maxValue < minValue) {
            throw new IllegalArgumentException("IntVariable "+ name +" not initialized!");
        }
        return minValue + random.nextInt(maxValue - minValue + 1);
    }
}
