Q1: For which communication(s) do you use the SSL encryption? If you are encrypting the communication from (1) to (2) in Figure 2, for example, write (1)→(2) in your answer.
A: (4)->(5) and (5)->(6). In the first communication, the user sends his credit-card information to the server for payment; and in the second, the server sends the credit-card information back to the user for confirmation.

Q2: How do you ensure that the item was purchased exactly at the Buy_Price of that particular item?
A: When the buyer lands on the item information page, we instantiate an HTTPSession variable. This is used to validate the request prior to purchase. For instance, any malicious user who tries to make a request to the payment page without having a proper session, gets an error.




---Ignore

		<%--
		   HttpSession session = request.getSession(true);
		   HashMap<String, String[]> itemMap = (HashMap<String, String[]>)session.getAttribute("itemMap");
		   String[] mapvalue = new String[2];
           mapvalue[0] = "etc";
           mapvalue[1] = "0.0";
           itemMap.put(request.getAttribute("ItemID"), mapvalue);
           session.setAttribute("itemMap", itemMap);
		--%>
		<%--
			(request.getSession(true)).setAttribute("itemMap", ((HashMap<String, String[]>)session.getAttribute("itemMap")).put(request.getAttribute("ItemID"), {"etc","0.0"}))
			
		--%>
