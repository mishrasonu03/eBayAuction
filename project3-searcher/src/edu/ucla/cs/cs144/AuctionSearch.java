package edu.ucla.cs.cs144;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
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
        Connection con = null;
        Statement stmt = null;
        ResultSet mItems = null;
        String coord = "";
        Double lat = 0;
        Double longi = 0;
        int curIndex = 0;
        int querySize = numResultsToReturn;
        ArrayList<SearchResult> finalResults = new ArrayList<SearchResult>();
		SearchResult[] basicResults = basicSearch(query, numResultsToSkip, numResultsToReturn);
        try {
            conn = DbManager.getConnection(true);
        } catch (SQLException ex) {
            System.out.println(ex);
        }

        while(finalResults.size() < numResultsToReturn){
            //Go through each basic search result and find if coordinate in range
            while(curIndex < basicResults.length){
                stmt = conn.createStatement();
                mItems = stmt.executeQuery("SELECT ItemID, astext(Coordinate) FROM SpatialIndex WHERE ItemIndex=" + basicResults[i].getItemId());
                if(items.next){
                    coord = items.getString("astext(Coordinate)");
                    //CHECK HOW COORD IS FORMATTED
                    System.out.println(coord);
                    //CODE TO EXTRACT Latitude and Longitude here
                    //
                    lat = Double.parseDouble(coord.substring(0,8)); //Change
                    longi = Double.parseDouble(coord.substring(8)); //Change
                    
                    if(lat >= region.getLx() && lat <= region.getRx() && longi >= region.getLy() && longi <= region.getRy()){
                        finalResults.add(basicResults[i]);
                        if(finalResults.size() == numResultsToReturn)
                            break;
                    }
                }
                curIndex++;
            }
            //If there are no more basic search results or num of searches reached, end
            if(basicResults.length < querySize || finalResults.size() == numResultsToReturn)
                break;
            //exponentially increase query size
            querySize*=2;
            basicResults = basicSearch(query, numResultsToSkip, querySize);
        }
        
        try {
            conn.close();
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        
		return finalResults.toArray(new SearchResult[finalResults.size()]);;
	}

	public String getXMLDataForItemId(String itemId) {
		// TODO: Your code here!
		return "";
	}
	
	public String echo(String message) {
		return message;
	}

}
