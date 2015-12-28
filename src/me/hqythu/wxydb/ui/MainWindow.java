package me.hqythu.wxydb.ui;

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
        getContentPane().add(label);

        createToolbar();
        getContentPane().add(toolBar, BorderLayout.NORTH);
    }

    private void createToolbar() {
        toolBar = new JToolBar();

        newDatabaseButton = new JButton("New Database");
        newDatabaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filePath = UiUtilities.chooseFile(mainwindow);
                label.setText(filePath);
            }
        });
        openDatabaseButton = new JButton("Open Database");
        openDatabaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filePath = UiUtilities.chooseFile(mainwindow);
                label.setText(filePath);
            }
        });

        toolBar.add(newDatabaseButton);
        toolBar.add(openDatabaseButton);
    }

}
