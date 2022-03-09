package com.timpage.app;

import java.io.File;
import java.io.IOException;  

/**
 * Driver for the tab-converter application
 */
public class App {

    private String newFileName;

    /**
     * Constructor for the App object. Used as the driver for first parsing the input file and then converting it to tab.
     * @param filename the name of the file to be parsed and converted to tab
     */
    public App(String filename) {
        try {
            TabConverter tc = new TabConverter(filename);
            newFileName = tc.convertPartsToTab();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

    /**
     * Constructor for the App object. Used as the driver for first parsing the input file and then converting it to tab.
     * @param file the file to be parsed and converted to tab
     */
    public App(File file) {
        try {
            TabConverter tc = new TabConverter(file);
            newFileName = tc.convertPartsToTab();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

    /**
     * Gets the filename of the file which has been converted to tab
     * @return the new filename
     */
    public String getNewFileName() {
        return newFileName;
    }
}
