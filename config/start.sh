if [ $# -gt 0 ];then
	if [ ! -d "./history" ]; then
       		 mkdir history
        	echo "备份文件夹history已创建!"
	fi
	if [ -f "./claim-server.jar" ]; then
        	backName="claim-server`date +%Y%m%d-%T`.jar"
        	mv claim-server.jar history/${backName}
        	echo "当前程序已经备份！文件名："${backName}
		if [ -f "claim-server-1.0.0-SNAPSHOT.jar" ]; then
                	mv claim-server-1.0.0-SNAPSHOT.jar claim-server.jar
                	echo "claim-server-1.0.0-SNAPSHOT.jar重命名为claim-server.jar。"
			if [ -f "./claim-server.jar" ]; then
                	while [ "$#" -ge "1" ];do
                        	echo "正在创建节点 $1"
                        	if [ ! -d "./$1" ]; then
                                	mkdir $1
                                	cp claim-server.jar $1/claim-server.jar
                                	echo "节点 $1 已创建!"
                                	cd $1
                                	(nohup java -jar claim-server.jar --spring.profiles.active=jfhtest --server.port=$1 >nohup.out 2>&1) &
                                	echo "节点 $1 已启动！"
                        	else
                                	cp claim-server.jar $1/claim-server.jar
                                	echo "节点 $1 开始启动!"
                                	cd $1
                                	(nohup java -jar claim-server.jar --spring.profiles.active=jfhtest --server.port=$1 >nohup.out 2>&1) &
                                	echo "节点 $1 已启动！"
                        	fi
                        	cd ..
                        	shift
                	done
        	fi
        	else
                	echo "无上传文件，请上传！"
			mv history/${backName} ./claim-server.jar
        	fi
	else
		echo "当前版本的jar包不存在，请检查！"
	fi
fi

#nohup java -jar claim-server.jar --spring.profiles.active=product --server.port=9012 &
#sleep 1
#tail -f nohup.out
