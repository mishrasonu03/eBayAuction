package edu.ucla.cs.cs144;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.io.StringWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.lucene.document.Document;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import edu.ucla.cs.cs144.DbManager;
import edu.ucla.cs.cs144.SearchRegion;
import edu.ucla.cs.cs144.SearchResult;

public class AuctionSearch implements IAuctionSearch {

	/* 
         * You will probably have to use JDBC to access MySQL data
         * Lucene IndexSearcher class to lookup Lucene index.
         * Read the corresponding tutorial to learn about how to use these.
         *
	 * You may create helper functions or classes to simplify writing these
	 * methods. Make sure that your helper functions are not public,
         * so that they are not exposed to outside of this class.
         *
         * Any new classes that you create should be part of
         * edu.ucla.cs.cs144 package and their source files should be
         * placed at src/edu/ucla/cs/cs144.
         *
         */
	
	public SearchResult[] basicSearch(String query, int numResultsToSkip, 
			int numResultsToReturn) {
        SearchResult[] results = null;
        try{            
            IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File("/var/lib/lucene/index1"))));
            QueryParser parser = new QueryParser("Union", new StandardAnalyzer());
            Query mQuery = parser.parse(query);
            TopDocs tDocs = searcher.search(mQuery, numResultsToSkip + numResultsToReturn);
            
            ScoreDoc[] hits = tDocs.scoreDocs;
            int maxLength;
            if(numResultsToReturn < hits.length - numResultsToSkip){
                results = new SearchResult[numResultsToReturn];
                maxLength = numResultsToReturn + numResultsToSkip;
            }
            else{
                results = new SearchResult[hits.length - numResultsToSkip];
                maxLength = hits.length;
            }
            int resIndex = 0;
            for(int i = numResultsToSkip; i < maxLength; i++){
                Document doc = searcher.doc(hits[i].doc);
                results[resIndex] = new SearchResult(doc.get("ItemID"), doc.get("Name"));
                resIndex++;
            }
            
        }
        catch(Exception e){
            System.out.println(e);
        }
		return results;
	}
    
	public SearchResult[] spatialSearch(String query, SearchRegion region,
        int numResultsToSkip, int numResultsToReturn) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet mItems = null;
        String coord = "";
        String[] subUnits = null;
        Double lat = 0.0;
        Double longi = 0.0;
        int curIndex = 0;
        int querySize = numResultsToReturn;
        ArrayList<SearchResult> finalResults = new ArrayList<SearchResult>();
		SearchResult[] basicResults = basicSearch(query, numResultsToSkip, numResultsToReturn);
        try {
            conn = DbManager.getConnection(true);
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        try{
        while(finalResults.size() < numResultsToReturn){
            //Go through each basic search result and find if coordinate in range
            while(curIndex < basicResults.length){
                stmt = conn.createStatement();
                mItems = stmt.executeQuery("SELECT ItemID, astext(LatLong) FROM SpatialIndex WHERE ItemID=" + basicResults[curIndex].getItemId());
                if(mItems.next()){
                    coord = mItems.getString("astext(LatLong)");
                    //COORD IS FORMATTED POINT(41.385256 -71.66813)
                    //
                    coord = coord.substring(6,coord.length()-1);
                    subUnits = coord.split(" ");
                    lat = Double.parseDouble(subUnits[0]);
                    longi = Double.parseDouble(subUnits[1]); 
                    
                    if(lat >= region.getLx() && lat <= region.getRx() && longi >= region.getLy() && longi <= region.getRy()){
                        finalResults.add(basicResults[curIndex]);
                        //System.out.println(coord);
                        if(finalResults.size() == numResultsToReturn)
                            break;
                    }
                }
                curIndex++;                
                mItems.close();

            }
            //If there are no more basic search results or num of searches reached, end
            if(basicResults.length < querySize || finalResults.size() == numResultsToReturn)
                break;
            //exponentially increase query size
            querySize*=2;
            basicResults = basicSearch(query, numResultsToSkip, querySize);
        }
        }
        catch(Exception e){
            System.out.println(e);
        }
        try {
            conn.close();
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        
		return finalResults.toArray(new SearchResult[finalResults.size()]);
	}

	public String getXMLDataForItemId(String itemId) {
        Connection conn = null;
        Statement itemsStmt = null;
        Statement categoryStmt = null;
        Statement currStmt = null;
        Statement numBidsStmt = null;
        Statement bidsStmt = null;
        Statement bidStmt = null;
        Statement sellerStmt = null;
        
        
        ResultSet itemRes = null;
        ResultSet categoryRes = null;
        ResultSet currRes = null;
        ResultSet numBidsRes = null;
        ResultSet bidsRes = null;
        ResultSet bidRes = null;
        ResultSet sellerRes = null;

        Element eleItem = null;
        Element eleName = null;
        Element eleCat = null;
        Element eleCurrent = null;
        Element eleBuyP = null;
        Element eleFirstBid = null;
        Element eleNumBids = null;
        Element eleBids = null;
        Element eleBid = null;
        Element eleBidder = null;
        Element eleTime = null;
        Element eleAmount = null;
        Element eleLoc = null;
        Element eleCountry = null;
        Element eleStarted = null;
        Element eleEnds = null;
        Element eleSeller = null;
        Element eleDescription = null;
        
        Attr itemAttr = null;
        Attr bUserID = null;
        Attr bRating = null;
        Attr locLat = null;
        Attr locLong = null;
        Attr sUserID = null;
        Attr sRating = null;
        
        Date date = null;
        SimpleDateFormat fromTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0");
        SimpleDateFormat toTime = new SimpleDateFormat("MMM-dd-yy HH:mm:ss");
        DocumentBuilderFactory mFactory = null;
        DocumentBuilder mBuilder = null;
        org.w3c.dom.Document mDoc = null;
        StringWriter writer = null;
		try {
            conn = DbManager.getConnection(true);
            itemsStmt = conn.createStatement();
            itemRes = itemsStmt.executeQuery("SELECT * FROM Items WHERE ItemID=" + itemId);
            if(!itemRes.next())
                return "";
            mFactory = DocumentBuilderFactory.newInstance();
            mBuilder = mFactory.newDocumentBuilder();
            mDoc = mBuilder.newDocument();
            //item
            eleItem = mDoc.createElement("Item");
            mDoc.appendChild(eleItem);
            itemAttr = mDoc.createAttribute("ItemID");
            itemAttr.setValue(itemId);
            eleItem.setAttributeNode(itemAttr);
            //name
            eleName = mDoc.createElement("Name");
            eleName.appendChild(mDoc.createTextNode(itemRes.getString("Name")));
            eleItem.appendChild(eleName);
            //categories
            categoryStmt = conn.createStatement();
            categoryRes = categoryStmt.executeQuery("SELECT * FROM Category WHERE ItemID=" + itemId);
            while(categoryRes.next()){
                eleCat = mDoc.createElement("Category");
                eleCat.appendChild(mDoc.createTextNode(categoryRes.getString("Category")));
                eleItem.appendChild(eleCat);
            }
            
            //currently
            eleCurrent = mDoc.createElement("Currently");
            currStmt = conn.createStatement();
            currRes = currStmt.executeQuery("SELECT MAX(Amount) FROM Bids WHERE ItemID=" + itemId);
            currRes.next();
            eleCurrent.appendChild(mDoc.createTextNode(currRes.getString("MAX(Amount)")));
            eleItem.appendChild(eleCurrent);
            
            //Buy_Price
            eleBuyP = mDoc.createElement("Buy_Price");
            eleBuyP.appendChild(mDoc.createTextNode(itemRes.getString("Buy_Price")));
            eleItem.appendChild(eleBuyP);
            
            //First_Bid
            eleFirstBid = mDoc.createElement("First_Bid");
            eleFirstBid.appendChild(mDoc.createTextNode(itemRes.getString("First_Bid")));
            eleItem.appendChild(eleFirstBid);
            
            //Number_of_Bids
            eleNumBids = mDoc.createElement("Number_of_Bids");
            numBidsStmt = conn.createStatement();
            numBidsRes = numBidsStmt.executeQuery("SELECT COUNT(*) FROM Bids WHERE ItemID=" + itemId);
            numBidsRes.next();
            eleNumBids.appendChild(mDoc.createTextNode(numBidsRes.getString("COUNT(*)")));
            eleItem.appendChild(eleNumBids);
            
            //First_Bid
            eleFirstBid = mDoc.createElement("First_Bid");
            eleFirstBid.appendChild(mDoc.createTextNode(itemRes.getString("First_Bid")));
            eleItem.appendChild(eleFirstBid);
            
            //Bids
            bidsStmt = conn.createStatement();
            bidsRes = bidsStmt.executeQuery("SELECT * FROM Bids WHERE ItemID=" + itemId);
            eleBids = mDoc.createElement("Bids");
            while(bidsRes.next()){
                //Bid
                eleBid = mDoc.createElement("Bid");
                bidStmt = conn.createStatement();
                bidRes = bidStmt.executeQuery("SELECT * FROM Bidders WHERE UserID=" + "\"" + bidRes.getString("BidderID") + "\"");
                bidRes.next();
                //Bidder
                eleBidder = mDoc.createElement("Bidder");
                bUserID = mDoc.createAttribute("UserID");
                bUserID.setValue(bidsRes.getString("BidderID"));
                eleBidder.setAttributeNode(bUserID);
                bRating = mDoc.createAttribute("Rating");
                bRating.setValue(bidRes.getString("BidRating"));
                eleBidder.setAttributeNode(bRating);
                
                //location
                eleLoc = mDoc.createElement("Location");
                eleLoc.appendChild(mDoc.createTextNode(bidRes.getString("Location")));
                eleBidder.appendChild(eleLoc);
                
                //Country
                eleCountry = mDoc.createElement("Country");
                eleCountry.appendChild(mDoc.createTextNode(bidRes.getString("Country")));
                eleBidder.appendChild(eleCountry);
                eleBid.appendChild(eleBidder);
                
                //Time
                eleTime = mDoc.createElement("Time");
                date = fromTime.parse(bidsRes.getString("Time"));
                eleTime.appendChild(mDoc.createTextNode(toTime.format(date)));
                eleBid.appendChild(eleTime);
                
                //Amount
                eleAmount = mDoc.createElement("Amount");
                eleAmount.appendChild(mDoc.createTextNode(bidsRes.getString("Amount")));
                eleBid.appendChild(eleAmount);
                
                eleBids.appendChild(eleBid);
            }
            eleItem.appendChild(eleBids);
            
            //Location
            eleLoc = mDoc.createElement("Location");
            locLat = mDoc.createAttribute("Latitude");
            locLat.setValue(itemRes.getString("Latitude"));
            eleLoc.setAttributeNode(locLat);
            locLong = mDoc.createAttribute("Longitude");
            locLong.setValue(itemRes.getString("Longitude"));
            eleLoc.setAttributeNode(locLong);
            eleItem.appendChild(eleLoc);
            
            //Country
            eleCountry = mDoc.createElement("Country");
            eleCountry.appendChild(mDoc.createTextNode(itemRes.getString("Country")));
            eleItem.appendChild(eleCountry);
            
            //Started
            eleStarted = mDoc.createElement("Started");
            date = fromTime.parse(itemRes.getString("Started"));
            eleStarted.appendChild(mDoc.createTextNode(toTime.format(date)));
            eleItem.appendChild(eleStarted);
            
            //Ends
            eleEnds = mDoc.createElement("Ends");
            date = fromTime.parse(itemRes.getString("Ends"));
            eleEnds.appendChild(mDoc.createTextNode(toTime.format(date)));
            eleItem.appendChild(eleEnds);
            
            //Seller
            eleSeller = mDoc.createElement("Seller");            
            sellerStmt = conn.createStatement();
            sellerRes = sellerStmt.executeQuery("SELECT * FROM Sellers WHERE UserID = " + "\"" + itemRes.getString("SellerID") + "\"");
            sellerRes.next();
            sUserID = mDoc.createAttribute("UserID");
            sUserID.setValue(sellerRes.getString("UserID"));
            eleSeller.setAttributeNode(sUserID);
            sRating = mDoc.createAttribute("Rating");
            sRating.setValue(sellerRes.getString("SellRating"));
            eleSeller.setAttributeNode(sRating);
            eleItem.appendChild(eleSeller);
            
            //Description
            eleDescription = mDoc.createElement("Description");
            eleDescription.appendChild(mDoc.createTextNode(itemRes.getString("Description")));
            eleItem.appendChild(eleDescription);
            
            //Finished
            DOMSource domSource = new DOMSource(mDoc);
			writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);
            
            //close everything            
            itemRes.close();
            categoryRes.close();
            currRes.close();
            numBidsRes.close();
            bidsRes.close();
            bidRes.close();
            sellerRes.close();
            conn.close();
                
        } catch (Exception ex) {
            System.out.println(ex);
        }
		return writer.toString();
	}
	
	public String echo(String message) {
		return message;
	}

}
