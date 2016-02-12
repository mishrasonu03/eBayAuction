package edu.ucla.cs.cs144;

import java.io.IOException;
import java.io.StringReader;
import java.io.File;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Indexer {
    
    /** Creates a new instance of Indexer */
    public Indexer() {
    }
 
    public void rebuildIndexes() {

        Connection conn = null;
        
        // create a connection to the database to retrieve Items from MySQL
	try {
	    conn = DbManager.getConnection(true);
        
	} catch (SQLException ex) {
	    System.out.println(ex);
	}


	/*
	 * Add your code here to retrieve Items using the connection
	 * and add corresponding entries to your Lucene inverted indexes.
         *
         * You will have to use JDBC API to retrieve MySQL data from Java.
         * Read our tutorial on JDBC if you do not know how to use JDBC.
         *
         * You will also have to use Lucene IndexWriter and Document
         * classes to create an index and populate it with Items data.
         * Read our tutorial on Lucene as well if you don't know how.
         *
         * As part of this development, you may want to add 
         * new methods and create additional Java classes. 
         * If you create new classes, make sure that
         * the classes become part of "edu.ucla.cs.cs144" package
         * and place your class source files at src/edu/ucla/cs/cs144/.
	 * 
	 */
     //Setup
    IndexWriter mIndexWriter = null;
    IndexWriterConfig config = null;
    Directory indexDir = null;
    try {
        indexDir = FSDirectory.open(new File ("/var/lib/lucene/index1"));
        config = new IndexWriterConfig(Version.LUCENE_4_10_2, new StandardAnalyzer());
        mIndexWriter = new IndexWriter(indexDir, config);
        
	} catch (IOException ex) {
	    System.out.println(ex);
	}
    
    //Get data
    Statement stmt = null;
    ResultSet mItems = null;
    int id = -1;
    String name = "";
    String description = "";
    String categories = "";
    String union = "";
    try{
    stmt = conn.createStatement();
    mItems = stmt.executeQuery("SELECT * FROM Items");

    while(mItems.next()){
        Document doc = new Document();
        id = mItems.getInt("ItemID");
        name = mItems.getString("Name");
        description = mItems.getString("Description");
        Statement stmt2 = conn.createStatement();
        ResultSet mCategories = stmt2.executeQuery("SELECT * FROM Category WHERE ItemID = " + id);
        categories = "";
        while(mCategories.next()){
            categories += mCategories.getString("Category") + " ";
        }
        
        union = mItems.getString("Name") + " " + mItems.getString("Description") + " " + categories;
        doc.add(new StringField("ItemID", ""+id, Field.Store.YES));
        doc.add(new TextField("Name", name, Field.Store.YES));
        doc.add(new TextField("Description", description, Field.Store.YES));
        doc.add(new TextField("Category", categories, Field.Store.YES));
        doc.add(new TextField("Union", union, Field.Store.YES));
        mIndexWriter.addDocument(doc);
        mCategories.close();
    }
    }
    catch(Exception e){
        System.out.println(e);
    }
    try{
        mItems.close();
        mIndexWriter.close();
    }
    catch(Exception e){
        System.out.println(e);
    }
        // close the database connection
	try {
	    conn.close();
	} catch (SQLException ex) {
	    System.out.println(ex);
	}
    }    

    public static void main(String args[]) {
        Indexer idx = new Indexer();
        idx.rebuildIndexes();
    }   
}
