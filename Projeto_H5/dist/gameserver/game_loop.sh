#!/bin/bash

while :;
do
java -server -Dfile.encoding=UTF-8 -Xmx8G -XX:PermSize=3G -cp config:./smrt.jar:./../libs/* l2f.gameserver.GameServer > log/stdout.log 2>&1
        [ $? -ne 2 ] && break
        sleep 30;
done

