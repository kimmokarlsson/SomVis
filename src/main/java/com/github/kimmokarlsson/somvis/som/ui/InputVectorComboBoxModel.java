package com.github.kimmokarlsson.somvis.som.ui;

import com.github.kimmokarlsson.somvis.som.MapCell;

/**
 * 
 */
public class InputVectorComboBoxModel extends AbstractStringArrayComboBoxModel
{
    private MapCell cell;

    public InputVectorComboBoxModel()
    {
        super();
    }

    public MapCell getCell()
    {
        return cell;
    }

    public void setCell(MapCell m, Object source)
    {
        cell = m;
        fireUpdateEvent(source);
    }
    
    @Override
    public int getSize()
    {
        if (cell != null)
        {
            return cell.getData().size();
        }
        return 0;
    }

    @Override
    public String getElementAt(int index)
    {
        if (cell != null)
        {
            return Integer.toString(index);
        }
        return null;
    }
}
