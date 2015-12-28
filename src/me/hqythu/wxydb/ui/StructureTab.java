package me.hqythu.wxydb.ui;

import me.hqythu.wxydb.manager.SystemManager;

import javax.swing.*;
import java.awt.*;

/**
 * Created by hqythu on 12/29/2015.
 */
public class StructureTab extends JPanel {

    private JPanel buttonPanel;
    private JTree structureTreeView;
    private JTextArea textArea;

    public StructureTab() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createUi();
            }
        });
    }

    private void createUi() {
        buttonPanel = new JPanel();
        add(buttonPanel, BorderLayout.NORTH);
        structureTreeView = new JTree();
        textArea = new JTextArea();
        add(textArea, BorderLayout.CENTER);

        refreshDatabase();
    }

    void refreshDatabase() {
        String tables = SystemManager.getInstance().showTables();
        textArea.setText(tables);
    }

}
