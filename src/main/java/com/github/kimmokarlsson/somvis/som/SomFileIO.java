package com.github.kimmokarlsson.somvis.som;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.github.kimmokarlsson.somvis.util.ArgsParser;
import com.github.kimmokarlsson.somvis.util.Json;

/**
 * 
 */
public class SomFileIO
{
    private SelfOrganizingMap som;
    private SomJsonParser parser;
    private Json json;
    private List<SomChangeListener> listeners;

    /**
     * 
     * @param configFile SOM config file
     * @throws IOException on errors
     */
    public SomFileIO()
    {
        json = new Json();
        parser = new SomJsonParser();
        listeners = new ArrayList<>();
    }
    
    public SomFileIO config(File configFile) throws IOException
    {
        som = parser.parse(configFile);
        notifyListeners();
        return this;
    }

    private void notifyListeners()
    {
        for (SomChangeListener l : listeners)
        {
            l.somChanged();
        }
    }

    /**
     * 
     * @return
     */
    public SelfOrganizingMap getSom()
    {
        return som;
    }

    public SomFileIO loadMap(File dataFile) throws IOException
    {
        parser.load(som, dataFile);
        return this;
    }
    
    /**
     * 
     * @param dataFile training data file
     * @throws IOException on errors
     */
    public SomFileIO train(File dataFile) throws IOException
    {
        parser.train(som, dataFile);
        return this;
    }

    /**
     * @param outputFile output file
     * @throws IOException on errors
     */
    public void save(File outputFile) throws IOException
    {
        parser.write(som, outputFile);
    }

    /**
     * Generate random input data.
     * @param outputFile output file
     * @param lines 
     * @throws IOException on errors
     */
    public void generateRandomData(String outputFile, int lines) throws IOException
    {
        try (PrintWriter p = new PrintWriter(new FileWriter(outputFile)))
        {
            for (int i = 0; i < lines; i++)
            {
                p.println(json.toJson(generateRandomDataLine()));
            }
        }
        catch (Exception e)
        {
            throw new IOException(e);
        }
    }

    private Map<String,Object> generateRandomDataLine()
    {
        Random random = new Random();
        Map<String,Object> map = new LinkedHashMap<>();
        for (Variable var : som.getVariables())
        {
            Object val = null;
            switch (var.getType())
            {
                case DOUBLE: val = var.getMinValue() + (random.nextDouble() * (var.getMaxValue() - var.getMinValue())); break;
                case ENUM: 
                case INT: val = ((int) var.getMinValue()) + random.nextInt((int) (var.getMaxValue() - var.getMinValue())); break;
            }
            
            map.put(var.getName(), val);
        }
        return map;
    }

    /**
     * 
     * @param args
     */
    public static void main(String[] args)
    {
        ArgsParser params = null;
        try
        {
            params = new ArgsParser(args, 2);
        }
        catch(ParseException e)
        {
            e.printStackTrace();
            System.out.println("Usage: SomFileIO -c [config file] -t [training data file] -o [output file]");
        }

        if (params != null)
        {
            try
            {
                new SomFileIO().config(new File(params.getValue("-c"))).train(new File(params.getValue("-t"))).save(new File(params.getValue("-o")));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void addSomChangeListener(SomChangeListener l)
    {
        if (l != null && !listeners.contains(l))
        {
            listeners.add(l);
        }
    }

    public void removeSomChangeListener(SomChangeListener l)
    {
        listeners.remove(l);
    }
}
