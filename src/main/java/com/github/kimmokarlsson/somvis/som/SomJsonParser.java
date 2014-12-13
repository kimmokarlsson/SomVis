package com.github.kimmokarlsson.somvis.som;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.kimmokarlsson.somvis.util.Json;

/**
 * Parses json files. 
 */
public class SomJsonParser
{
    private Json jsonUtil;
    
    public SomJsonParser()
    {
        jsonUtil = new Json();
    }

    /**
     * 
     * @param configFile
     * @param dataFile
     * @return
     * @throws IOException
     */
    public SelfOrganizingMap parse(File configFile) throws IOException
    {
        String contents = readFile(configFile);
        JsonNode json = null;
        try
        {
            json = jsonUtil.parse(contents);
        }
        catch (Exception e)
        {
            throw new IOException(e);
        }
        StringValueRegistry registry = new StringValueRegistry();
        List<Variable> variables = new ArrayList<>();
        Dimension dim = null;
        try
        {
            dim = parseConfigJson(json, variables, registry);
        }
        catch (Exception e)
        {
            throw new IOException(e);
        }
        
        SelfOrganizingMap som = new SelfOrganizingMap(dim.width, dim.height, variables, registry);
        som.setIterationCount(jsonUtil.getInt(json, "iterations", 1000));
        som.setInitialLearningRate(jsonUtil.getDouble(json, "initLearnRate", 0.1));
        som.setUseRandomSamples(jsonUtil.getBoolean(json, "useRandomSamples", true));
        som.setMultiThread(jsonUtil.getBoolean(json, "multiThread", true));
        return som;
    }

    /**
     * 
     * @param dataFile
     * @throws IOException
     */
    public void init(SelfOrganizingMap som, String dataFile) throws IOException
    {   
        int cols = som.getCols();
        List<Variable> variables = som.getVariables();
        double[] minValues = new double[variables.size()];
        Arrays.fill(minValues, Integer.MAX_VALUE);
        double[] maxValues = new double[variables.size()];
        Arrays.fill(maxValues, Integer.MIN_VALUE);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile))))
        {
            String line = null;
            int cell = 0;
            while ((line = br.readLine()) != null)
            {
                JsonNode lineJson = null;
                try
                {
                    lineJson = jsonUtil.parse(line);
                }
                catch (Exception e)
                {
                    throw new IOException(e);
                }
                
                double[] values = new double[variables.size()];
                for (Iterator<String> iter = lineJson.fieldNames(); iter.hasNext(); )
                {
                    String name = iter.next();
                    Variable var = findVariable(name, variables);
                    try
                    {
                        double v = var.getValue(jsonUtil, lineJson);
                        var.checkValue(v);
                        int index = variables.indexOf(var);
                        minValues[index] = Math.min(v, minValues[index]);
                        maxValues[index] = Math.max(v, maxValues[index]);
                        values[index] = v;
                    }
                    catch (Exception e)
                    {
                        throw new IOException(e);
                    }
                }
                
                som.initCell(cell % cols, cell / cols, values);
                cell++;
            }
        }
        
        for (Variable var : variables)
        {
            if (var.getMaxValue() < var.getMinValue())
            {
                var.setMaxValue(maxValues[variables.indexOf(var)]);
            }
            // TODO: check if unset
            if (var.getMinValue() == 0)
            {
                var.setMinValue(minValues[variables.indexOf(var)]);
            }
        }
    }

    /**
     * 
     * @param dataFile
     * @throws IOException
     */
    public void train(SelfOrganizingMap som, File dataFile) throws IOException
    {
        List<Vector> trainingData = new ArrayList<>();
        List<Variable> variables = som.getVariables();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile))))
        {
            String line = null;
            while ((line = br.readLine()) != null)
            {
                JsonNode lineJson = null;
                try
                {
                    lineJson = jsonUtil.parse(line);
                }
                catch (Exception e)
                {
                    throw new IOException(e);
                }
                
                double[] values = parseValues(variables, lineJson);
                
                trainingData.add(new Vector(values));
            }
        }
        
        double[] minValues = new double[variables.size()];
        Arrays.fill(minValues, Integer.MAX_VALUE);
        double[] maxValues = new double[variables.size()];
        Arrays.fill(maxValues, Integer.MIN_VALUE);
        for (Vector vec : trainingData) {
            double v[] = vec.get();
            for (int index = 0; index < v.length; index++) {
                minValues[index] = Math.min(v[index], minValues[index]);
                maxValues[index] = Math.max(v[index], maxValues[index]);
            }
        }
        for (Variable var : variables)
        {
            var.setMaxValue(maxValues[variables.indexOf(var)]);
            var.setMinValue(minValues[variables.indexOf(var)]);
        }
        
        som.initCells(trainingData);
        if (som.isMultiThread())
        {
            som.concurrentLearn(trainingData);
        }
        else
        {
            som.learn(trainingData);
        }
    }

    public double[] parseValues(List<Variable> variables, JsonNode lineJson) throws IOException
    {
        double[] values = new double[variables.size()];
        for (Iterator<String> iter = lineJson.fieldNames(); iter.hasNext(); )
        {
            String name = iter.next();
            Variable var = findVariable(name, variables);
            try
            {
                double v = var.getValue(jsonUtil, lineJson);
                var.checkValue(v);
                values[variables.indexOf(var)] = v;
            }
            catch (Exception e)
            {
                throw new IOException(e);
            }
        }
        return values;
    }

    private static Variable findVariable(String name, List<Variable> variables)
    {
        for (Variable v : variables)
        {
            if (v.getName().equals(name))
            {
                return v;
            }
        }
        return null;
    }
    
    private Dimension parseConfigJson(JsonNode json, List<Variable> variables, StringValueRegistry registry) throws IOException 
    {
        JsonNode varArr = json.get("vars");
        for (int i = 0; i < varArr.size(); i++)
        {
            JsonNode j = varArr.get(i);
            String type = j.get("type").asText();
            String name = j.get("name").asText();
            boolean ignored = jsonUtil.getBoolean(j, "ignored", false);
            Variable var = null;
            switch (type)
            {
                case "double":
                    var = new DoubleVariable(i, name, jsonUtil.getDouble(j, "minValue", 0.0), jsonUtil.getDouble(j, "maxValue", -1.0), jsonUtil.getDouble(j, "init", 0.0));
                    break;
                case "int":
                    var = new IntVariable(i, name, jsonUtil.getInt(j, "minValue", 0), jsonUtil.getInt(j, "maxValue", -1), jsonUtil.getInt(j, "init", 0));
                    break;
                case "enum":
                    String init = jsonUtil.getString(j, "init", null);
                    JsonNode arr = j.get("values");
                    List<String> list = new ArrayList<String>();
                    for (int k = 0; k < arr.size(); k++)
                    {
                        list.add(arr.get(k).asText());
                    }
                    var = new EnumVariable(i, name, list, init);
                    break;
                case "string":
                    var = new StringVariable(registry, i, name);
                    ignored = true;
                    break;
                default: throw new IOException("Invalid type: "+ type);
            }
            var.setIgnored(ignored);
            variables.add(var);
        }
        
        return new Dimension(json.get("cols").asInt(), json.get("rows").asInt());
    }

    private static String readFile(File configFile) throws IOException
    {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(configFile)));
        String line = null;
        while ((line = br.readLine()) != null)
        {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }

    public void load(SelfOrganizingMap som, File dataFile) throws IOException
    {
        List<Variable> variables = som.getVariables();
        double[] minValues = new double[variables.size()];
        Arrays.fill(minValues, Integer.MAX_VALUE);
        double[] maxValues = new double[variables.size()];
        Arrays.fill(maxValues, Integer.MIN_VALUE);
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile))))
        {
            int cell = 0;
            String line = null;
            while ((line = br.readLine()) != null)
            {
                JsonNode lineJson = null;
                try
                {
                    lineJson = jsonUtil.parse(line);
                }
                catch (Exception e)
                {
                    throw new IOException(e);
                }
                
                JsonNode dataJson = lineJson.get("data");
                if (dataJson == null || !dataJson.isArray())
                {
                    throw new IOException("data node");
                }
                JsonNode valueJson = lineJson.get("value");
                if (valueJson == null)
                {
                    throw new IOException("value node");
                }
                
                MapCell mapCell = som.getCell(cell % som.getCols(), cell / som.getCols());
                mapCell.reset();
                mapCell.setValue(new Vector(parseValues(som.getVariables(), valueJson)));
                for (int i = 0; i < dataJson.size(); i++)
                {
                    Vector vec = new Vector(parseValues(som.getVariables(), dataJson.get(i)));
                    mapCell.addData(vec);
                    double v[] = vec.get();
                    for (int index = 0; index < v.length; index++) {
                        minValues[index] = Math.min(v[index], minValues[index]);
                        maxValues[index] = Math.max(v[index], maxValues[index]);
                    }
                }
                cell++;
            }
        }
        
        for (Variable var : variables)
        {
            var.setMaxValue(maxValues[variables.indexOf(var)]);
            var.setMinValue(minValues[variables.indexOf(var)]);
        }
    }

    /**
     * 
     * @param som
     * @param outputFile
     */
    public void write(SelfOrganizingMap som, File outputFile) throws IOException
    {
        try (PrintWriter p = new PrintWriter(new FileWriter(outputFile)))
        {
            p.print(toJsonLines(som));
            p.flush();
        }
        catch (Exception e)
        {
            throw new IOException(e);
        }
    }

    private String toJsonLines(SelfOrganizingMap som)
    {
        StringBuffer sb = new StringBuffer();
        for (int j = 0; j < som.getRows(); j++)
        {
            for (int i = 0; i < som.getCols(); i++)
            {
                MapCell cell = som.getCell(i, j);
                sb.append(toJson(som, cell).toString());
                sb.append('\n');
            }
        }
        return sb.toString();
    }

    private JsonNode toJson(SelfOrganizingMap som, MapCell cell)
    {
        ObjectNode json = jsonUtil.newObject();
        {
            ObjectNode valueNode = json.putObject("value");
            double[] d = cell.getValue().get();
            int i = 0;
            for (Variable v : som.getVariables())
            {
                if (!v.isIgnored())
                {
                    valueNode.put(v.getName(), v.getStringValue(d[i++]));
                }
            }
        }
        ArrayNode arr = json.putArray("data");
        for (Vector vec : cell.getData())
        {
            ObjectNode dataNode = arr.addObject();
            double[] d = vec.get();
            int i = 0;
            for (Variable var : som.getVariables())
            {
                switch (var.getType())
                {
                    case INT:
                        dataNode.put(var.getName(), (int)d[i++]);
                        break;
                    case DOUBLE:
                        dataNode.put(var.getName(), d[i++]);
                        break;
                    case ENUM:
                        dataNode.put(var.getName(), var.getStringValue(d[i++]));
                        break;
                    case STRING:
                        dataNode.put(var.getName(), var.getStringValue(d[i++]));
                        break;
                    default:
                        throw new IllegalStateException("Invalid type");
                }
            }
        }
        return json;
    }
}
