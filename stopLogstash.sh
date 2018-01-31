﻿#!/bin/bash
NAME="logstash"
echo $NAME
ID=`ps -ef | grep $NAME | grep -v "$0" | grep -v "grep" | awk '{print $2}'`
echo $ID
echo "---------------"
for id in $ID
do
kill -9 $id
done
echo "---------------"