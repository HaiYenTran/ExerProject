#!/bin/bash

########################### PARAMETERS ############################
CFG_FILE=$1


########################### GLOBAL VARIABLES ######################
CLI_PATH=""
DEFAULT_ITR_XML=""
TMP_XML=""

########################### REFERENCE #############################
Light_red='\033[1;31m'
Light_blue='\033[1;34m'
Reset='\033[0;0m'

function Funt_job_action() {
    local LC_actions=$(echo $1 | tr ",|;" "\n") #delete,create,update,enable,disable
    local LC_config_job_file=$2
    local LC_jobname=$3
    for LC_action in $LC_actions; do
        case $LC_action in
            (enable)    echo -e "\t\t- Enable job : ${Light_blue}$LC_jobname${Reset}"
                        java -jar $CLI_PATH -s $JENKINS_URL enable-job $LC_jobname;;
            (disable)   echo -e "\t\t- Disable job: ${Light_blue}$LC_jobname${Reset}"
                        java -jar $CLI_PATH -s $JENKINS_URL disable-job $LC_jobname;;
            (delete)    echo -e "\t\t- Delete job : ${Light_red}$LC_jobname${Reset}"
                        java -jar $CLI_PATH -s $JENKINS_URL delete-job $LC_jobname;;
            (create)    echo -e "\t\t- Create job : ${Light_blue}$LC_jobname${Reset}"
                        java -jar $CLI_PATH -s $JENKINS_URL create-job $LC_jobname < $LC_config_job_file;;
            (update)    echo -e "\t\t- Update job : ${Light_red}$LC_jobname${Reset}"
                        java -jar $CLI_PATH -s $JENKINS_URL update-job $LC_jobname < $LC_config_job_file;;
            (build)     echo -e "\t\t- Build job : ${Light_red}$LC_jobname${Reset}"
                        java -jar $CLI_PATH -s $JENKINS_URL build $LC_jobname;;
        esac
    done
}

function Funt_main_action() {
    local Config_line=$1
    condition=`echo $Config_line | grep "VISE" | grep -v "#"`
    if [ "$condition" != "" ]; then
        VISE_NAME=${Config_line:0:8}
        VISE_IP=${Config_line:9}
        echo VISE_NAME=$VISE_NAME
        echo VISE_IP=$VISE_IP
        if [ "$ACTION" = "delete" ]; then
            Funt_job_action delete $TMP_XML $VISE_NAME
        elif [ "$ACTION" = "create" ]; then
            # delete before create
            Funt_job_action delete $TMP_XML $VISE_NAME

            # create ITR job
            cat $DEFAULT_ITR_XML | sed "s?NODE_DB_PATH?$NODE_DB_PATH?g" | sed "s?VISE_NUMBER?${VISE_NAME:5}?g" | sed "s?VISE_IP?$VISE_IP?g" | sed "s?CUSTOM_TAG?$CUSTOM_TAG?g" > $TMP_XML
            Funt_job_action create $TMP_XML $VISE_NAME
        elif [ "$ACTION" = "update" ]; then
            # update ITR job
            cat $DEFAULT_ITR_XML | sed "s?NODE_DB_PATH?$NODE_DB_PATH?g" | sed "s?VISE_NUMBER?${VISE_NAME:5}?g" | sed "s?VISE_IP?$VISE_IP?g" | sed "s?CUSTOM_TAG?$CUSTOM_TAG?g" > $TMP_XML
            Funt_job_action disable $TMP_XML $VISE_NAME
            Funt_job_action update $TMP_XML $VISE_NAME
            Funt_job_action enable $TMP_XML $VISE_NAME
        fi
    fi
}


#echo -e "\t\t- Parsing config file:"
while IFS= read -r Config_line; do
    Config_line="$(echo -e "${Config_line}" | tr -d '[:space:]')"
    condition=`echo $Config_line | grep "VISE" | grep -v "#"`

    case "$Config_line" in
        (\#*|\}|*\(\)\{|"") ;; #skip
        (ACTION*)          ACTION=${Config_line:7};;
        (JENKINS_URL*)     JENKINS_URL=${Config_line:12};;
        (CLI_PATH*)        CLI_PATH=${Config_line:9};;
        (DEFAULT_ITR_XML*) DEFAULT_ITR_XML=${Config_line:16};;
        (TMP_XML*)         TMP_XML=${Config_line:8};;
        (VISE*)            Funt_main_action ${Config_line};;
        (*) echo -e "\t\t- Unknown to process: ${Light_red}$Config_line${Reset}"
    esac
done < "$CFG_FILE"
