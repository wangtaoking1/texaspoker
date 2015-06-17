#!/usr/bin/env bash
cd `dirname $(readlink -f $0)`

if [[ ! -d ../run_area ]]; then 
  echo "missing  run_area folder, please check"
  exit 
fi  

echo "check the size of works foldery ...."
size=$(du -m -s works | awk '{print $1}')
if [[ $size -ge 50 ]]; then
  echo "the total size( $size M) of the works is beyoung the limit( 50 M) "
  echo "you can "
  echo " 1. clean the tmp file during the build process or "
  echo " 2. remove libarys and file those is no need any more or"
  echo " 3. rework you code"
  exit
fi

echo "archive the works folder"
rm -rf works.tar.gz
tar czf works.tar.gz works/
zipsize=$(du -m -s works.tar.gz | awk '{print $1}')
if [[ $zipsize -ge 10 ]]; then
  echo "the archive ($zipsize M) of the works is beyoung the limit( 10 M) "
  echo "you can "
  echo " 1. clean the tmp file during the build process or "
  echo " 2. remove libarys and file those is no need any more or"
  echo " 3. rework you code"
  exit
fi

echo "you can use works.tar.gz for submit"

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

echo "start your program"
pushd . >/dev/null
cd works/target
for i in 1 2 3 4 5 6 7 8
do
chmod u+x game 
./game 127.0.0.1 6000 127.0.0.$i 600$i $i$i$i$i 0</dev/null 1>/dev/null 2>/dev/null &
done
popd >/dev/null

popd >/dev/null


ps t
