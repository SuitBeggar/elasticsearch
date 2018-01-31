nohup ~/elk/logstash-2.3.4/bin/logstash agent ¨Cf  ~/elk/logstash-2.3.4/conf/  > logstash-2.3.4/nohup.out &
sleep 1
tail -f logstash-2.3.4/nohup.out