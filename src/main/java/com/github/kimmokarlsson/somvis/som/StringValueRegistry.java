package com.github.kimmokarlsson.somvis.som;

import java.util.ArrayList;
import java.util.List;

/**
 * Store all string values here, so that MapCells can have simple numeric vectors.
 */
public class StringValueRegistry
{
    private List<String> values;

    public StringValueRegistry()
    {
        values = new ArrayList<>();
    }

    public String getValue(int i)
    {
        return values.get(i);
    }

    public int addValue(String value)
    {
        values.add(value);
        return values.size()-1;
    }

    public double getSize()
    {
        return values.size();
    }
}
