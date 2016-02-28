package edu.ucla.cs.cs144;

import java.io.IOException;
import java.net.HttpURLConnection;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.*;
import java.io.*;

public class ProxyServlet extends HttpServlet implements Servlet {
       
    public ProxyServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String query = request.getParameter("q");
        URL url = new URL("http://google.com/complete/search?output=toolbar&q=" + URLEncoder.encode(query, "UTF-8"));
        URLConnection conn = url.openConnection();
        response.setContentType("text/xml"); 

		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuffer buf = new StringBuffer();
		
		while ((inputLine = reader.readLine()) != null) 
		{
			buf.append(inputLine);
		}
				
		PrintWriter writer = response.getWriter();
        writer.println(buf.toString());
    }
}
