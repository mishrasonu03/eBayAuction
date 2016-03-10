package edu.ucla.cs.cs144;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class BuyServlet extends HttpServlet implements Servlet {
       
  public BuyServlet() {}

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
  {
  	doGet(request, response);
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
  {
  	try
  	{
	  	String id = request.getParameter("id");
	  	HttpSession session = request.getSession(true);

	  	HashMap<String, String[]> itemMap = (HashMap<String, String[]>)session.getAttribute("itemMap");

	  	if (itemMap.containsKey(id)) 
	  	{
	  		request.setAttribute("ID", id);
	  	
	  		String[] namePrice = (String[])itemMap.get(id);
	  		request.setAttribute("Name", namePrice[0]);
			request.setAttribute("BuyPrice", namePrice[1]);
		
			request.getRequestDispatcher("/payment.jsp").forward(request, response);
	  	}
  	}
  	catch (Exception e)
  	{
  		response.sendRedirect("error.html");
	}
  }

}