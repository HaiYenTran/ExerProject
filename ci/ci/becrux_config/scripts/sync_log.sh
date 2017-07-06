#!/bin/bash

# Format SYNC_LOG_LINK=*/JOB_NAME/BUILD_NUMBER/console[Full]

Green='\033[0;32m'
Reset='\033[0;0m'

echo -e "\n\n\n$Green"
echo -e "##################################################################"
echo -e "#                          START SYNC LOG                        #"
echo -e "##################################################################"
echo -e "\n\n\n$Reset"

JAR_DIR=/home/xthdidi/THIENVU/IMS_CI/EUREKA/delivery/bin/jenkins-cli.jar
#SYNC_LOG_LINK=https://fem023-eiffel021.rnd.ki.sw.ericsson.se:8443/jenkins/job/VISE0203/1/console
SYNC_LOG_LINK=${WORKER_URL}
SYNC_LOG_LINK=${SYNC_LOG_LINK%*/console*}
condition=`echo $SYNC_LOG_LINK | grep job | grep view`
if [ "$condition" == "" ]; then
    FEM_URL=${SYNC_LOG_LINK%*/job*}
else
    FEM_URL=${SYNC_LOG_LINK%*/view*}
fi
LOG_BUILD_NUMBER=${SYNC_LOG_LINK##*/}
SYNC_LOG_LINK=${SYNC_LOG_LINK%/*}
LOG_JOB_NAME=${SYNC_LOG_LINK##*/}
java -jar $JAR_DIR -s $FEM_URL console $LOG_JOB_NAME $LOG_BUILD_NUMBER -f

echo -e "\n\n\n$Green"
echo -e "##################################################################"
echo -e "#                            END SYNC LOG                        #"
echo -e "##################################################################"
echo -e "\n\n\n$Reset"