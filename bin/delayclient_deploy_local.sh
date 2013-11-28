#!/bin/bash

#working dir
day=`date -d "${date}" +%d`
year=`date -d "${date}" +%Y`
month=`date -d "${date}" +%m`
time=`date +%Y-%m-%d,%H:%M:%S`

workDir=/home/hadoop/xa
logDir=${workDir}/log
runJar=${workDir}/runJar

jar="delay-client-jar-with-dependencies.jar"
testjar="delayclient_test.jar"

pwd=$(cd "$(dirname "$0")"; pwd)
nowDir=`dirname $pwd`
dist=${nowDir}/dist

#log deploy info
if [ ! -d ${logDir} ]
then
	mkdir -p ${logDir}
fi

cd ${nowDir}

mvn clean package

#***************
#copy the jar 

hostliststr="localhost"
host=`echo ${hostliststr} | awk '{split($1,a,",");for(key in a)print a[key];}'`
for node in ${host} 
do
	echo ${node}
	scp  ${dist}/${jar} ${node}:${runJar}/${testjar}
done
