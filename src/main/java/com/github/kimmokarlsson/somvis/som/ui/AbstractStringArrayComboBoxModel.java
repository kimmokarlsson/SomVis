package com.github.kimmokarlsson.somvis.som.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * Simple implementation of a combo box containing string items.
 */
public abstract class AbstractStringArrayComboBoxModel implements ComboBoxModel<String>
{
    protected String selectedItem;
    protected List<ListDataListener> listeners;

    protected AbstractStringArrayComboBoxModel()
    {
        listeners = new ArrayList<>();
    }

    protected void fireUpdateEvent(Object source)
    {
        ListDataEvent evt = new ListDataEvent(source, ListDataEvent.CONTENTS_CHANGED, 0, getSize());
        fireChangeEvent(evt);
    }

    protected void fireChangeEvent(ListDataEvent evt)
    {
        for (ListDataListener l : listeners)
        {
            l.contentsChanged(evt);
        }
    }

    @Override
    public void addListDataListener(ListDataListener l)
    {
        if (l != null && !listeners.contains(l))
        {
            listeners.add(l);
        }
    }

    @Override
    public void removeListDataListener(ListDataListener l)
    {
        listeners.remove(l);
    }

    @Override
    public void setSelectedItem(Object anItem)
    {
        selectedItem = (String) anItem;
    }

    @Override
    public Object getSelectedItem()
    {
        return selectedItem;
    }
}
