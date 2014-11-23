package com.github.kimmokarlsson.somvis.som.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Simple color picker dialog.
 */
public class ColorPickerDialog extends JDialog
{
    private Color color;
    
    public ColorPickerDialog(JFrame owner, Color init)
    {
        super(owner, "Choose color", true);
        color = init;
        setBounds(100, 100, 500, 500);
        
        Container root = getContentPane();
        root.setLayout(new BorderLayout());
        
        final JColorChooser colorChooser = new JColorChooser();
        root.add(colorChooser, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        root.add(buttonPanel, BorderLayout.SOUTH);
        buttonPanel.add(new JButton(new AbstractAction("OK") {
            @Override
            public void actionPerformed(ActionEvent e) {
                color = colorChooser.getColor();
                setVisible(false);
            }}));
        buttonPanel.add(new JButton(new AbstractAction("Cancel") {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }}));
    }

    public Color getColor() {
        return color;
    }
}
