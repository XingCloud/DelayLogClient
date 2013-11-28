#!/bin/bash

#working dir

time=`date +%Y-%m-%d,%H:%M:%S`

workDir=/home/hadoop/xa
logDir=${workDir}/log
runJar=${workDir}/runJar

jar="delayclient_test.jar";

#hadoopsh="/usr/lib/hadoop/bin/hadoop"

fileencoding="-Dfile.encoding=UTF-8"
verboses="-XX:+HeapDumpOnOutOfMemoryError"
memarg="-server -Xms1g -Xmx1g -Xss160K"
gcarg="-XX:SurvivorRatio=16 -XX:+UseConcMarkSweepGC -XX:NewSize=256M -XX:MaxNewSize=256M -XX:+UseAdaptiveSizePolicy -XX:-ExplicitGCInvokesConcurrent -XX:+UseCMSCompactAtFullCollection -XX:CMSFullGCsBeforeCompaction=2"

main="com.xingcloud.delay.DelayClient"

hostliststr="localhost"
host=`echo ${hostliststr}|awk '{split($1,a,",");for(key in a)print a[key];}'`
for node in ${host} 
do
	echo ${node}
	echo "beforekill"
	pidlist=`ssh ${node} ps aux|grep $main|grep -v grep|awk '{print$2}'`
for pid in $pidlist
do
echo $pid
ssh ${node} kill $pid
done
    echo "afterkill"
    ssh ${node} ps aux|grep $main|grep -v grep|awk '{print$2}'
    ssh ${node} nohup /usr/java/jdk/bin/java $fileencoding $memarg $gcarg $verboses -classpath ${runJar}/${jar} $main &
done
