#!/bin/sh
# example: sh  delete_es_by_day.sh logstash-kettle-log logsdate 30

index_name=$policyinfo_index
daycolumn=$operateDate
savedays=$30

if [ ! -n "$savedays" ]; then
  echo "the args is not right,please input again...."
  exit 1
fi

if [ ! -n "$format_day" ]; then
   format_day='%Y%m%d'
fi

sevendayago=`date -d "-${savedays} day " +${format_day}`

curl -XDELETE "192.168.20.83:9200/${index_name}/_query?pretty" -d "
{
        "query": {
                "filtered": {
                        "filter": {
                                "bool": {
                                        "must": {
                                                "range": {
                                                        "${daycolumn}": {
                                                                "from": null,
                                                                "to": ${sevendayago},
                                                                "include_lower": true,
                                                                "include_upper": true
                                                        }
                                                }
                                        }
                                }
                        }
                }
        }
}"

echo "ok"