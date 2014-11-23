package com.github.kimmokarlsson.somvis.som;

import java.util.Random;

/**
 * 
 */
public interface Variable
{
    public enum Type { INT,DOUBLE,ENUM }
    
    String getName();
    Type getType();
    int getIndex();
    
    boolean isIgnored();
    void setIgnored(boolean b);

    double getMinValue();
    void setMinValue(double d);
    double getMaxValue();
    void setMaxValue(double d);

    /**
     * 
     * @param s
     * @return
     */
    double getValue(String s);
    
    /**
     * 
     * @param val
     * @return
     */
    double getNormalizedValue(double val);
    
    /**
     * 
     * @param val
     * @return
     */
    String getStringValue(double val);
    
    /**
     * 
     * @param v
     */
    void checkValue(double v);
    
    /**
     * 
     * @return
     */
    double getInitialValue();
    
    /**
     * 
     * @return
     */
    double getRandomValue(Random random);
}
