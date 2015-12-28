package me.hqythu.wxydb.ui;

import me.hqythu.wxydb.manager.SystemManager;

import javax.swing.*;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by hqythu on 12/29/2015.
 */
public class StructureTab extends JPanel {

    private JPanel buttonPanel;
    private JTree structureTreeView;
    private DefaultTreeModel structureTreeModel;
    private JTextArea textArea;
    private DefaultMutableTreeNode root;

    public StructureTab() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createUi();
            }
        });
    }

    private void createUi() {
        setLayout(new BorderLayout());
        buttonPanel = new JPanel();
        add(buttonPanel, BorderLayout.NORTH);
        root = new DefaultMutableTreeNode();
        structureTreeModel = new DefaultTreeModel(root);
        structureTreeView = new JTree();
        textArea = new JTextArea();
        add(textArea, BorderLayout.SOUTH);
        add(structureTreeView, BorderLayout.CENTER);

        refreshDatabase();
    }

    void refreshDatabase() {
        String tables = SystemManager.getInstance().showTables();
        tables = tables.substring(1, tables.length() - 1);
        textArea.setText(tables);

        String[] tableNames = tables.split(", ");
        root = (DefaultMutableTreeNode) structureTreeModel.getRoot();
        root.removeAllChildren();
        for (String tableName : tableNames) {
            System.out.println(tableName);
            DefaultMutableTreeNode tableNode = new DefaultMutableTreeNode(tableName);
            String columnsResult = SystemManager.getInstance().showTableColumns(tableName);
            String[] columns = columnsResult.substring(1, columnsResult.length() - 1).split(",");
            for (String column : columns) {
                String columnName = column.split(" ")[0];
                System.out.println(columnName);
                tableNode.add(new DefaultMutableTreeNode(columnName));
            }
            root.add(tableNode);
        }

        structureTreeModel.reload(root);
        repaint();
    }

}
