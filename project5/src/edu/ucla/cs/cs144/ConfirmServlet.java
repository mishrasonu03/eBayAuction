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

public class ConfirmServlet extends HttpServlet implements Servlet 
{       
  public ConfirmServlet() {}

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
  {
  	try
  	{
	  	String queryType = request.getParameter("action");
		  	
	  	if (queryType.equals("confirmation"))
	  	{
	  		if (request.isSecure())
	  		{
	  			String id = request.getParameter("id");
		  		HttpSession session = request.getSession(true);

		  		HashMap<String, String[]> itemMap = (HashMap<String, String[]>)session.getAttribute("itemMap");
		
		  		if (itemMap.containsKey(id))
		  		{
		  			request.setAttribute("ID", id);
					request.setAttribute("cardNo", request.getParameter("cardNo"));
					request.setAttribute("Time", new Date());

		  			String[] values = (String[])itemMap.get(id);
			  		request.setAttribute("Name", values[0]);					
					request.setAttribute("BuyPrice", values[1]);

					request.getRequestDispatcher("/confirm.jsp").forward(request, response);
				}
			}
	  	}
  	}
  	catch (Exception e)
  	{
  		response.sendRedirect("error.html");
	}
  }

}