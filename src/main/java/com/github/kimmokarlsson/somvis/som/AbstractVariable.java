package com.github.kimmokarlsson.somvis.som;


/**
 * 
 */
public abstract class AbstractVariable implements Variable
{
    protected String name;
    protected Type type;
    private int index;
    private boolean ignored;

    public AbstractVariable(String name, Type type, int index)
    {
        this.name = name;
        this.type = type;
        this.index = index;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public Type getType()
    {
        return type;
    }
    
    @Override
    public int getIndex()
    {
        return index;
    }

    @Override
    public boolean isIgnored()
    {
        return ignored;
    }
    @Override
    public void setIgnored(boolean b)
    {
        ignored = b;
    }

    @Override
    public void checkValue(double d)
    {
        if (getMaxValue() > getMinValue())
        {
            if (d < getMinValue())
            {
                throw new IllegalStateException("Value ("+d+") less than minValue ("+getMinValue()+") in var \""+name+"\"!");
            }
            else if (d > getMaxValue())
            {
                throw new IllegalStateException("Value ("+d+") greater than maxValue ("+getMaxValue()+") in var \""+name+"\"!");
            }
        }
    }

    @Override
    public double getNormalizedValue(double val)
    {
        if (ignored)
        {
            return 0.0;
        }
        double div = getMaxValue() - getMinValue();
        if (Math.abs(div) < 0.001) {
            return 0.0;
        }
        return (val - getMinValue()) / div;
    }
}
