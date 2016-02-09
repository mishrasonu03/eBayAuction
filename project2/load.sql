LOAD DATA LOCAL INFILE 'seller.dat' INTO TABLE Sellers 
FIELDS TERMINATED BY '|*|';

LOAD DATA LOCAL INFILE 'bidder.dat' INTO TABLE Bidders 
FIELDS TERMINATED BY '|*|';

LOAD DATA LOCAL INFILE 'item.dat' INTO TABLE Items 
FIELDS TERMINATED BY '|*|';

LOAD DATA LOCAL INFILE 'category.dat' INTO TABLE Category 
FIELDS TERMINATED BY '|*|';

LOAD DATA LOCAL INFILE 'bid.dat' INTO TABLE Bids 
FIELDS TERMINATED BY '|*|';
