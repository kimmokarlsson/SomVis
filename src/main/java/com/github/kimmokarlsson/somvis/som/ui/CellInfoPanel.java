/**
 * $Id$
 */
package com.github.kimmokarlsson.somvis.som.ui;

import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.github.kimmokarlsson.somvis.som.MapCell;
import com.github.kimmokarlsson.somvis.som.SelfOrganizingMap;
import com.github.kimmokarlsson.somvis.som.SomChangeListener;
import com.github.kimmokarlsson.somvis.som.SomFileIO;
import com.github.kimmokarlsson.somvis.som.Variable;
import com.github.kimmokarlsson.somvis.util.ui.GridPanel;

/**
 * Shows info about the selected cell. 
 */
public class CellInfoPanel extends GridPanel implements CellSelectionListener, SomChangeListener
{
    private final SomFileIO somio;
    private final List<JLabel> infoNames;
    private final List<JLabel> infoValues;
    private final JComboBox<String> vectorSelector;
    private final InputVectorComboBoxModel model;
    private final JLabel coordLabel;

    /**
     * 
     * @param somio
     */
    public CellInfoPanel(SomFileIO somio)
    {
        super();
        this.somio = somio;

        JLabel title = new JLabel("Cell Info");
        title.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));
        title.setFont(title.getFont().deriveFont(Font.BOLD).deriveFont(title.getFont().getSize2D() + 2.0f));
        setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        addToLeft(title);
        
        addToLeft("Selected cell: ");
        coordLabel = new JLabel();
        addToRight(coordLabel);
        addToLeft("Input Vector: ");
        model = new InputVectorComboBoxModel();
        vectorSelector = new JComboBox<String>(model);
        addToRight(vectorSelector);
        vectorSelector.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent evt) {
                if (evt.getStateChange() == ItemEvent.SELECTED)
                {
                    updateLabels((String)evt.getItem());
                }
            }});
        infoNames = new ArrayList<>();
        infoValues = new ArrayList<>();
        createInfoLabels();
        somio.addSomChangeListener(this);
    }

    private void createInfoLabels()
    {
        SelfOrganizingMap som = somio.getSom();
        if (som != null)
        {
            List<Variable> vars = som.getVariables();
            // add more labels if needed
            int existing = infoNames.size();
            for (int i = existing; i < vars.size(); i++)
            {
                JLabel name = new JLabel();
                infoNames.add(name);
                addToLeft(name);
                JLabel label = new JLabel();
                infoValues.add(label);
                addToRight(label);
            }
            for (int i = 0; i < vars.size(); i++)
            {
                infoNames.get(i).setText(vars.get(i).getName());
                infoValues.get(i).setText("");
            }
            // reset extra labels
            for (int i = vars.size(); i < existing; i++)
            {
                infoNames.get(i).setText("");
                infoValues.get(i).setText("");
            }
        }
    }

    private void updateLabels(String idx)
    {
        updateLabels(Integer.parseInt(idx));
    }
    
    private void updateLabels(int index)
    {
        SelfOrganizingMap som = somio.getSom();
        if (som != null)
        {
            List<Variable> vars = som.getVariables();
            int i = 0;
            double[] d = model.getCell().getData().get(index).get();
            for (JLabel lab : infoValues)
            {
                Variable var = vars.get(i);
                lab.setText(var.getStringValue(d[i++]));
            }
        }
    }
    
    private void resetLabels()
    {
        for (JLabel lab : infoValues)
        {
            lab.setText("");
        }        
    }

    /**
     * @see com.github.kimmokarlsson.somvis.som.ui.CellSelectionListener#cellSelected(com.github.kimmokarlsson.somvis.som.MapCell)
     */
    @Override
    public void cellSelected(MapCell m)
    {
        coordLabel.setText("("+m.getX()+","+m.getY()+")");
        vectorSelector.setSelectedIndex(-1);
        resetLabels();
        model.setCell(m, this);
        if (vectorSelector.getItemCount() > 0)
        {
            vectorSelector.setSelectedIndex(0);
        }
    }

    @Override
    public void somChanged()
    {
        createInfoLabels();
    }
}
