package com.github.kimmokarlsson.somvis.som.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.github.kimmokarlsson.somvis.som.CellWeightVariable;
import com.github.kimmokarlsson.somvis.som.SelfOrganizingMap;
import com.github.kimmokarlsson.somvis.som.SomFileIO;

/**
 * Main window of the GUI.
 */
public class SomVisualizer implements MenuItemSelectionListener
{
    private SomFileIO somio;
    private JFrame frame;
    private File currentDirectory;
    private ClusterInfoPanel clusterPanel;
    private HexCanvas canvas;
    private ColorizationPanel colorizationPanel;
    
    public SomVisualizer(File configFile, File dataFile) throws IOException
    {
        somio = new SomFileIO();
        if (configFile != null)
        {
            somio.config(configFile);
        }
        if (dataFile != null && somio != null)
        {
            somio.loadMap(dataFile);
        }
        if (configFile != null)
        {
            currentDirectory = configFile.getParentFile();
        }
        else
        {
            currentDirectory = new File(".");
        }
    }

    public void openDialog()
    {
        frame = new JFrame("SOM Visualizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(100, 100, 900, 800);
        Container root = frame.getContentPane();
        root.setLayout(new BorderLayout());

        SelfOrganizingMap som = somio.getSom();
        CellInfoPanel infoPanel = new CellInfoPanel(somio);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        CellWeightVariable.INSTANCE.setMaxValue(som == null ? 1 : som.getMaxCellWeight());

        canvas = new HexCanvas( somio );
        createMenu();

        colorizationPanel = new ColorizationPanel(frame, canvas, somio);
        clusterPanel = new ClusterInfoPanel(somio);
        canvas.addCellSelectionListener(clusterPanel);
        canvas.addCellSelectionListener(infoPanel);
        JSplitPane bottomPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(clusterPanel), new JPanel());
        JSplitPane midPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(colorizationPanel), bottomPane);
        JSplitPane leftPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(infoPanel), midPane);
        JSplitPane main = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, new JScrollPane(canvas));
        main.setOneTouchExpandable(true);
        
        root.add(main, BorderLayout.CENTER);
        frame.getRootPane().setDoubleBuffered(true);

        frame.setVisible(true);
        main.setDividerLocation(0.33);
        leftPane.setDividerLocation(0.25);
        midPane.setDividerLocation(0.35);
        bottomPane.setDividerLocation(0.80);
        canvas.init();
        clustering();
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                canvas.zoomToFit();
                canvas.getParent().repaint();
            }});
    }

    private void createMenu()
    {
        JMenuBar mb = new JMenuBar();
        frame.setJMenuBar(mb);
        
        JMenu fileMenu = mb.add(new JMenu("File"));
        JMenu viewMenu = mb.add(new JMenu("View"));
        fileMenu.add(createMenuItem("Open...", KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK, this, SomVisualizerMenuItems.ITEM_CODE_OPEN));
        fileMenu.add(createMenuItem("Exit", KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK, this, SomVisualizerMenuItems.ITEM_CODE_EXIT));
        viewMenu.add(createMenuItem("Zoom In", KeyEvent.VK_PLUS, KeyEvent.CTRL_DOWN_MASK, canvas, HexCanvasMenuItems.ITEM_CODE_ZOOM_IN));
        viewMenu.add(createMenuItem("Zoom Out", KeyEvent.VK_MINUS, KeyEvent.CTRL_DOWN_MASK, canvas, HexCanvasMenuItems.ITEM_CODE_ZOOM_OUT));
        viewMenu.add(createMenuItem("Reset Zoom", KeyEvent.VK_0, KeyEvent.CTRL_DOWN_MASK, canvas, HexCanvasMenuItems.ITEM_CODE_ZOOM_RESET));
        viewMenu.add(createMenuItem("Zoom To Fit", KeyEvent.VK_EQUALS, KeyEvent.CTRL_DOWN_MASK, canvas, HexCanvasMenuItems.ITEM_CODE_ZOOM_FIT));
    }

    private JMenuItem createMenuItem(String title, int keyCode, int modifiers, final MenuItemSelectionListener lis, final int itemCode)
    {
        JMenuItem item = new JMenuItem(new AbstractAction(title) {
            @Override
            public void actionPerformed(ActionEvent evt) {
                lis.itemSelected(itemCode);
            }});
        item.setAccelerator(KeyStroke.getKeyStroke(keyCode, modifiers));
        return item;
    }

    @Override
    public void itemSelected(int itemCode)
    {
        if (itemCode == SomVisualizerMenuItems.ITEM_CODE_EXIT)
        {
            System.exit(0);
        }
        else if (itemCode == SomVisualizerMenuItems.ITEM_CODE_OPEN)
        {
            JFileChooser dialog = new JFileChooser(currentDirectory);
            dialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
            File configFile = null;
            if (dialog.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION)
            {
                configFile = dialog.getSelectedFile();
            }
            if (configFile != null)
            {
                loadMap(configFile);
            }
        }
    }

    private void loadMap(File configFile)
    {
        File dataFile = new File(configFile.getParentFile(), configFile.getName().replace("config.json", "data.jsonrows"));
        try
        {
            somio.config(configFile).loadMap(dataFile);
            setSom(somio.getSom());
        }
        catch (IOException e)
        {
            System.out.println("Error loading map: "+ configFile.getAbsolutePath() + ", data: "+ dataFile.getAbsolutePath());
            e.printStackTrace();
        }
    }

    private void setSom(SelfOrganizingMap som)
    {
        CellWeightVariable.INSTANCE.setMaxValue(som.getMaxCellWeight());
        canvas.repaint();
        colorizationPanel.reset();
        clusterPanel.reset();
        clustering();
        canvas.zoomToFit();
    }

    private void clustering()
    {
        if (somio.getSom() != null)
        {
            System.out.println("Clustering start.");
            long startTime = System.currentTimeMillis();
            somio.getSom().getUnifiedDistanceMatrix(true, true);
            System.out.println("Clustering done in " + (System.currentTimeMillis()-startTime) + " ms.");
        }
    }

    public static void main(String[] args)
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        try
        {
            File configFile = null;
            File dataFile = null;
            if (args.length >= 2)
            {
                configFile = new File(args[0]);
                dataFile = new File(args[1]);
                if (!configFile.exists())
                {
                    throw new FileNotFoundException(configFile.getAbsolutePath());
                }
                if (!dataFile.exists())
                {
                    throw new FileNotFoundException(dataFile.getAbsolutePath());
                }
            }
            new SomVisualizer(configFile, dataFile).openDialog();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
