package com.timpage;

// Import required java libraries
import java.io.*;
import java.util.*;
 
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.timpage.app.App;
import com.timpage.app.TabConverter;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.output.*;

public class UploadServlet extends HttpServlet {
   
   private boolean isMultipart;
   private String filePath;
   private String uploadPath;
   private int maxFileSize = 2000 * 1024;
   private int maxMemSize = 50 * 1024;
   private File file ;

    public void init() {
        // Get the file location where it would be stored.
        String rootPath = System.getProperty("catalina.home");
        uploadPath = getServletContext().getInitParameter("file-upload");
        filePath = rootPath + File.separator + uploadPath; 
        System.out.println(filePath);
    }
   
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, java.io.IOException {

        System.out.println("upload servlet is running");
    
        // Check that we have a file upload request
        isMultipart = ServletFileUpload.isMultipartContent(request);
        response.setContentType("text/html");
        java.io.PrintWriter out = response.getWriter( );
    
        if( !isMultipart ) {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet upload</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<p>No file uploaded</p>"); 
            out.println("</body>");
            out.println("</html>");
            return;
        }
    
        DiskFileItemFactory factory = new DiskFileItemFactory();
    
        // maximum size that will be stored in memory
        factory.setSizeThreshold(maxMemSize);
    
        // Location to save data that is larger than maxMemSize.
        factory.setRepository(new File("../temp"));

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);
    
        // maximum file size to be uploaded.
        upload.setSizeMax( maxFileSize );

        try { 
            // Parse the request to get file items.
            List fileItems = upload.parseRequest(request);
        
            // Process the uploaded file items
            Iterator i = fileItems.iterator();

            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet upload</title>");  
            out.println("</head>");
            out.println("<body>");
    
            while ( i.hasNext () ) {
                FileItem fi = (FileItem)i.next();
                if ( !fi.isFormField () ) {
                    // Get the uploaded file parameters
                    String fieldName = fi.getFieldName();
                    String fileName = fi.getName();
                    String contentType = fi.getContentType();
                    boolean isInMemory = fi.isInMemory();
                    long sizeInBytes = fi.getSize();
                    
                    // Write the file
                    // if( fileName.lastIndexOf("/") >= 0 ) {
                        // file = new File( filePath + fileName.substring( fileName.lastIndexOf("/"))) ;
                        // System.out.println("made new " + file.getAbsolutePath() + " " + isInMemory);
                    // } else {
                        file = new File( filePath + fileName.substring(fileName.lastIndexOf("/")+1)) ;
                        System.out.println("made new 1 " + file.getAbsolutePath() + " " + isInMemory);
                    // }
                    fi.write( file ) ;
                    out.println("Uploaded Filename: " + fileName + "<br>");
                }
            }
            out.println("</body>");
            out.println("</html>");
            //todo figure out where to redirect in the case where there are multiple files uploaded
            App runTC = new App(file);
            String fileName = runTC.getNewFileName().substring(runTC.getNewFileName().lastIndexOf("/")+1, runTC.getNewFileName().length());
            String newFilePath = "/data/" + fileName;
            request.setAttribute("filename", newFilePath);
            request.getRequestDispatcher("/WEB-INF/pages/showtab.jsp").forward(request, response);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, java.io.IOException {

        throw new ServletException("GET method used with " +
            getClass( ).getName( )+": POST method required.");
    }
}
