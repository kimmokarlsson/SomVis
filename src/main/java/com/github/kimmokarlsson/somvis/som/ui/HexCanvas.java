package com.github.kimmokarlsson.somvis.som.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.github.kimmokarlsson.somvis.som.CellWeightVariable;
import com.github.kimmokarlsson.somvis.som.ClusteringVariable;
import com.github.kimmokarlsson.somvis.som.MapCell;
import com.github.kimmokarlsson.somvis.som.SelfOrganizingMap;
import com.github.kimmokarlsson.somvis.som.SomFileIO;
import com.github.kimmokarlsson.somvis.som.Variable;

/**
 * 
 */
public class HexCanvas extends JPanel
    implements Runnable, MenuItemSelectionListener
{
    private static final int FPS = 25;
    private static final int COLOR_FADE_ANIMATION_LENGTH_MILLIS = 2000;
    private static final int DEFAULT_EDGE_LENGTH = 24;
    private final SomFileIO somio;
    private int edge = DEFAULT_EDGE_LENGTH;
    private Point selected;

    private Color primaryColor1;
    private Color primaryColor2;
    private Color secondaryColor1;
    private Color secondaryColor2;

    private Variable primaryVariable;
    private Variable secondaryVariable;
    private List<CellSelectionListener> listeners;
    private long paintStartTime;

    public HexCanvas(SomFileIO somio)
    {
        this.somio = somio;
        this.selected = new Point(-1,-1);
        listeners = new ArrayList<>();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                SelfOrganizingMap som = HexCanvas.this.somio.getSom();
                if (som != null)
                {
                    Point p = getCell(e.getX(), e.getY(), som);
                    if (p != null)
                    {
                        selected = p;
                        notifyListeners(som.getCell(selected.x, selected.y));
                    }
                }
            }
        });
    }

    public void init()
    {
        paintStartTime = System.currentTimeMillis();
        new Thread(this).start();
    }

    private float getColorByCurrentTime()
    {
        long timeDiff = System.currentTimeMillis() - paintStartTime;
        double animPhase = timeDiff % COLOR_FADE_ANIMATION_LENGTH_MILLIS;
        return (float) Math.sin(Math.PI * animPhase / COLOR_FADE_ANIMATION_LENGTH_MILLIS);
    }

    private Color getAdjustedColor(Color c, float factor)
    {
        return new Color(Math.min(c.getRed() * factor, 255.0f) / 255.0f,
                        Math.min(c.getGreen() * factor, 255.0f) / 255.0f,
                        Math.min(c.getBlue() * factor, 255.0f) / 255.0f);
    }

    @Override
    public void paint(Graphics g)
    {
        final boolean drawNeighbors = false;
        
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.BLACK);
        
        SelfOrganizingMap som = somio.getSom();
        if (som == null) return;
        
        for (HexGridCell cell : new HexGridIterator(som.getCols(), som.getRows(), edge))
        {
            int i = cell.getCol();
            int j = cell.getRow();
            
            Color cellColor = Color.WHITE;
            MapCell mapCell = som.getCell(i, j);
            if ((primaryVariable == null && secondaryVariable == null) || !mapCell.hasData())
            {
                if (j == selected.y && i == selected.x)
                {
                    cellColor = new Color(0xFF,0xE0,0xFF);
                }
                else if (i == selected.x)
                {
                    cellColor = new Color(0xFF,0xF0,0xF0);
                }
                else if (j == selected.y)
                {
                    cellColor = new Color(0xF0,0xF0,0xFF);
                }
            }
            else if (primaryVariable != null && secondaryVariable != null)
            {
                double val = mapCell.getValue(primaryVariable);
                double norm = primaryVariable.getNormalizedValue(val);
                float f = (float) norm;
                Color ipColor1 = getInterpolatedColor(primaryColor1, primaryColor2, f);
                Color ipColor2 = getInterpolatedColor(secondaryColor1, secondaryColor2, f);
                cellColor = getInterpolatedColor(ipColor1, ipColor2, 0.5f);
            }
            else if (primaryVariable != null)
            {
                double val = mapCell.getAvgValue(primaryVariable);
                double norm = primaryVariable.getNormalizedValue(val);
                if (norm < 0.0) {
                    throw new IllegalStateException("normalized value: "+norm+" < 0 @ ("+i+","+j+")");
                }
                cellColor = getInterpolatedColor(primaryColor1, primaryColor2, (float)norm);
            }
            else if (secondaryVariable != null)
            {
                double val = mapCell.getAvgValue(secondaryVariable);
                double norm = secondaryVariable.getNormalizedValue(val);
                cellColor = getInterpolatedColor(secondaryColor1, secondaryColor2, (float)norm);
            }
            if (j == selected.y && i == selected.x && mapCell.hasData())
            {
                cellColor = getInterpolatedColor(getAdjustedColor(cellColor, 0.5f),
                                                getAdjustedColor(cellColor, 1.5f),
                                                getColorByCurrentTime());
            }
            g.setColor(cellColor);
            g.fillPolygon(cell.getPolygon());
            g.setColor(Color.BLACK);
            g.drawPolygon(cell.getPolygon());
            if (0.0 < mapCell.getMeanUnifiedDistance() && mapCell.getMeanUnifiedDistance() < som.getClusterThreshold())
            {
                g.drawPolygon(cell.getSmallerPolygon());
            }
            
            // draw neighbors
            if (drawNeighbors)
            {
                MapCell pn = som.getCell(i,j);
                MapCell sel = som.getCell(selected.x, selected.y);
                if (sel != null && pn != null)
                {
                    for (int k = 0; k < MapCell.NEIGHBOR_COUNT; k++)
                    {
                        if (pn == sel.getNeighbor(k))
                        {
                            g.setColor(new Color(0xE0,0xE0,0xE0));
                            g.fillPolygon(cell.getPolygon());
                            break;
                        }
                    }
                }
            }
        }
    }

    private Color getInterpolatedColor(Color c1, Color c2, float f)
    {
        float r1 = c1.getRed();
        float g1 = c1.getGreen();
        float b1 = c1.getBlue();
        
        float r2 = c2.getRed();
        float g2 = c2.getGreen();
        float b2 = c2.getBlue();
        
        float r = f * (r2 - r1) + r1;
        float g = f * (g2 - g1) + g1;
        float b = f * (b2 - b1) + b1;
        
        Color res = c1;
        try
        {
            res = new Color(r / 255.0f, g / 255.0f, b / 255.0f);
        }
        catch (Exception e)
        {
            System.out.println("(r,g,b): ("+r+","+g+","+b+"), "+f);
        }
        return res;
    }

    private Point getCell(int dx, int dy, SelfOrganizingMap som)
    {
        for (HexGridCell cell : new HexGridIterator(som.getCols(), som.getRows(), edge))
        {
            if (cell.getPolygon().contains(dx, dy))
            {
                return new Point(cell.getCol(), cell.getRow());
            }
        }
        return null;
    }

    @Override
    public Dimension getPreferredSize()
    {
        Dimension dim = new Dimension(0,0);
        SelfOrganizingMap som = somio.getSom();
        if (som != null)
        {
            for (HexGridCell cell : new HexGridIterator(som.getCols(), som.getRows(), edge))
            {
                Polygon p = cell.getPolygon();
                Rectangle bounds = p.getBounds();
                dim.width = Math.max(dim.width, bounds.x+bounds.width);
                dim.height = Math.max(dim.height, bounds.y+bounds.height);
            }
        }
        return dim;
    }

    @Override
    public Dimension getMaximumSize()
    {
        return getPreferredSize();
    }

    @Override
    public Dimension getMinimumSize()
    {
        return getPreferredSize();
    }

    @Override
    public void update(Graphics g)
    {
        paint(g);
    }

    @Override
    public void itemSelected(int itemCode)
    {
        if (itemCode == HexCanvasMenuItems.ITEM_CODE_DEBUG_PRINT)
        {
            System.out.println(somio.getSom());
        }
        else if (itemCode == HexCanvasMenuItems.ITEM_CODE_ZOOM_IN)
        {
            edge = Math.min(50, edge+1);
        }
        else if (itemCode == HexCanvasMenuItems.ITEM_CODE_ZOOM_OUT)
        {
            edge = Math.max(5, edge-1);
        }
        else if (itemCode == HexCanvasMenuItems.ITEM_CODE_ZOOM_RESET)
        {
            edge = DEFAULT_EDGE_LENGTH;
        }
        else if (itemCode == HexCanvasMenuItems.ITEM_CODE_ZOOM_FIT)
        {
            zoomToFit();
        }
    }

    public void zoomToFit()
    {
        SelfOrganizingMap som = somio.getSom();
        if (som != null)
        {
            int cellW = getParent().getWidth() / (som.getCols()+1);
            int cellH = getParent().getHeight() / (som.getRows()+1);
            
            int edgeW = (int) Math.round(cellW / (2.0 * Math.cos(Math.PI*30.0/180.0)));
            int edgeH = cellH * 2 / 3;
            
            edge = Math.max(5, Math.min(edgeW, edgeH));
        }
    }

    private void notifyListeners(MapCell mapCell)
    {
        for (CellSelectionListener l : listeners)
        {
            l.cellSelected(mapCell);
        }
    }

    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                Thread.sleep(1000 / FPS);
            }
            catch(InterruptedException e)
            {
            }
            repaint();
        }
    }

    public void addCellSelectionListener(CellSelectionListener l)
    {
        if (l != null && !listeners.contains(l))
            listeners.add(l);
    }

    public void setPrimColor1(Color newColor)
    {
        primaryColor1 = newColor;
    }

    public void setPrimaryColor2(Color newColor)
    {
        primaryColor2 = newColor;
    }

    public void setSecondaryColor1(Color newColor)
    {
        secondaryColor1 = newColor;
    }

    public void setSecondaryColor2(Color newColor)
    {
        secondaryColor2 = newColor;
    }

    public void setPrimaryVariableName(String text)
    {
        primaryVariable = getVariableByName(text);
    }

    public void setSecondaryVariableName(String text)
    {
        secondaryVariable = getVariableByName(text);
    }

    private Variable getVariableByName(String text)
    {
        if (text == null)
        {
            return null;
        }
        else if (ClusteringVariable.TITLE.equals(text)) {
            return ClusteringVariable.INSTANCE;
        }
        else if (CellWeightVariable.TITLE.equals(text)) {
            return CellWeightVariable.INSTANCE;
        }
        else
        {
            SelfOrganizingMap som = somio.getSom();
            if (som != null)
            {
                for (Variable v : som.getVariables())
                {
                    if (v.getName().equals(text))
                    {
                        return v;
                    }
                }
            }
        }
        return null;
    }
}
