**tabConverter**


About the codebase:
- The project uses a Maven webapp file structure.
  - Web files can be found in src>main>webapp (with the exception of the Java Servlet which handles the communication between the html and internal Java - see below). Here you will find the starting webpage, sheet music visualiser javaserver page, and OSMD library.
  - Internal Java files can be found in src>main>java>come>timpage. Here you will find musicXMLparserDH.java and Note.java which are modified versions of Dorien Herremans' code. All other files here are purely written by us. The flow of the code can be followed starting from where the file is received by the servlet in UploadServlet.java, or the step after where the main Java operation begins in app>App.java.
  - Generated Class files can be found in target.


Running instructions (this is not the only way to run the code, but is the way found to be most convenient during development):
- Open the tab-converter folder in Visual Studio Code
- Install the required extensions:
  - redhat.vscode-rsp-ui
  - redhat.vscode-community-server-connector
- Set up the server
  - In Explorer view, click 'Create new server...' from the Servers panel
  - Click 'Yes', download server
  - Select 'Apache Tomcat 9.0.41' and agree to the license conditions
  - Right-click on Community Server Connector > apache-tomcat-9.0.41 from within the Servers panel then click add deployment. 
  - Navigate to tab-converter>target>tab-converter-1.0-SNAPSHOT.war and say 'No' to editing optional deployment parameters.
  - Right-click on Community Server Connector > apache-tomcat-9.0.41 from within the Servers panel again and click 'Publish Server (Full)'
  - Right-click on Community Server Connector > apache-tomcat-9.0.41 from within the Servers panel again and click 'Start Server'
  - In a web browser, go to 'http://localhost:8080/tab-converter-1.0-SNAPSHOT/' to view the webpage
