package me.hqythu.wxydb.ui;

import me.hqythu.wxydb.manager.QueryEngine;
import me.hqythu.wxydb.sql.ParseResult;
import me.hqythu.wxydb.sql.SQLParser;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

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

        sqlExeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sqlStr = sqlTextField.getText();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ParseResult parseResult = SQLParser.parse(sqlStr);
                        java.util.List<Object[]> resultList = new ArrayList<>();
                        try {
                            if (parseResult.type == ParseResult.OrderType.SELECT) {
                                resultList = QueryEngine.getInstance().query(
                                        parseResult.selectOption, parseResult.where);
                            } else {
                                Object[] tmp = new Object[1];
                                tmp[0] = parseResult.execute();
                                resultList.add(tmp);
                            }
                        } catch (Exception e) {
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
                                executeResultTable.setModel(model);
                            }
                        });
                    }
                }).start();
            }
        });
    }

}
