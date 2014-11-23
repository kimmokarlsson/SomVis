package com.github.kimmokarlsson.somvis.som;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class ClusterAnalyser
{
    private final SomFileIO somio;
    private Variable selectedVariable;
    private Point sampleMemberPosition;
    private int frequency;
    private int minSearchValue;
    private int maxSearchValue;
    private List<MapCell> clusterMembers;
    
    public ClusterAnalyser(SomFileIO somio)
    {
        this.somio = somio;
        clusterMembers = new ArrayList<>();
    }

    public void reset()
    {
        clusterMembers = new ArrayList<>();
        selectedVariable = null;
        sampleMemberPosition = null;
        frequency = 0;
    }

    public void setSelectedVariable(String name)
    {
        selectedVariable = getVariable(name);
        reCalculate();
    }

    private Variable getVariable(String name)
    {
        SelfOrganizingMap som = somio.getSom();
        if (som != null)
        {
            for (Variable v : som.getVariables())
            {
                if (name.equals(v.getName()))
                {
                    return v;
                }
            }
        }
        return null;
    }

    public void setMemberPosition(int x, int y)
    {
        sampleMemberPosition = new Point(x, y);
        SelfOrganizingMap som = somio.getSom();
        MapCell sampleMember = som.getCell(sampleMemberPosition.x, sampleMemberPosition.y);
        clusterMembers = getClusterMembers(sampleMember);
        reCalculate();
    }

    public int getCellCount()
    {
        return clusterMembers.size();
    }

    public int getSearchValueFrequency()
    {
        return frequency;
    }

    private void reCalculate()
    {
        if (clusterMembers.size() == 0 || selectedVariable == null)
        {
            return;
        }
        
        int freq = 0;
        for (MapCell cell : clusterMembers)
        {
            double v = cell.getAvgValue(selectedVariable);
            if (minSearchValue < v && v < maxSearchValue)
            {
                freq++;
            }
        }
        frequency = freq;
    }

    private List<MapCell> getClusterMembers(MapCell start)
    {
        SelfOrganizingMap som = somio.getSom();
        List<MapCell> visited = new ArrayList<>();
        List<MapCell> cluster = new ArrayList<>();
        List<MapCell> cells = new ArrayList<>();
        visited.add(start);
        cluster.add(start);
        addNeighbors(start, cells, visited);
        while (cells.size() > 0)
        {
            MapCell c = cells.remove(0);
            if (visited.contains(c))
            {
                continue;
            }
            visited.add(c);
            double dist = c.getMeanUnifiedDistance();
            if (0.0 < dist && dist < som.getClusterThreshold())
            {
                cluster.add(c);
                addNeighbors(c, cells, visited);
            }
        }
        return cluster;
    }

    private void addNeighbors(MapCell c, List<MapCell> cells, List<MapCell> visited)
    {
        for (int i = 0; i < MapCell.NEIGHBOR_COUNT; i++)
        {
            MapCell n = c.getNeighbor(i);
            if (n != null && !cells.contains(n) && !visited.contains(n))
            {
                cells.add(n);
            }
        }
    }

    public void setMinSearchValue(int value)
    {
        minSearchValue = value;
        reCalculate();
    }
    public void setMaxSearchValue(int value)
    {
        maxSearchValue = value;
        reCalculate();
    }
}
