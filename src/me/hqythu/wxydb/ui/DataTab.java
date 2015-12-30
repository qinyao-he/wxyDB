package me.hqythu.wxydb.ui;

import me.hqythu.wxydb.manager.QueryEngine;
import me.hqythu.wxydb.manager.SystemManager;
import me.hqythu.wxydb.object.Table;
import me.hqythu.wxydb.sql.ParseResult;
import me.hqythu.wxydb.sql.SQLParser;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by hqythu on 12/29/2015.
 */
public class DataTab extends JPanel {

    private JPanel controlPanel;
    private JComboBox<String> tableSelectComboBox;
    private JTable dataTable;
    private JScrollPane dataScroll;

    public DataTab() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createUi();
            }
        });
    }

    private void createUi() {
        setLayout(new BorderLayout());

        controlPanel = new JPanel();
        tableSelectComboBox = new JComboBox<String>();
        controlPanel.add(tableSelectComboBox);

        dataTable = new JTable();
        dataScroll = new JScrollPane(dataTable);

        tableSelectComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox<String> comboBox = (JComboBox)e.getSource();
                String tableName;
                if (comboBox.getSelectedItem() != null) {
                    tableName = comboBox.getSelectedItem().toString();
                } else {
                    return;
                }

                String sqlStr = "select * from " + tableName + ";";
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ParseResult parseResult = SQLParser.parse(sqlStr);
                        java.util.List<Object[]> resultList = new ArrayList<>();
                        try {
                            resultList = QueryEngine.getInstance().queryById(
                                    parseResult.selectOption, parseResult.where);
                        } catch (Exception e) {
                            Object[] tmp = new Object[1];
                            tmp[0] = e.getMessage();
                            resultList.add(tmp);
                        }
                        final java.util.List<Object[]> result = resultList;
                        TableModel model = new AbstractTableModel() {
                            @Override
                            public int getRowCount() {
                                return result.size();
                            }

                            @Override
                            public int getColumnCount() {
                                if (result.size() == 0) {
                                    return 0;
                                } else {
                                    return result.get(0).length;
                                }
                            }

                            @Override
                            public Object getValueAt(int rowIndex, int columnIndex) {
                                return result.get(rowIndex)[columnIndex];
                            }
                        };
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                dataTable.setModel(model);
                            }
                        });
                    }
                }).start();
            }
        });

        add(controlPanel, BorderLayout.NORTH);
        add(dataScroll, BorderLayout.CENTER);

        refreshDatabase();
    }

    public void refreshDatabase() {
        Map<String, Table> tables = SystemManager.getInstance().getTables();

        tableSelectComboBox.removeAllItems();

        if (tables != null) {
            for (Map.Entry<String, Table> entry : tables.entrySet()) {
                tableSelectComboBox.addItem(entry.getKey());
            }
        }
    }

}
