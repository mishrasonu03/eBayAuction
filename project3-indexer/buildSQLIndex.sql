CREATE TABLE SpatialIndex(
ItemID INT,
LatLong POINT NOT NULL,
PRIMARY KEY(ItemID)
)ENGINE=MyISAM;

INSERT INTO SpatialIndex(ItemID, LatLong) SELECT ItemID, POINT(Latitude, Longitude) FROM Items;

ALTER TABLE SpatialIndex ADD SPATIAL INDEX(LatLong);