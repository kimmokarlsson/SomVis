package com.github.kimmokarlsson.somvis.util.ui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A <code>JPanel</code> using <code>GridBagLayout</code>. There are helper methods for adding
 * different components. This removes the need for creating <code>GridBagConstraints</code>
 * objects.
 */
public class GridPanel
    extends JPanel
{

	/**
	 * constraints for a leftmost component in a grid row
	 */
	private static GridBagConstraints sm_leftMost = new GridBagConstraints(
	        GridBagConstraints.RELATIVE, GridBagConstraints.RELATIVE, 1, 1, 0.0, 1.0,
	        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);

	/**
	 * constraints for a leftmost component in a grid row
	 */
	private static GridBagConstraints sm_leftMostResizing = new GridBagConstraints(
	        GridBagConstraints.RELATIVE, GridBagConstraints.RELATIVE, 1, 1, 1.0, 1.0,
	        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);

	/**
	 * constraints for a component in the middle of a row. This makes the component take minimum
	 * available space.
	 */
	private static GridBagConstraints sm_middle = new GridBagConstraints(
	        GridBagConstraints.RELATIVE, GridBagConstraints.RELATIVE, 1, 1, 0.0, 0.0,
	        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);

	/**
	 * constraints for a component in the middle of a row. This makes the component take maximum
	 * space.
	 */
	private static GridBagConstraints sm_middleResizing = new GridBagConstraints(
	        GridBagConstraints.RELATIVE, GridBagConstraints.RELATIVE, 1, 1, 1.0, 0.0,
	        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);

	/**
	 * constraints for a component at the end of a row
	 */
	private static GridBagConstraints sm_rightMostResizing = new GridBagConstraints(
	        GridBagConstraints.RELATIVE, GridBagConstraints.RELATIVE,
	        GridBagConstraints.REMAINDER, 1, 1.0, 0.0, GridBagConstraints.WEST,
	        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);

	/**
	 * constraints for a component at the end of a row
	 */
	private static GridBagConstraints sm_rightMost = new GridBagConstraints(
	        GridBagConstraints.RELATIVE, GridBagConstraints.RELATIVE,
	        GridBagConstraints.REMAINDER, 1, 0.0, 0.0, GridBagConstraints.WEST,
	        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);

	/**
	 * constraints for a component at the end of a row
	 */
	private static GridBagConstraints sm_rightBottomResizing = new GridBagConstraints(
	        GridBagConstraints.RELATIVE, GridBagConstraints.RELATIVE,
	        GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1.0, 1.0,
	        GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);

	/**
	 * the layout of this panel
	 */
	private GridBagLayout m_layout;

	/**
	 * true, if left-most column is the resizing column
	 */
	private boolean m_leftResizing;

	/**
	 * true, if addToRight() has been called after last addToLeft()
	 */
	private boolean m_hasLineEnded = true;

	/**
	 * Creates a new panel with grid bag layout.
	 */
	public GridPanel()
	{
		super();
		m_layout = new GridBagLayout();
		setLayout(m_layout);
	}

	/**
	 * Adds the given component to the middle of a row. The component will take extra horizontal
	 * space in to use.
	 * 
	 * @param comp
	 *            component to add
	 * @return the given component after adding it to the panel
	 */
	@Override
    public Component add(Component comp)
	{
		m_layout.setConstraints(comp, sm_middleResizing);
		return super.add(comp);
	}

	/**
	 * Adds the given component as a middle-in-the-row component. This makes the component to take
	 * minimum space in the middle.
	 * 
	 * @param comp
	 *            component to add
	 * @return the given component after adding it to the panel
	 */
	public Component addToMiddle(Component comp)
	{
		m_layout.setConstraints(comp, sm_middle);
		return super.add(comp);
	}

	/**
	 * Adds the given component as the leftmost component of the current row.
	 * 
	 * @param comp
	 *            component to add
	 * @return the given component after adding it to the panel
	 */
	public Component addToLeft(Component comp)
	{
		return addToLeft(comp, false);
	}

	/**
	 * Adds the given component as the leftmost component of the current row.
	 * 
	 * @param comp
	 *            component to add
	 * @param resize
	 * @return the given component after adding it to the panel
	 */
	public Component addToLeft(Component comp,
	                           boolean resize)
	{
		if (!m_hasLineEnded)
		{
			addToRight(new JLabel(""), !m_leftResizing);
		}
		if (resize)
		{
			m_layout.setConstraints(comp, sm_leftMostResizing);
			m_leftResizing = true;
		}
		else
		{
			m_layout.setConstraints(comp, sm_leftMost);
		}
		m_hasLineEnded = false;
		return super.add(comp);
	}

	/**
	 * Adds the given label as the leftmost component of the current row.
	 * 
	 * @param label
	 *            label to wrap inside JLabel
	 * @return the given component after adding it to the panel
	 */
	public Component addToLeft(String label)
	{
		return addToLeft(new JLabel(label));
	}

	/**
	 * Adds the given label as the leftmost component of the current row.
	 * 
	 * @param label
	 *            label to wrap inside JLabel
	 * @return the given component after adding it to the panel
	 */
	public Component addToRight(String label)
	{
		return addToRight(new JLabel(label));
	}

	/**
	 * Adds the given component as the rightmost component of the current row.
	 * 
	 * @param comp
	 *            component to add
	 * @return the given component after adding it to the panel
	 */
	public Component addToRight(Component comp)
	{
		return addToRight(comp, false, false);
	}

	/**
	 * Adds the given component as the rightmost component of the current row.
	 * 
	 * @param comp
	 *            component to add
	 * @param resize
	 *            true, if the component should take the remaining space
	 * @return the given component after adding it to the panel
	 */
	public Component addToRight(Component comp,
	                            boolean resize)
	{
		return addToRight(comp, resize, false);
	}

	/**
	 * Adds the given component as the rightmost component of the current row.
	 * 
	 * @param comp
	 *            component to add
	 * @param resizeHoriz
	 *            true, if the component should take the remaining space horizontally
	 * @param resizeVerti
	 *            true, if the component should take the remaining space vertically
	 * @return the given component after adding it to the panel
	 */
	public Component addToRight(Component comp,
	                            boolean resizeHoriz,
	                            boolean resizeVerti)
	{
		if (resizeVerti)
		{
			m_layout.setConstraints(comp, sm_rightBottomResizing);
		}
		else if (resizeHoriz)
		{
			m_layout.setConstraints(comp, sm_rightMostResizing);
		}
		else
		{
			m_layout.setConstraints(comp, sm_rightMost);
		}
		m_hasLineEnded = true;
		return super.add(comp);
	}

	/**
	 * Adds some empty vertical space to the grid.
	 */
	public void addSeparator()
	{
		addToLeft(new JLabel(" "));
		addToRight(new JLabel(" "));
	}
}
