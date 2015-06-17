#!/usr/bin/env bash

echo "prepare for test.."
killall gameserver 
killall game
killall zhu
killall less
killall new_less
killall advance
killall all_in
killall check
killall random
killall raise1
killall raise100
killall fold
killall call
killall java

cd ../
rm server/log.txt
rm server/replay.txt
#rm server/data.csv

echo "start server"
cd server/
for i in 1 2 3 4 5 6 7 8
do 
  export "PLAYER"$i"_IP"=127.0.0.$i
  export "PLAYER"$i"_PORT"=600$i
  export "PLAYER"$i"_ID"=$i$i$i$i
done
chmod u+x gameserver
#./gameserver -gip 127.0.0.1 -seq replay -r 30 -d 1 -m 10000 -b 50 -t 2000 -h 500 0</dev/null 1>/dev/null 2>/dev/null  & 
./gameserver -gip 127.0.0.1 -seq replay -r 30 -d 0 -m 4000 -b 20 -t 2000 -h 600 -i 500 0</dev/null 1>/dev/null 2>/dev/null  & 

cd ../works/target

echo "start playmates"

./check 127.0.0.1 6000 127.0.0.1 6001 1111 0</dev/null 1>/dev/null 2>/dev/null &
./call 127.0.0.1 6000 127.0.0.2 6002 2222 0</dev/null 1>/dev/null 2>/dev/null &
java -jar 2_less.jar 127.0.0.1 6000 127.0.0.3 6003 3333 0</dev/null 1>/dev/null 2>/dev/null &
java -jar 3_less.jar 127.0.0.1 6000 127.0.0.4 6004 4444 0</dev/null 1>/dev/null 2>/dev/null &
java -jar 5_less.jar 127.0.0.1 6000 127.0.0.5 6005 5555 0</dev/null 1>/dev/null 2>/dev/null &
java -jar select.jar 127.0.0.1 6000 127.0.0.6 6006 6666 0</dev/null 1>/dev/null 2>/dev/null &
java -jar 2_select.jar 127.0.0.1 6000 127.0.0.7 6007 7777 0</dev/null 1>/dev/null 2>/dev/null &
java -jar 4_select.jar 127.0.0.1 6000 127.0.0.8 6008 8888 0</dev/null 1>/dev/null 2>/dev/null &

ps t
