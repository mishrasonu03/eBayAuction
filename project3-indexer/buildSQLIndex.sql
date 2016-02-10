CREATE TABLE SpatialIndex(
ItemID INT,
LatLong POINT NOT NULL,
PRIMARY KEY(ItemID)
)ENGINE=MyISAM;

INSERT INTO SpatialIndex(ItemID, LatLong) SELECT ItemID, POINT(Latitude, Longitude) FROM Items WHERE Latitude IS NOT NULL AND Longitude IS NOT NULL;

CREATE SPATIAL INDEX sp_index ON SpatialIndex(LatLong);