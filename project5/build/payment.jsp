<% String secureConfirm = "https://" + request.getServerName() + ":8443" + request.getContextPath() + "/confirm"; %>
<% String insecureSearch = "http://" + request.getServerName() + ":1448" + request.getContextPath() + "/search"; %>

<html>
    <head>
        <script type="text/javascript" src="suggest.js"></script>  
        <link rel="stylesheet" type="text/css" href="suggest.css"/>            
        <script type="text/javascript">
        window.onload = function () {           
            var oTextbox = new AutoSuggestControl(document.getElementById("qbox"));        
        }
        </script>       
    </head> 

	<body>
        <form action="<%= insecureSearch %>">            
            Search for: <input type="text" name="q" id="qbox" style="width:500px;"/>            
            <input type="hidden" name="numResultsToSkip" value="0"/>
            <input type="hidden" name="numResultsToReturn" value="11"/>
            <input type="submit"/> <br/> 
        </form>
        <hr>

		<h1>Payment Details</h1>
		
		<form method="post" action="<%= secureConfirm %>">
        	<p>Item ID: <%= request.getAttribute("ID") %></p> 
            <p>Item Name: <%= request.getAttribute("Name") %></p> 
        	<p>Buy Price: <%= request.getAttribute("BuyPrice") %></p> <br />
        	
            <input type="hidden" name="action" value="confirmation" />
            <input type="hidden" name="id" value="<%= request.getAttribute("ID") %>" />
        	Enter Credit Card #: <input type="text" name="cardNo" /> <br />
        	
        	<input type="submit" value="Pay Now" />
        </form>		
	</body>
</html>