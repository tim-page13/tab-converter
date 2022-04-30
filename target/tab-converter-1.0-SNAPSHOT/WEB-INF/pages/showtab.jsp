<!DOCTYPE html>
<html>
    <head>
        <title>tabConverter - Display Page</title>
        <meta http-equiv="Content-Type" content="text/html; charset-UTF-8">
        <%-- <link rel="stylesheet" href="style.css"> --%>
    </head>
    <body style="text-align: center">
        <h2>Your converted tab:</h2>
        <form action="./UploadServlet" method="POST" enctype="multipart/form-data">
          <input type="file" id="myFile" name="file" size="50">
          <input type="submit" value="Click here">
        </form>

        <%  String filename = (String) request.getAttribute("filename");
        %>

        <div id="osmdContainer"/>
        <script type="text/javascript">
            var filename = '<% out.print(filename); %>';
        </script>
        <script src="resources/js/opensheetmusicdisplay.min.js"></script>
        <script>
        var osmd = new opensheetmusicdisplay.OpenSheetMusicDisplay("osmdContainer", {
            autoResize: true, // just an example for an option, no option is necessary.
            backend: "svg",
            drawTitle: true,
            // put further options here
        });
        osmd.load(filename)
        .then(
            function() {
                osmd.render();
            }
        );
        console.log(filename);
    </script>
    </body>
</html>
