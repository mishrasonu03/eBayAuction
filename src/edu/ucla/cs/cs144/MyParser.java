/* CS144
 *
 * Parser skeleton for processing item-???.xml files. Must be compiled in
 * JDK 1.5 or above.
 *
 * Instructions:
 *
 * This program processes all files passed on the command line (to parse
 * an entire diectory, type "java MyParser myFiles/*.xml" at the shell).
 *
 * At the point noted below, an individual XML file has been parsed into a
 * DOM Document node. You should fill in code to process the node. Java's
 * interface for the Document Object Model (DOM) is in package
 * org.w3c.dom. The documentation is available online at
 *
 * http://java.sun.com/j2se/1.5.0/docs/api/index.html
 *
 * A tutorial of Java's XML Parsing can be found at:
 *
 * http://java.sun.com/webservices/jaxp/
 *
 * Some auxiliary methods have been written for you. You may find them
 * useful.
 */

package edu.ucla.cs.cs144;

import java.io.*;
import java.text.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;


class MyParser {
    
    static final String columnSeparator = "|*|";
    static DocumentBuilder builder;
    
    /*
     * File variables used to write xml data to character stream outputs
     * bidID is used so that two similar bids can be treated separately
     * and deleted by sort -u
     */
    static BufferedWriter itemTableWriter;    
    static BufferedWriter categoryTableWriter;
    static BufferedWriter userTableWriter;
    static BufferedWriter bidTableWriter;

    static final String[] typeName = {
	"none",
	"Element",
	"Attr",
	"Text",
	"CDATA",
	"EntityRef",
	"Entity",
	"ProcInstr",
	"Comment",
	"Document",
	"DocType",
	"DocFragment",
	"Notation",
    };
    
    static class MyErrorHandler implements ErrorHandler {
        
        public void warning(SAXParseException exception)
        throws SAXException {
            fatalError(exception);
        }
        
        public void error(SAXParseException exception)
        throws SAXException {
            fatalError(exception);
        }
        
        public void fatalError(SAXParseException exception)
        throws SAXException {
            exception.printStackTrace();
            System.out.println("There should be no errors " +
                               "in the supplied XML files.");
            System.exit(3);
        }
        
    }
    
    /* Non-recursive (NR) version of Node.getElementsByTagName(...)
     */
    static Element[] getElementsByTagNameNR(Element e, String tagName) {
        Vector< Element > elements = new Vector< Element >();
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
            {
                elements.add( (Element)child );
            }
            child = child.getNextSibling();
        }
        Element[] result = new Element[elements.size()];
        elements.copyInto(result);
        return result;
    }
    
    /* Returns the first subelement of e matching the given tagName, or
     * null if one does not exist. NR means Non-Recursive.
     */
    static Element getElementByTagNameNR(Element e, String tagName) {
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
                return (Element) child;
            child = child.getNextSibling();
        }
        return null;
    }
    
    /* Returns the text associated with the given element (which must have
     * type #PCDATA) as child, or "" if it contains no text.
     */
    static String getElementText(Element e) {
        if (e.getChildNodes().getLength() == 1) {
            Text elementText = (Text) e.getFirstChild();
            return elementText.getNodeValue();
        }
        else
            return "";
    }
    
    /* Returns the text (#PCDATA) associated with the first subelement X
     * of e with the given tagName. If no such X exists or X contains no
     * text, "" is returned. NR means Non-Recursive.
     */
    static String getElementTextByTagNameNR(Element e, String tagName) {
        Element elem = getElementByTagNameNR(e, tagName);
        if (elem != null)
            return getElementText(elem);
        else
            return "";
    }
    
    /* Returns the amount (in XXXXX.xx format) denoted by a money-string
     * like $3,453.23. Returns the input if the input is an empty string.
     */
    static String strip(String money) {
        if (money.equals(""))
            return money;
        else {
            double am = 0.0;
            NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
            try { am = nf.parse(money).doubleValue(); }
            catch (ParseException e) {
                System.out.println("This method should work for all " +
                                   "money values you find in our data.");
                System.exit(20);
            }
            nf.setGroupingUsed(false);
            return nf.format(am).substring(1);
        }
    }


    /**
     * Parse2ItemTable parses one item from the given xml file.<p>
     * @param root element (in)
     * @throws IOException thrown if error in reading values
     */
    static void parse2ItemTable(Element item) throws IOException {
        String itemID = item.getAttribute("ItemID");
        String name = getElementTextByTagNameNR(item, "Name");
        String buyPrice = strip(getElementTextByTagNameNR(item, "Buy_Price"));
        String firstBid = strip(getElementTextByTagNameNR(item, "First_Bid"));
        String started = getElementTextByTagNameNR(item, "Started");
        String ends = getElementTextByTagNameNR(item, "Ends");
        String description = getElementTextByTagNameNR(item, "Description");

        started = timestamp(started); //source of error
        ends = timestamp(ends); //source of error
        description = description.length() <= 4000? description: description.substring(0, 4000);

        Element seller = getElementByTagNameNR(item, "Seller");
        String sellerID = seller.getAttribute("UserID");

        writeToFile(itemTableWriter, itemID, sellerID, name, buyPrice, firstBid, started, ends, description);
    }


    /**
     * parse2UserTable parses one item from the given xml file.<p>
     * @param root element (in)
     * @throws IOException thrown if error in reading values
     */
    static void parse2UserTable(Element item) throws IOException {
        Element user = getElementByTagNameNR(item, "Seller");
        String userID = user.getAttribute("UserID");
        String rating = user.getAttribute("Rating");
        String location = getElementText(getElementByTagNameNR(item, "Location"));
        String country = getElementText(getElementByTagNameNR(item, "Country"));
            
        //location = (location == null)? "" : location; 
        //country = (country == null)? "" : country;

        writeToFile(userTableWriter, userID, rating, location, country);
            
        Element[] bids = getElementsByTagNameNR(getElementByTagNameNR(item, "Bids"), "Bid");
            
        for(int i = 0; i < bids.length; i++){
            Element bidder = getElementByTagNameNR(bids[i], "Bidder");
            String bID = bidder.getAttribute("UserID");
            String bRating = bidder.getAttribute("Rating");
            String bLocation = getElementTextByTagNameNR(bidder, "Location");
            String bCountry = getElementTextByTagNameNR(bidder, "Country");

            //bLocation = (bLocation == null)? "" : bLocation;
            //bCountry = (bCountry == null)? "" : bCountry;
            
            writeToFile(userTableWriter, bID, bRating, bLocation, bCountry);
        }
    }


    /**
     * parse2CategoriesTable parses one item from the given xml file.<p>
     * @param root element (in)
     * @throws IOException thrown if error in reading values
     */
    static void parse2CategoriesTable(Element item) throws IOException {
        String itemID = item.getAttribute("ItemID");
        Element[] categories = getElementsByTagNameNR(item, "Category");

        for(int i = 0; i < categories.length; i++){
            String category = getElementText(categories[i]);
            writeToFile(categoryTableWriter, itemID, category);
        }
    }


    /**
     * parse2BidTable parses one item from the given xml file.<p>
     * @param root element (in)
     * @throws IOException thrown if error in reading values
     */
    static void parse2BidTable(Element item) throws IOException {
        String itemID = item.getAttribute("ItemID");
        Element[] bids = getElementsByTagNameNR(getElementByTagNameNR(item, "Bids"), "Bid");
        
        for(int i = 0; i < bids.length; i++){
            Element bidder = getElementByTagNameNR(bids[i], "Bidder");
            String userID = bidder.getAttribute("UserID");
            String time = timestamp(getElementTextByTagNameNR(bids[i], "Time")); //source of error
            String amount = strip(getElementTextByTagNameNR(bids[i], "Amount"));
            writeToFile(bidTableWriter, itemID, userID, time, amount);
        }
    }


    /**
     * Method converts the xml date input into output.<p>
     * @param pass in date as string from xml document
     * @throws IOException thrown if there is an error reading in the value
     */
    static String timestamp(String date) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("MMM-dd-yy HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return outputFormat.format(inputFormat.parse(date)).toString();
        }
        catch(ParseException e) {
            System.err.println("timestamp: Parse error");
            return "timestamp: Parse error";
        }
    }

    
    /** 
     * rowFormat generates the tuple format. <p>
     * @param String[] input (in)
     * @throws IOException if error in writing values
     */
    static String rowFormat(String[] input) {
        StringBuilder row = new StringBuilder();
        int i = 0;
        while(i < input.length-1){
            row.append(input[i] + columnSeparator);
            i++;
        }
        row.append(input[i]);
            
        return row.toString();
    }
    

    /** 
     * writeToFile writes a tuple to the given table. <p>
     * @param BufferedWriter output(in), String... args(in)
     * @throws IOException if error in writing values
     */ 
    static void writeToFile(BufferedWriter output, String... args) throws IOException {
        output.write(rowFormat(args));
        output.newLine();
    }


    /* Process one items-?.xml file.
     */
    static void processFile(File xmlFile) {
        Document doc = null;
        try {
            doc = builder.parse(xmlFile);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(3);
        }
        catch (SAXException e) {
            System.out.println("Parsing error on file " + xmlFile);
            System.out.println("  (not supposed to happen with supplied XML files)");
            e.printStackTrace();
            System.exit(3);
        }
    
        System.out.println("Successfully parsed - " + xmlFile);

        Element[] items = getElementsByTagNameNR(doc.getDocumentElement(), "Item");
        
        try {
            for(int i = 0; i< items.length; i++) {
                parse2ItemTable(items[i]);
                parse2CategoriesTable(items[i]);
                parse2UserTable(items[i]);
                parse2BidTable(items[i]);
            }
        }
        catch (IOException e) {
                e.printStackTrace();
        }
    }
    
    public static void main (String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java MyParser [file] [file] ...");
            System.exit(1);
        }
    
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringElementContentWhitespace(true);      
            builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new MyErrorHandler());
        }
        catch (FactoryConfigurationError e) {
            System.out.println("unable to get a document builder factory");
            System.exit(2);
        } 
        catch (ParserConfigurationException e) {
            System.out.println("parser was unable to be configured");
            System.exit(2);
        }
        
        try{

            itemTableWriter = new BufferedWriter(new FileWriter("itemTable.dat", true));            
            categoryTableWriter = new BufferedWriter(new FileWriter("categoryTable.dat", true));
            userTableWriter = new BufferedWriter(new FileWriter("userTable.dat", true));
            bidTableWriter = new BufferedWriter(new FileWriter("bidTable.dat", true));

            /* Process all files listed on command line. */
            for (int i = 0; i < args.length; i++) {
                File currentFile = new File(args[i]);
                processFile(currentFile);
            }

            itemTableWriter.close();
            userTableWriter.close();
            categoryTableWriter.close();
            bidTableWriter.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
