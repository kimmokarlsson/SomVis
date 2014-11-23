package com.github.kimmokarlsson.somvis.som.ui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ColorSelectorPanel extends JPanel
{
    private ColorPickerDialog dialog;
    private ColorLabel box;
    
    private List<ColorChangeListener> listeners;

    public ColorSelectorPanel(JFrame owner, Color init, ColorChangeListener l)
    {
        super(new GridLayout(1,2));
        listeners = new ArrayList<>();
        addColorChangeListener(l);
        dialog = new ColorPickerDialog(owner, init);
        box = new ColorLabel(10, 10, init);
        add(box);
        
        add(new JButton(new AbstractAction("Choose...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(true);
            }}));
        dialog.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent evt) {
                box.setBackground(dialog.getColor());
                notifyListeners();
            }});
        notifyListeners();
    }

    public Color getColor()
    {
        return dialog.getColor();
    }

    private void notifyListeners()
    {
        for (ColorChangeListener l : listeners)
        {
            l.colorChanged(getColor());
        }
    }
    
    public void addColorChangeListener(ColorChangeListener l)
    {
        if (l != null && !listeners.contains(l))
        {
            listeners.add(l);
        }
    }
}
