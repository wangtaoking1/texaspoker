# coding: utf-8

import os
from operator import itemgetter

OUT_FILE = "average_data.out"
IN_FILE = "data_out/data.csv"
#IN_FILE = "data.csv"

def analyze_data():
	total = {}
	res = {}
	num = {} # 用来保存每个程序跑的场数，因为有可能有牌手程序会连接不上，因此每个牌手程序分别保存参赛的场数
	for i in ["1111", "2222", "3333", "4444", "5555", "6666", "7777", "8888"]:
		total[i] = {"pid": "", "Money": 0, "HandNum": 0, "ShowDownNum": 0, \
	"NetWin": 0, "NutHand": "", "Rank": 0, "Status": "", "RankOneNum": 0}
		res[i] = {"pid": "", "Money": 0, "HandNum": 0, "ShowDownNum": 0, \
	"NetWin": 0, "NutHand": "", "Rank": 0, "Status": "", "RankOneNum": 0}
		num[i] = 0
	data_file = open(IN_FILE)
	
	data_file.readline() # 跳过第一行（标题行）
	lines = data_file.readlines()
	for line in lines:
		if line.strip == "":
			continue
		each = line.split(", ")
		if each[0] == "Pid":
			continue
		if len(each) == 8:
			# print each[0]
			if each[7].strip() == "NORMAL":
				total[each[0]]["pid"] = each[0]
				total[each[0]]["Money"] += int(each[1])
				total[each[0]]["HandNum"] += int(each[2])
				total[each[0]]["ShowDownNum"] += int(each[3])
				total[each[0]]["NetWin"] += int(each[4])
				# total[each[0]]["NutHand"] += int(each[5])
				total[each[0]]["Rank"] += int(each[6])
				total[each[0]]["Status"] = "NORMAL"
				num[each[0]] += 1
				# print each[6]
				if each[6] == "1":
					total[each[0]]["RankOneNum"] += 1
			else:
				total[each[0]]["Status"] = each[7].strip()
	data_file.close()
	print num
	
	for i in ["1111", "2222", "3333", "4444", "5555", "6666", "7777", "8888"]:
		if total[i]["Status"] == "NORMAL":
			res[i]["Money"] = str(int(total[i]["Money"]) / num[i])
			res[i]["HandNum"] = str(int(total[i]["HandNum"]) / num[i])
			res[i]["ShowDownNum"] = str(int(total[i]["ShowDownNum"]) / num[i])
			res[i]["NetWin"] = str(int(total[i]["NetWin"]) / num[i])
			res[i]["Rank"] = str(int(total[i]["Rank"]) / num[i])
			res[i]["RankOneNum"] = str(total[i]["RankOneNum"])
		res[i]["Status"] = total[i]["Status"]


	res = sorted(res.iteritems(), key=lambda d:d[0], reverse = False)

	out = open(OUT_FILE, "w")
	out.write("Pid, Money, HandNum, ShowDownNum, NetWin, Rank, RankOneNum\n")
	for each in res:
		if each[1]["Status"] == "NORMAL":
			out.write(each[0] + ", ")
			#out.write(each[1]["pid"] + ", ")
			out.write(each[1]["Money"] + ", ")
			out.write(each[1]["HandNum"] + ", ")
			out.write(each[1]["ShowDownNum"] + ", ")
			out.write(each[1]["NetWin"] + ", ")
			out.write(each[1]["Rank"] + ", ")
			out.write(each[1]["RankOneNum"] + "\n")
	out.close()
	# print res
if __name__ == '__main__':
	print "Start analyzing..."
	analyze_data()
	print "Finished"
	print "Output file is " + OUT_FILE