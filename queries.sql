--Find the number of users in the database
SELECT COUNT(*) 
FROM Users;

--Find the number of items in "New York"
SELECT COUNT(*) 
FROM Items, Users 
WHERE Items.SellerID = Users.UsersID AND Users.COUNTRY = 'New York';

--Find the number of auctions belonging to exactly four categories
SELECT COUNT(*)
FROM (SELECT ItemID 
FROM Category
GROUP BY ItemID
HAVING COUNT(Category) = 4) as a;

--Find the ID(s) of current (unsold) auction(s) with the highest bid
SELECT ItemID
FROM Items
WHERE Currently = (SELECT MAX(Currently)
FROM Items 
WHERE Ends > '2001-12-20 00:00:01');

--Find the number of sellers whose rating is higher than 1000
SELECT COUNT(*)
FROM Users, Items
WHERE Users.UsersID = Items.SellerID AND Users.SellRating > 1000;

--Find the number of users who are both sellers and bidders
SELECT COUNT(*) 
FROM (SELECT DISTINCT Items.ItemID
FROM Items INNER JOIN Bids
ON Items.SellerID = Bids.BidderID) as a;

--Find the number of categories that include at least one item with a bid of more than $100
SELECT COUNT(*)
FROM Category
GROUP BY Category
HAVING ItemID IN (SELECT DISTINCT ItemID
FROM Items, Bids 
WHERE Bids.Amount > 100.00 AND Items.ItemID = Bids.ItemID);