#!/bin/bash

mysql CS144 < drop.sql
mysql CS144 < create.sql

ant
ant run
#Use this to run on all data
#ant run-all

sort -u items.csv > items.csv
sort -u categories.csv > categories.csv
sort -u bids.csv > bids.csv
sort -u users.csv > users.csv

mysql CS144 < load.sql

rm items.csv
rm categories.csv
rm bids.csv
rm users.csv