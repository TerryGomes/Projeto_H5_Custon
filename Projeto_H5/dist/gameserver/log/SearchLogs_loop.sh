#!/bin/bash

while :;
do
java -Dtakipi.name="Logs_Search" -agentlib:TakipiAgent LogsCheck allItems.log 0 -1 274045216 > result.txt 2>&1
        [ $? -ne 2 ] && break
        sleep 30;
done
