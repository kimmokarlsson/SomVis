package com.github.kimmokarlsson.somvis.som.ui;

import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.github.kimmokarlsson.somvis.som.ClusterAnalyser;
import com.github.kimmokarlsson.somvis.som.MapCell;
import com.github.kimmokarlsson.somvis.som.SelfOrganizingMap;
import com.github.kimmokarlsson.somvis.som.SomFileIO;
import com.github.kimmokarlsson.somvis.util.ui.GridPanel;

/**
 * Shows info about clusters.
 */
public class ClusterInfoPanel extends GridPanel implements CellSelectionListener
{
    private final ClusterAnalyser clusterAnalysis;
    
    private JLabel clusterLabel;
    private JLabel occurLabel;
    private JLabel percentLabel;

    private JSlider thresholdSlider;

    /**
     * 
     * @param som
     */
    public ClusterInfoPanel(final SomFileIO somio)
    {
        this.clusterAnalysis = new ClusterAnalyser(somio);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JLabel title = new JLabel("Clustering");
        title.setFont(title.getFont().deriveFont(Font.BOLD).deriveFont(title.getFont().getSize() + 2.0f));
        title.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        addToLeft(title);
        
        addToLeft("Threshold: ");
        thresholdSlider = new JSlider(0, 1000, 500);
        thresholdSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e)
            {
                SelfOrganizingMap som = somio.getSom();
                if (som != null)
                {
                    som.setClusterThreshold(thresholdSlider.getValue() / 1000.0);
                }
            }
        });
        thresholdSlider.setValue(120);
        addToRight(thresholdSlider);
        addToLeft("Selected cluster: ");
        clusterLabel = new JLabel();
        addToRight(clusterLabel);
        addToLeft("Selected variable: ");
        JComboBox<String> variableSelector = new JComboBox<>(new SomVariableComboBoxModel(somio));
        variableSelector.addItemListener(new ItemListener() {
            @Override public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String s = (String) e.getItem();
                    variableSelected(s);
                }
            }});
        addToRight(variableSelector);
        addToLeft("Minimum Search Value: ");
        final JSlider minSlider = new JSlider(0, 100, 50);
        minSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                clusterAnalysis.setMinSearchValue(minSlider.getValue());
            }
        });
        addToRight(minSlider);
        minSlider.setValue(1);
        addToLeft("Maximum Search Value: ");
        final JSlider maxSlider = new JSlider(0, 100, 50);
        maxSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                clusterAnalysis.setMaxSearchValue(maxSlider.getValue());
            }
        });
        addToRight(maxSlider);
        maxSlider.setValue(5);
        addToLeft("Occurences: ");
        occurLabel = new JLabel();
        occurLabel.setToolTipText("Number of cells containing value in search range.");
        addToRight(occurLabel);
        addToLeft("Amount: ");
        percentLabel = new JLabel();
        addToRight(percentLabel);
        
        if (somio.getSom() != null)
        {
            variableSelector.setSelectedIndex(0);
            variableSelected(variableSelector.getItemAt(0));
        }
    }

    private void variableSelected(String name)
    {
        clusterAnalysis.setSelectedVariable(name);
        updateLabels();
    }

    @Override
    public void cellSelected(MapCell m)
    {
        clusterAnalysis.setMemberPosition(m.getX(), m.getY());
        updateLabels();
    }

    private void updateLabels()
    {
        int cells = clusterAnalysis.getCellCount();
        int freq = clusterAnalysis.getSearchValueFrequency();
        clusterLabel.setText(cells + " cells");
        occurLabel.setText(Integer.toString(freq));
        String per = "";
        if (cells > 0) {
            per = Double.toString(100.0 * freq / cells);
            int len = per.length();
            per = per.substring(0, Math.min(len, 4));
            per += " %";
        }
        percentLabel.setText(per);
        repaint();
    }

    public void reset()
    {
        clusterAnalysis.reset();
        thresholdSlider.setValue(120);
    }
}
