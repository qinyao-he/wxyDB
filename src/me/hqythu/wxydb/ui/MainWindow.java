package me.hqythu.wxydb.ui;

import me.hqythu.wxydb.manager.SystemManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by hqythu on 12/29/2015.
 */
public class MainWindow extends JFrame {

    private JFrame mainwindow;
    private JToolBar toolBar;
    private JButton newDatabaseButton;
    private JButton openDatabaseButton;
    private JLabel label;

    private JTabbedPane tabbedPane;
    private StructureTab structureTab;
    private DataTab dataTab;
    private ExeSQLTab exeSQLTab;

    private SystemManager dbSystemManager;

    public MainWindow() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createUi();
            }
        });
    }

    private void createUi() {
        mainwindow = this;
        setTitle("wxyDB");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(640, 480);
        setLayout(new BorderLayout());

        label = new JLabel("Hello World");

        createToolbar();
        getContentPane().add(toolBar, BorderLayout.NORTH);

        createTabView();
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
    }

    private void createToolbar() {
        toolBar = new JToolBar();

        newDatabaseButton = new JButton("New Database");
        newDatabaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filePath = UiUtilities.chooseFile(mainwindow);
                label.setText(filePath);
                try {
                    SystemManager.getInstance().createDatabase(filePath);
                } catch (Exception exception) {
                    label.setText("create database fail");
                }
                refreshDatabase();
            }
        });
        openDatabaseButton = new JButton("Open Database");
        openDatabaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filePath = UiUtilities.chooseFile(mainwindow);
                label.setText(filePath);
                SystemManager.getInstance().useDatabase(filePath);
                refreshDatabase();
            }
        });

        toolBar.add(newDatabaseButton);
        toolBar.add(openDatabaseButton);
    }

    private void createTabView() {
        structureTab = new StructureTab();
        dataTab = new DataTab();
        exeSQLTab = new ExeSQLTab();

        tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Database Structure", structureTab);
        tabbedPane.addTab("Browser Data", dataTab);
        tabbedPane.addTab("Execute SQL", exeSQLTab);
    }

    private void refreshDatabase() {
        structureTab.refreshDatabase();
    }

}
