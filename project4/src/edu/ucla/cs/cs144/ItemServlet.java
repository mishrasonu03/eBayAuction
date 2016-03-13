package edu.ucla.cs.cs144;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.ucla.cs.cs144.AuctionSearchClient;
import org.apache.axis2.AxisFault;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import java.io.StringReader;

public class ItemServlet extends HttpServlet implements Servlet {
       
    public ItemServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        try{
            String searchID = request.getParameter("id");
            if (searchID.isEmpty()) {
                response.sendRedirect("getItem.html");
                return;
            }
			String query = request.getParameter("id");
            String result = AuctionSearchClient.getXMLDataForItemId(query);
            String id = "";
			String name = "";
            String[] categories;
			String currently = ""; 
			String buyPrice = ""; 
			String firstBid = ""; 
			String numOfBids = "";
            String[] bidderId;
            String[] bidderRating;
            String[] bidderLocation;
			String[] bidderCountry;      
            String[] bidTime;
			String[] bidAmount;            
			String started = "";
            String ends = "";
			String sellerId = "";
			String sellerRating = ""; 
			String description = "";

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    		DocumentBuilder builder = factory.newDocumentBuilder();
   			InputSource is = new InputSource(new StringReader(result));
    		Document doc = builder.parse(is);

    		name = doc.getElementsByTagName("Name").item(0).getTextContent();
    		currently = doc.getElementsByTagName("Currently").item(0).getTextContent();
    		
    		if (doc.getElementsByTagName("Buy_Price").getLength() != 0) {
    			buyPrice = doc.getElementsByTagName("Buy_Price").item(0).getTextContent();
    		}
    		
    		firstBid = doc.getElementsByTagName("First_Bid").item(0).getTextContent();
    		numOfBids = doc.getElementsByTagName("Number_of_Bids").item(0).getTextContent();
    		started = doc.getElementsByTagName("Started").item(0).getTextContent();
            ends = doc.getElementsByTagName("Ends").item(0).getTextContent();
            
    		NodeList sellers = doc.getElementsByTagName("Seller");
    		Node seller = sellers.item(0);
    		Element sellerElement = (Element)seller;
    		sellerId = sellerElement.getAttribute("UserID");
    		sellerRating = sellerElement.getAttribute("Rating");
    		description = doc.getElementsByTagName("Description").item(0).getTextContent();

    		NodeList mCategories = doc.getElementsByTagName("Category");

    		categories = new String[mCategories.getLength()];

    		for (int i = 0; i < mCategories.getLength(); i++){
    			categories[i] = mCategories.item(i).getTextContent();
    		}

    		NodeList bidders = doc.getElementsByTagName("Bidder");
    		bidderId = new String[bidders.getLength()];
    		bidderRating = new String[bidders.getLength()];
            Node bid;
    		Element bidderElement;
    		for (int i = 0; i < bidders.getLength(); i++) {
    			bid = bidders.item(i);
    			bidderElement = (Element) bid;
    			bidderId[i] = bidderElement.getAttribute("UserID");
    			bidderRating[i] = bidderElement.getAttribute("Rating");
    		}

    		NodeList locations = doc.getElementsByTagName("Location");
    		bidderLocation = new String[locations.getLength()];
    		for (int i = 0; i < locations.getLength(); i++) {
    			bidderLocation[i] = locations.item(i).getTextContent();
    		}

    		NodeList countries = doc.getElementsByTagName("Country");
    		bidderCountry = new String[countries.getLength()];
    		for (int i = 0; i < countries.getLength(); i++) {
    			bidderCountry[i] = countries.item(i).getTextContent();
    		}

    		NodeList times = doc.getElementsByTagName("Time");
    		bidTime = new String[times.getLength()];
    		for (int i = 0; i < times.getLength(); i++) {
    			bidTime[i] = times.item(i).getTextContent();
    		}

    		NodeList amounts = doc.getElementsByTagName("Amount");
    		bidAmount = new String[amounts.getLength()];
    		for (int i = 0; i < amounts.getLength(); i++) {
    			bidAmount[i] = amounts.item(i).getTextContent();
    		}

            request.setAttribute("ItemID", query);
    		request.setAttribute("Name", name);
			request.setAttribute("Currently", currently);
			request.setAttribute("Buy_Price", buyPrice);
			request.setAttribute("First_Bid", firstBid);
			request.setAttribute("Number_of_Bids", numOfBids);
			request.setAttribute("Started", started);
            request.setAttribute("Ends", ends);
			request.setAttribute("Seller_ID", sellerId);
			request.setAttribute("Seller_Rating", sellerRating);
			request.setAttribute("Description", description);
			request.setAttribute("Categories", categories);
			request.setAttribute("Bidder_ID", bidderId);
			request.setAttribute("Bidder_Rating", bidderRating);
			request.setAttribute("Bidder_Location", bidderLocation);
			request.setAttribute("Bidder_Country", bidderCountry);
			request.setAttribute("BidTime", bidTime);
			request.setAttribute("BidAmount", bidAmount);
    		request.getRequestDispatcher("/getItem.jsp").forward(request, response);

    	}
    	catch (Exception e) {
            response.sendRedirect("error.html");
        }
    }
}