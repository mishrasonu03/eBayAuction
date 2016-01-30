1. Below is our database schema

Items(ItemID*, Name, Buy_Price, First_Bid, Started, Ends, SellerID, Description)
Category(ItemID*, Category*)
Bids(ItemID*, BidderID*, Time*, Amount)  
Users(UsersID*, Location, Country, BuyRating, SellRating)

2. Nontrivial functional dependencies:
{ItemID} -> {Name, Buy_Price, First_Bid, Started, Ends, SellerID, Description}
{ItemID, BidderID, Time} -> {Amount}
{UsersID} -> {Location, Country, BuyRating, SellRating}

3. Yes our relations are in BCNF

4. Yes our relations are in 4NF