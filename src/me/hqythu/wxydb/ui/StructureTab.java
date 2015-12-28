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
    private JScrollPane treeScroll;
    private DefaultTreeModel structureTreeModel;
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
        root = new DefaultMutableTreeNode("Database");
        structureTreeModel = new DefaultTreeModel(root);
        structureTreeView = new JTree(structureTreeModel);
        treeScroll = new JScrollPane(structureTreeView);
        add(treeScroll, BorderLayout.CENTER);

        refreshDatabase();
    }

    void refreshDatabase() {
        String tables = SystemManager.getInstance().showTables();
        if (tables.equals("")) {
            return;
        } else {
            tables = tables.substring(1, tables.length() - 1);
        }

        String[] tableNames = tables.split(", ");
        root = (DefaultMutableTreeNode) structureTreeModel.getRoot();
        root.removeAllChildren();
        for (String tableName : tableNames) {
            DefaultMutableTreeNode tableNode = new DefaultMutableTreeNode(tableName);
            String columnsResult = SystemManager.getInstance().showTableColumns(tableName);
            String[] columns = columnsResult.substring(1, columnsResult.length() - 1).split(",");
            for (String column : columns) {
                String columnName = column.split(" ")[0];
                tableNode.add(new DefaultMutableTreeNode(columnName));
            }
            root.add(tableNode);
        }

        structureTreeModel.reload(root);
    }

}
