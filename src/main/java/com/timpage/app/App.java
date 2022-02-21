package com.timpage.app;

import java.io.File;
import java.io.IOException;  
// import javax.servlet.ServletException;  
// import javax.servlet.annotation.WebServlet;  
// import javax.servlet.http.HttpServlet;  
// import javax.servlet.http.HttpServletRequest;  
// import javax.servlet.http.HttpServletResponse; 

import com.timpage.musicXMLparserDH.Note;
import com.timpage.musicXMLparserDH.parser.musicXMLparserDH;

public class App {

    private String newFileName;

    public App(String filename) {
        try {

            System.out.println("made it to old App " + filename);
            
            TabConverter tc = new TabConverter(filename);
            
            System.out.println("past TC");
            
            newFileName = tc.convertPartsToTab();

            System.out.println("new filename: " + newFileName);

        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
    public App(File file) {
        try {

            System.out.println("made it to App(File) " + file.getName());
            System.out.println("made it to App(File) " + file.getAbsolutePath());
            
            // TabConverter tc = new TabConverter(file.getAbsolutePath());
            TabConverter tc = new TabConverter(file);

            // musicXMLparserDH parser = new musicXMLparserDH(file);
            // parser.parseMusicXML();
            
            System.out.println("past TC");
            
            newFileName = tc.convertPartsToTab();

            // System.out.println("new filename: " + newFile);

        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

    public String getNewFileName() {
        return newFileName;
    }
}
