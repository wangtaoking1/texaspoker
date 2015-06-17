#!/usr/bin/env bash
cd `dirname $(readlink -f $0)`

if [[ ! -d ../run_area ]]; then 
  echo "missing run_area folder, please check"
  exit 
fi  

echo "check the size of works folder ...."
size=$(du -m -s works | awk '{print $1}')
if [[ $size -ge 50 ]]; then
  echo "the total size( $size M) of the works is beyond the limit( 50 M) "
  echo "you should "
  echo " 1. clean the tmp file during the build process or "
  echo " 2. remove libraries and files those are not needed any more or"
  echo " 3. rework you code"
  exit
fi

echo "archive the works folder"
rm -rf works.tar.gz
tar czf works.tar.gz works/
zipsize=$(du -m -s works.tar.gz | awk '{print $1}')
if [[ $zipsize -ge 10 ]]; then
  echo "the archive ($zipsize M) of the works is beyond the limit( 10 M) "
  echo "you should "
  echo " 1. clean the tmp file during the build process or "
  echo " 2. remove libraries and files those are not needed any more or"
  echo " 3. rework you code"
  exit
fi

echo "you should use works.tar.gz for submit"

echo ""

echo "prepare for test.."
killall game >/dev/null 2>&1
killall gameserver >/dev/null 2>&1
rm  -rf ../run_area/*
cp -r server ../run_area
cp -rf works.tar.gz ../run_area
pushd . >/dev/null
cd ../run_area
rm -rf works
tar zxf works.tar.gz  

echo "start server"
pushd . >/dev/null
cd server
for i in 1 2 3 4 5 6 7 8
do 
  export "PLAYER"$i"_IP"=127.0.0.$i
  export "PLAYER"$i"_PORT"=600$i
  export "PLAYER"$i"_ID"=$i$i$i$i
done
chmod u+x gameserver
./gameserver -gip 127.0.0.1 -seq replay -r 30 -d 0 -m 4000 -b 20 -t 2000 -h 600 -i 500 0</dev/null 1>/dev/null 2>/dev/null  &
popd >/dev/null

echo "start players"
pushd . >/dev/null
cd works/target

chmod u+x *

echo "start playmates"
#./all_in   127.0.0.1 6000 127.0.0.1 6001 1111 0</dev/null 1>/dev/null 2>/dev/null &
#./call     127.0.0.1 6000 127.0.0.2 6002 2222 0</dev/null 1>/dev/null 2>/dev/null &
#./check    127.0.0.1 6000 127.0.0.3 6003 3333 0</dev/null 1>/dev/null 2>/dev/null &
#./fold     127.0.0.1 6000 127.0.0.4 6004 4444 0</dev/null 1>/dev/null 2>/dev/null &
#./raise1   127.0.0.1 6000 127.0.0.5 6005 5555 0</dev/null 1>/dev/null 2>/dev/null &
#./raise100 127.0.0.1 6000 127.0.0.6 6006 6666 0</dev/null 1>/dev/null 2>/dev/null &
#./random   127.0.0.1 6000 127.0.0.7 6007 7777 0</dev/null 1>/dev/null 2>/dev/null &

./game_1   127.0.0.1 6000 127.0.0.1 6001 1111 0</dev/null 1>/dev/null 2>/dev/null &
./game_2    127.0.0.1 6000 127.0.0.2 6002 2222 0</dev/null 1>/dev/null 2>/dev/null &
./game_3    127.0.0.1 6000 127.0.0.3 6003 3333 0</dev/null 1>/dev/null 2>/dev/null &
./game_4   127.0.0.1 6000 127.0.0.4 6004 4444 0</dev/null 1>/dev/null 2>/dev/null &
./game_7   127.0.0.1 6000 127.0.0.5 6005 5555 0</dev/null 1>/dev/null 2>/dev/null &
./game_7   127.0.0.1 6000 127.0.0.6 6006 6666 0</dev/null 1>/dev/null 2>/dev/null &
./game_7   127.0.0.1 6000 127.0.0.7 6007 7777 0</dev/null 1>/dev/null 2>/dev/null &
#./game     127.0.0.1 6000 127.0.0.8 6008 8888 0</dev/null 1>/dev/null 2>/dev/null &
#./simple   127.0.0.1 6000 127.0.0.1 6001 1111 0</dev/null 1>/dev/null 2>/dev/null &
#./simple     127.0.0.1 6000 127.0.0.2 6002 2222 0</dev/null 1>/dev/null 2>/dev/null &
#./simple    127.0.0.1 6000 127.0.0.3 6003 3333 0</dev/null 1>/dev/null 2>/dev/null &
#./simple     127.0.0.1 6000 127.0.0.4 6004 4444 0</dev/null 1>/dev/null 2>/dev/null &



echo "start your game"
./game 127.0.0.1 6000 127.0.0.8 6008 8888 >$(pwd)/8888
#gdb ./game -ex "r 127.0.0.1 6000 127.0.0.8 6008 8888"

popd >/dev/null

popd >/dev/null

