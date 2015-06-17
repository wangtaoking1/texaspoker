#!/bin/sh
if [[ ! -d data_out ]]; then 
echo "create data_out folder"
mkdir data_out
fi 
counter=10
if [  ! -z "$1" ]; then
	counter=$1
fi
echo $counter

# rm data_out/data.csv
rm data.csv
touch data_out/data.csv
sec=0
for i in $(seq 1 $counter)
do
	echo "Running"$i
	./run.sh
	ps t
	while [ ! -f "data.csv" ]
	do
		let sec=sec+5
		if [ $sec == 60 ]; then
			sec=0
			break
		fi
		sleep 5
	done
	if [ $i != 1 ]; then
		echo " " >> data_out/data.csv
	fi
	cat data.csv >> data_out/data.csv
	rm data.csv
	
done

echo "Totally run $counter games."
echo "The output data is in the directory data_out/"
