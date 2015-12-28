package me.hqythu.wxydb.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by hqythu on 12/29/2015.
 */
public class ExeSQLTab extends JPanel {

    private JTextField sqlTextField;
    private JButton sqlExeButton;
    private JTable executeResultTable;
    private JScrollPane resultScroll;
    private JPanel controlPanel;

    public ExeSQLTab() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createUi();
            }
        });
    }

    private void createUi() {
        sqlTextField = new JTextField();
        sqlTextField.setPreferredSize(new Dimension(400, 24));
        sqlExeButton = new JButton("Execute");
        executeResultTable = new JTable();
        resultScroll = new JScrollPane(executeResultTable);

        controlPanel = new JPanel();
        controlPanel.add(sqlTextField);
        controlPanel.add(sqlExeButton);

        setLayout(new BorderLayout());
        add(controlPanel, BorderLayout.NORTH);
        add(resultScroll, BorderLayout.CENTER);
    }

}
