#!/bin/bash

mysql CS144 < drop.sql
mysql CS144 < create.sql

ant
#ant run
ant run-all

sort -u categoryTable.dat > category.dat
sort -u itemTable.dat > item.dat
sort -u bidTable.dat > bid.dat
sort -u sellerTable.dat > seller.dat
sort -u bidderTable.dat > bidder.dat

mysql CS144 < load.sql

rm *.dat
rm -rf bin

