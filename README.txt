1. Below is our database schema

Items(ItemID*, Name, Buy_Price, First_Bid, Started, Ends, SellerID, Location, Latitude, Longitude, Country, Description)
Category(ItemID*, Category*)
Bids(ItemID*, BidderID*, Time*, Amount)
Sellers(UserID*, SellRating)
Bidders(UserID*, BuyRating, Location, Country)

Notes:
a. Currently and Number_of_Bids have not been included in the Item table, because these can be calculated from Bids table
b. As posted by Param on Piazza, Item's location is not seller's location. So Items' location/country have been kept in Items table only.
c. According to items.txt, Seller's attributes give only UserID and rating.
d. According to items.txt, when location is a child element of a Bidder, it does not have Latitude and Longitude attributes.
e. Merging Sellers and Bidders into one table Users would lead to many NULL entries.


2. Nontrivial functional dependencies:
{ItemID} -> {Name, Buy_Price, First_Bid, Started, Ends, SellerID, Location, Latitude, Longitude, Country, Description}
{ItemID, BidderID, Time} -> {Amount}
{UsersID} -> {SellRating}
{UsersID} -> {Location, Country, BuyRating}

3. Yes our relations are in BCNF

4. Yes our relations are in 4NF
