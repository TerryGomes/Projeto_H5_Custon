#!/bin/bash

while :;
do
	java -server -Dfile.encoding=UTF-8 -Xmx1G -cp config:./../libs/* l2mv.loginserver.AuthServer > log/stdout.log 2>&1

	[ $? -ne 2 ] && break
	sleep 10;
done
