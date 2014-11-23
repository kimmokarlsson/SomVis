package com.github.kimmokarlsson.somvis.som;

import java.util.Random;

public class ClusteringVariable extends AbstractVariable
{
    public static final ClusteringVariable INSTANCE = new ClusteringVariable();
    public static final String TITLE = "Clusters";
    
    protected ClusteringVariable()
    {
        super("Clustering", Type.DOUBLE, 0);
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
        return 1.0;
    }

    @Override
    public void setMaxValue(double d)
    {
    }

    @Override
    public double getValue(String s)
    {
        return Double.parseDouble(s);
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
