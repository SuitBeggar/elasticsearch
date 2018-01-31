#!/bin/bash
#ip="192.168.20.83"
#index_name="caseino_index"
#column="reportTime"
#savedays="30"


#ip和端口号
ip="192.168.20.83:9200"
echo "请ip和端口号:"
echo "[选择退出:0]"
read ip
if [ -z "$ip" ]
then
	echo "ip和端口号为空!"
	echo "否要退出?"
	echo "请选择:[Y/N]"
	read answer
	if [ $answer = Y -o $answer = y ]
	then
		exit
	else
		echo "请输入ip和端口号:"
		echo "[选择退出:0]"
		read ip
		if [ -z "$ip" ]
		then
			echo "ip和端口号为空!"
			echo "退出删除程序!"
			exit
		fi
	fi
else
	if [ $ip = 0 ]
	then
		exit
	fi
fi


#索引名称 
indexname="caseinfo_index"
echo "请输入索引名称:"
echo "[选择退出:0]"
read indexname
if [ -z "$indexname" ]
then
	echo "索引名称为空!"
	echo "否要退出?"
	echo "请选择:[Y/N]"
	read answer
	if [ $answer = Y -o $answer = y ]
	then
		exit
	else
		echo "请输入索引名称:"
		echo "[选择退出:0]"
		read indexname
		if [ -z "$indexname" ]
		then
			echo "索引名称为空!"
			echo "退出删除程序!"
			exit
		fi
	fi
else
	if [ $indexname = 0 ]
	then
		exit
	fi
fi



#时间戳字段
column="reportTime"
echo "请输入时间戳字段:"
echo "[选择退出:0]"
read column
if [ -z "$column" ]
then
	echo "时间戳字段为空!"
	echo "否要退出?"
	echo "请选择:[Y/N]"
	read answer
	if [ $answer = Y -o $answer = y ]
	then
		exit
	else
		echo "请输入时间戳字段:"
		echo "[选择退出:0]"
		read column
		if [ -z "$column" ]
		then
			echo "时间戳字段为空!"
			echo "退出删除程序!"
			exit
		fi
	fi
else
	if [ $column = 0 ]
	then
		exit
	fi
fi



#删除的天数
savedays="30"
echo "请输入删除的天数(几天前的数据):"
echo "[选择退出:0]"
read savedays
if [ -z "$savedays" ]
then
	echo "时间戳字段为空!"
	echo "否要退出?"
	echo "请选择:[Y/N]"
	read answer
	if [ $answer = Y -o $answer = y ]
	then
		exit
	else
		echo "请输入时间戳字段:"
		echo "[选择退出:0]"
		read savedays
		if [ -z "$savedays" ]
		then
			echo "时间戳字段为空!"
			echo "退出删除程序!"
			exit
		fi
	fi
else
	if [ $savedays = 0 ]
	then
		exit
	fi
fi


echo "请选择:[Y/N]"
echo $indexname"/"$column"/"$savedays
echo "请查看输入的信息是否正确?"
echo "请选择:[Y/N]"
read answer
	if [ $answer = N -o $answer = n ]
	then
		echo "输入信息有误，退出程序!"
		exit
	fi
	
	
#删除程序
if [ ! -n "$savedays" ]; then
  echo "the args is not right,please input again...."
  exit 1
fi

if [ ! -n "$format_day" ]; then
   format_day='%Y-%m-%d'
fi


sevendayago=`date -d "-$savedays day " +$format_day`
echo "正在删除请稍等。。。"
curl -XDELETE "$ip/$indexname/_query?pretty" -d"
{
  "\"query\"": {
    "\"bool\"": {
      "\"must\"": [
        {
          "\"range\"": {
            "\"$column\"": {
              "\"lt\"": "\"$sevendayago\""
            }
          }
        }
      ]
    }
  }
}"

echo "ok"