--Find the number of users in the database
--Correct Answer: 13422
SELECT COUNT(*) 
FROM Users;


--Find the number of items in "New York"
--Correct Answer: 80
SELECT COUNT(DISTINCT Items.ItemID)
FROM Users INNER JOIN Items
ON Users.UsersID = Items.SellerID
WHERE BINARY Location = 'New York';


--Find the number of auctions belonging to exactly four categories
--Correct Answer: 8365
SELECT COUNT(*)
FROM (SELECT ItemID 
FROM Category
GROUP BY ItemID
HAVING COUNT(Category) = 4) as a;


--Find the ID(s) of current (unsold) auction(s) with the highest bid
--Correct Answer: 1046740686
SELECT Bids.ItemID
FROM Bids INNER JOIN Items
ON Bids.ItemID = Items.ItemID
WHERE Ends > '2001-12-20 00:00:01'
AND Amount = (SELECT MAX(Amount) FROM Bids);


--Find the number of sellers whose rating is higher than 1000
--Wrong Answer: 2565
SELECT COUNT(*)
FROM Users
WHERE Users.SellRating > 1000;


--Find the number of users who are both sellers and bidders
--Correct Answer: 6717
SELECT COUNT(DISTINCT Items.SellerID)
FROM Items INNER JOIN Bids
ON Items.SellerID = Bids.BidderID;


--Find the number of categories that include at least one item with a bid of more than $100
--Correct Answer: 150
SELECT COUNT(DISTINCT Category)
FROM Category INNER JOIN Bids
ON Category.ItemID = Bids.ItemID
WHERE Amount > 100;
