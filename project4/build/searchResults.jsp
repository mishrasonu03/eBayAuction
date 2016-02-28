<%@ page import="edu.ucla.cs.cs144.SearchResult" %>
<%
	int numSkip = Integer.parseInt(request.getAttribute("numResultsToSkip").toString());
	int numReturn = Integer.parseInt(request.getAttribute("numResultsToReturn").toString());
	String query = request.getAttribute("searchQuery").toString();
%>

<html>
	<head>
		<script type="text/javascript" src="suggest.js"></script>   
		<script type="text/javascript">
    	window.onload = function () {    		
			var oTextbox = new AutoSuggestControl(document.getElementById("qbox"));        
        }
		</script>		
	</head>	
	<body>
		
		<form action="/eBay/search">			
			Search for: <input type="text" name="q" id="qbox" style="width:500px;"/> 			
			<input type="hidden" name="numResultsToSkip" value="0"/>
			<input type="hidden" name="numResultsToReturn" value="11"/>
			<input type="submit"/> <br/> 
		</form>
        <hr>
		<%
		SearchResult[] basicResults = (SearchResult[]) request.getAttribute("searchResults");
		int resLength = basicResults.length;		
		if (resLength > 10){
			resLength = 10;
		}
		
		if (resLength > 0){			
			if (!basicResults[0].getItemId().equals("-1")){
		%>
		
		<p> Showing Results <%= numSkip+1 %> to <%= numSkip+resLength %> of <%= query %> </p>
		
		<%
				int count = 0;
				for (SearchResult result : basicResults){
					if (count == 10){
						break;
					}
		%>
				<a href="/eBay/item?id=<%= result.getItemId() %>" > <%= result.getItemId() + ": " + result.getName() %> </a> <br/><br/>
		<%
					count++;
				}		
			}
		%>
		
		<br/>
		<% 
			if (numSkip > 0){
			%> 
				<a href="/eBay/search?q=<%= query %>&numResultsToSkip=<%= numSkip-10 %>&numResultsToReturn=11">Previous Page</a>
			<%
			}
			if (basicResults.length - 10 > 0){
			%> 
				<a href="/eBay/search?q=<%= query %>&numResultsToSkip=<%= numSkip+10 %>&numResultsToReturn=11">Next Page</a>
			<%
			}
		} 
		%>
	</body>
</html>