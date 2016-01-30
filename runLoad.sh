#!/bin/bash

mysql CS144 < drop.sql
mysql CS144 < create.sql

ant
#ant run
#Use this to run on all data
ant run-all

sort -u itemTable.dat > itemTable.dat
sort -u categoryTable.dat > categoryTable.dat
sort -u userTable.dat > userTable.dat
sort -u bidTable.dat > bidTable.dat
sort -u sellerTable.dat > sellerTable.dat
sort -u bidderTable.dat > bidderTable.dat

mysql CS144 < load.sql

rm itemTable.dat
rm categoryTable.dat
rm userTable.dat
rm bidTable.dat
rm sellerTable.dat
rm bidderTable.dat
