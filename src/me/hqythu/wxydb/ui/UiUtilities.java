package me.hqythu.wxydb.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by hqythu on 12/29/2015.
 */
public class UiUtilities {
    /**
     *
     * @param parent
     * @return file path chosen
     */
    public static String chooseFile(Component parent) {
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(parent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getPath();
        } else {
            return null;
        }
    }
}
