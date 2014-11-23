package com.github.kimmokarlsson.somvis.som.ui;

import java.util.Collections;
import java.util.List;

import com.github.kimmokarlsson.somvis.som.SelfOrganizingMap;
import com.github.kimmokarlsson.somvis.som.SomFileIO;
import com.github.kimmokarlsson.somvis.som.Variable;

/**
 * 
 */
public class SomVariableComboBoxModel extends AbstractStringArrayComboBoxModel
{
    private final SomFileIO somio;
    private final String[] extraVariableNames;

    public SomVariableComboBoxModel(SomFileIO somio)
    {
        this(somio, new String[0]);
    }

    public SomVariableComboBoxModel(SomFileIO somio, String[] extraNames)
    {
        super();
        this.somio = somio;
        this.extraVariableNames = extraNames;
    }

    @Override
    public int getSize()
    {
        return getVariables().size() + extraVariableNames.length;
    }

    private List<Variable> getVariables()
    {
        SelfOrganizingMap som = somio.getSom();
        if (som != null)
        {
            return som.getVariables();
        }
        return Collections.emptyList();
    }

    @Override
    public String getElementAt(int index)
    {
        if (index < extraVariableNames.length)
        {
            return extraVariableNames[index];
        }
        index -= extraVariableNames.length;
        return getVariables().get(index).getName();
    }
}
