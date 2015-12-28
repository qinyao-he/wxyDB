package me.hqythu.wxydb.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by hqythu on 12/29/2015.
 */
public class MainWindow extends JFrame {

    private JToolBar toolBar;
    private JButton newDatabaseButton;
    private JButton openDatabaseButton;

    public MainWindow() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createUi();
            }
        });
    }

    private void createUi() {
        JFrame frame = new JFrame("wxyDB");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640, 480);
        frame.setLayout(new BorderLayout());

        JLabel label = new JLabel("Hello World");
        frame.getContentPane().add(label);

        createToolbar();
        frame.getContentPane().add(toolBar, BorderLayout.NORTH);

//        frame.pack();
        frame.setVisible(true);
    }

    private void createToolbar() {
        toolBar = new JToolBar();

        newDatabaseButton = new JButton("New Database");
        newDatabaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        openDatabaseButton = new JButton("Open Database");
        openDatabaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        toolBar.add(newDatabaseButton);
        toolBar.add(openDatabaseButton);
    }

}
