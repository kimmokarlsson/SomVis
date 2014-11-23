package com.github.kimmokarlsson.somvis.som.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import com.github.kimmokarlsson.somvis.som.CellWeightVariable;
import com.github.kimmokarlsson.somvis.som.ClusteringVariable;
import com.github.kimmokarlsson.somvis.som.SomFileIO;
import com.github.kimmokarlsson.somvis.util.ui.GridPanel;

/**
 * Color settings panel.
 */
public class ColorizationPanel extends GridPanel
{
    private JComboBox<String> primaryVarCombo;
    private JComboBox<String> secondaryVarCombo;

    public ColorizationPanel(final JFrame frame, final HexCanvas canvas, final SomFileIO somio)
    {
        super();
        
        final int preDefVars = 3;
        String[] extraNames = new String[preDefVars];
        extraNames[0] = "(none)";
        extraNames[1] = ClusteringVariable.TITLE;
        extraNames[2] = CellWeightVariable.TITLE;
        
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JLabel title = new JLabel("Colorization");
        title.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        addToLeft(title);
        title.setFont(title.getFont().deriveFont(Font.BOLD).deriveFont(title.getFont().getSize()+2.0f));
        addToLeft("Primary variable: ");
        primaryVarCombo = new JComboBox<>(new SomVariableComboBoxModel(somio, extraNames));
        primaryVarCombo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String text = (String)e.getItem();
                    if ("(none)".equals(text)) {
                        text = null;
                    }
                    canvas.setPrimaryVariableName(text);
                }
            }
        });
        primaryVarCombo.setSelectedIndex(2);
        addToRight(primaryVarCombo);
        addToLeft("Primary color 1: ");
        addToRight(new ColorSelectorPanel(frame, Color.RED, new ColorChangeListener() {
                @Override
                public void colorChanged(Color newColor) {
                    canvas.setPrimColor1(newColor);
                }
            }));
        addToLeft("Primary color 2: ");
        addToRight(new ColorSelectorPanel(frame, Color.BLUE, new ColorChangeListener() {
                @Override
                public void colorChanged(Color newColor) {
                    canvas.setPrimaryColor2(newColor);
                }
            }));
        addToLeft("Secondary variable: ");
        secondaryVarCombo = new JComboBox<>(new SomVariableComboBoxModel(somio, extraNames));
        secondaryVarCombo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String text = (String)e.getItem();
                    if ("(none)".equals(text)) {
                        text = null;
                    }
                    canvas.setSecondaryVariableName(text);
                }
            }
        });
        secondaryVarCombo.setSelectedIndex(0);
        addToRight(secondaryVarCombo);
        addToLeft("Secondary color 1: ");
        addToRight(new ColorSelectorPanel(frame, Color.GREEN, new ColorChangeListener() {
                @Override
                public void colorChanged(Color newColor) {
                    canvas.setSecondaryColor1(newColor);
                }
            }));
        addToLeft("Secondary color 2: ");
        addToRight(new ColorSelectorPanel(frame, Color.YELLOW, new ColorChangeListener() {
                @Override
                public void colorChanged(Color newColor) {
                    canvas.setSecondaryColor2(newColor);
                }
            }));
    }

    public void reset()
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run()
            {
                primaryVarCombo.updateUI();
                secondaryVarCombo.updateUI();
            }});
        primaryVarCombo.setSelectedIndex(2);
        secondaryVarCombo.setSelectedIndex(0);
    }
}
