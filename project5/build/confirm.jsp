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
        <form action="/eBay/search">            
            Search for: <input type="text" name="q" id="qbox" style="width:500px;"/>            
            <input type="hidden" name="numResultsToSkip" value="0"/>
            <input type="hidden" name="numResultsToReturn" value="11"/>
            <input type="submit"/> <br/> 
        </form>
        <hr>
	
    	<h1>Order Confirmation</h1>
		
        <p>Item ID: <%= request.getAttribute("ID") %></p> 
        <p>Item Name: <%= request.getAttribute("Name") %></p> 
       	<p>Buy Price: <%= request.getAttribute("BuyPrice") %></p> 
        <p>Credit Card: <%= request.getAttribute("CC") %></p>
        <p>Time: <%= request.getAttribute("Time") %></p>
    	
    </body>
</html>