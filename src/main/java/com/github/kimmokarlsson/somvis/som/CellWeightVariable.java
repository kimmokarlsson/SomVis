package com.github.kimmokarlsson.somvis.som;

import java.util.Random;

public class CellWeightVariable extends AbstractVariable
{
    public static final CellWeightVariable INSTANCE = new CellWeightVariable();
    public static final String TITLE = "Cell Weight";
    private int maxValue;
    
    protected CellWeightVariable()
    {
        super("Cell Weight", Type.INT, 1);
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
        return maxValue;
    }

    @Override
    public void setMaxValue(double d)
    {
        maxValue = (int) d;
    }

    @Override
    public double getValue(String s)
    {
        return Integer.parseInt(s);
    }

    @Override
    public double getInitialValue()
    {
        return 0;
    }

    @Override
    public String getStringValue(double val)
    {
        return Double.toString(val);
    }

    @Override
    public double getRandomValue(Random random)
    {
        return 0;
    }
}
