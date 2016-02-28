<%@ page import="edu.ucla.cs.cs144.SearchResult" %>
<%@ page import="javax.xml.parsers.DocumentBuilderFactory" %>
<%@ page import="javax.xml.parsers.DocumentBuilder" %>


<html>
<body onload="initialize()"> 
<form action="/eBay/item">
	Search By ItemId: <input type="text" name="id" style="width:500px;"/>
	<input type="submit" />
</form>

<hr>
<h1><%= request.getAttribute("Name") %></h1>

<p>ID: <%= request.getAttribute("ItemID") %></p>

<p>Categories: 
<% String[] Categories = (String[]) request.getAttribute("Categories");		
		for(int i = 0; i < Categories.length; i++){
			if (i == 0){
		%>
		<%= Categories[i] %>
		<%
			}
			else {
			%>
			<%= ", " + Categories[i]%>

            <%
            }
		}		
		%>
</p>

<p>Currently: <%= request.getAttribute("Currently") %></p>

<% if(request.getAttribute("Buy_Price") != ""){
%>
	<p>Buy Price: <%= request.getAttribute("Buy_Price") %></p>
<%
} 
%>

<p>First Bid: <%= request.getAttribute("First_Bid") %></p>

<p>Number of Bids: <%= request.getAttribute("Number_of_Bids") %></p>

<p>Started: <%= request.getAttribute("Started") %></p>

<p>Ends: <%= request.getAttribute("Ends") %></p>

<p>Seller UserID: <%= request.getAttribute("Seller_ID") %></p>

<p>Seller Rating: <%= request.getAttribute("Seller_Rating") %></p>

<% if (request.getAttribute("Description") != ""){
%>
	<p>Description: <%= request.getAttribute("Description") %></p>
<%
} 
%>

<p>
<% 
    String[] BidUID = (String[]) request.getAttribute("Bidder_ID");
	String[] BidRating = (String[]) request.getAttribute("Bidder_Rating");
	String[] BidLocation = (String[]) request.getAttribute("Bidder_Location");
	String[] BidCountry = (String[]) request.getAttribute("Bidder_Country");
	String[] BidTime = (String[]) request.getAttribute("BidTime");
	String[] BidAmount = (String[]) request.getAttribute("BidAmount");
		
	for(int i = 0; i < BidUID.length; i++){
		if(i == 0){
		%>
		<table border="1" cellpadding="5">
			<tr>
				<th>Bid #</th>
				<th>UserID </th>
				<th>Rating </th>
				<th>Location </th>
				<th>Country </th>
				<th>Time </th>
				<th>Amount </th>
			</tr>
		<%
	}
		%>
		<tr> 
			<td><%= i+1 %></td> 
			<td><%= BidUID[i]%></td>
			<td><%= BidRating[i]%></td>
			<td><%= BidLocation[i] %></td>
			<td><%= BidCountry[i] %></td>
			<td><%= BidTime[i] %></td>
			<td><%= BidAmount[i] %></td>
		</tr>
		<% if (i == (BidUID.length-1)) { %>
		</table>
	
		<%
		}
		%>	

		<%
		}		
		%>
</p>

<p>Location: <%= BidLocation[BidLocation.length-1] %></p>
<p>Country: <%= BidCountry[BidCountry.length-1] %></p>

<% String geoLocation = BidLocation[BidLocation.length-1] + ", " + BidCountry[BidCountry.length-1]; %>

<meta name="viewport" content="initial-scale=1.0, user-scalable=no" /> 

<script type="text/javascript" 
    src="http://maps.google.com/maps/api/js?sensor=false"> 
</script> 
<script type="text/javascript"> 
  function initialize() { 
  	geocoder = new google.maps.Geocoder();
  	var address = "<%=geoLocation%>";
    geocoder.geocode( { 'address': address}, function(results, status) {
      if (status == google.maps.GeocoderStatus.OK) {
      	var myOptions = {
                zoom: 14,
                center: results[0].geometry.location,
                mapTypeId: google.maps.MapTypeId.ROADMAP
            };
      	var map = new google.maps.Map(document.getElementById("map_canvas"), myOptions); 
        map.setCenter(results[0].geometry.location);
        var marker = new google.maps.Marker({
            map: map,
            position: results[0].geometry.location
        });
      } else { //location not found, show generic map
        var latlng = new google.maps.LatLng(0.0, 0.0);
        var myOptions = { 
            zoom: 1, 
            center: latlng, 
            mapTypeId: google.maps.MapTypeId.ROADMAP 
            }; 
        var map = new google.maps.Map(document.getElementById("map_canvas"), myOptions); 
      }
    });
    
  } 
</script> 
<div id="map_canvas" style="width:500px; height:400px"></div> 
</div>
</body>
</html>