#!/bin/bash

##
#@author wangtao
#@time 2014/12/16
##

file_nums=`find /home/wangtao/workspace/huawei/trunk/game/works -name "*.java" |wc -l`

code_lines=`find /home/wangtao/workspace/huawei/trunk/game/works -name "*.java" | xargs cat |wc -l`

echo

echo "Trunk:"
echo "The number of files: $file_nums"
echo "The total lines: $code_lines"


file_nums1=`find /home/wangtao/workspace/huawei/branches/mlearning/game/works -name "*.java" |wc -l`
code_lines1=`find /home/wangtao/workspace/huawei/branches/mlearning/game/works -name "*.java" | xargs cat |wc -l`
echo
echo "mlearning:"
echo "The number of files: $file_nums1"
echo "The total lines: $code_lines1"
