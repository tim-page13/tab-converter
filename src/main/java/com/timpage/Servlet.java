package com.timpage;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Servlet extends HttpServlet {
    public void processRequest(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out;
        try {
            out = response.getWriter();
            out.println("<html>");
            out.println("<h1>Hello all from servlet</h1>");
            out.println("<h1>Servlet newServlet at " + request.getContextPath() + "</h1>");
            String user = request.getParameter("filename");
            out.println("<h2> Welcome " + user + "</h2>");
            out.println("</html>");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
