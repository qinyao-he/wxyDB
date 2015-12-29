package me.hqythu.wxydb.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by hqythu on 12/29/2015.
 */
public class DataTab extends JPanel {

    private JPanel controlPanel;
    private JComboBox tableSelectComboBox;
    private DefaultComboBoxModel tableSelectModel;

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
        tableSelectModel = new DefaultComboBoxModel();
        tableSelectComboBox = new JComboBox(tableSelectModel);
        controlPanel.add(tableSelectComboBox);

        add(controlPanel, BorderLayout.NORTH);
    }

    public void refreshDatabase() {

    }

}
