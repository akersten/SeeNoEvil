/*
 Project: SeeNoEvil
 File: SeeNoEvil.java (com.alexkersten.SeeNoEvil)
 Author: Alex Kersten
 */
package com.alexkersten.SeeNoEvil;

import com.alexkersten.SeeNoEvil.gui.LaunchFrame;
import javax.swing.UIManager;

/**
 *
 * @author Alex Kersten
 */
public class SeeNoEvil {

    public static final String BRANDING = "SeeNoEvil";

    public static final String VERSION = "0.0.0.0 Dev";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Couldn't set the system look and feel.");
        }


        new LaunchFrame().setVisible(true);
    }
}
