  #!/bin/bash 
#------------------------------------------------------------------
function Fun_clone_job() {
	local LC_job_name=$1
	local LC_temp_dir=$2
	local LC_job_dir="$3/../jenkins_job"
	if [[ -f $LC_job_dir/${LC_job_name}.xml ]]; then
		[ -d $LC_temp_dir ] || mkdir -p $LC_temp_dir
		\cp -f $LC_job_dir/${LC_job_name}.xml $LC_temp_dir/
		echo $LC_temp_dir/${LC_job_name}.xml
	else
		echo >&2 -e "\t\t- Undefined Jenkins job: ${Light_red}$LC_job_name${Black}"
		echo 
	fi
}
#------------------------------------------------------------------
function Fun_clone_config() {
	local LC_config_file=$1
	local LC_temp_dir=$2
	local LC_config_dir="$3/../jenkins_global_config"
	if [[ -f $LC_config_dir/$LC_config_file ]]; then
		[ -d $LC_temp_dir ] || mkdir -p $LC_temp_dir
		\cp -f $LC_config_dir/$LC_config_file $LC_temp_dir/
		echo $LC_temp_dir/$LC_config_file
	else
		echo >&2 -e "\t\t- Undefined global config: ${Light_red}$LC_config_name${Black}"
		echo 
	fi
}
#------------------------------------------------------------------
function Fun_update_value_by_tag() {
	local LC_tag_name=$1
	local LC_tag_value=$2
	local LC_file_name=$3
	if [[ -n $LC_file_name ]]; then
		local LC_cur_value_tag=$(grep "<${LC_tag_name}>" $LC_file_name | sed 's/^[ \t]*//;s/[ \t]*$//')
		local LC_count=$(wc -w <<< "$LC_cur_value_tag")
		if [[ $LC_count -eq 1 ]]; then
			echo -e "\t\t- Modify (xml_tag:new_value): ${Light_blue}$LC_tag_name:$LC_tag_value${Black}"
			sed -i "s?${LC_cur_value_tag}?<${LC_tag_name}>${LC_tag_value}</${LC_tag_name}>?g" $LC_file_name
		else
			echo -e "\t\t- Duplicate xml tag: ${Light_red}<$LC_tag_name>${Black}. File: ${Light_red}$LC_file_name${Black}. Exit!!!"	
		fi
	else
		echo -e "\t\t- File not found to update value: ${Light_red}$LC_file_name${Black}"
	fi
}
#------------------------------------------------------------------
function Fun_replace_all_value() {
	local LC_old_value=$1
	local LC_new_value=$2
	local LC_file_name=$3
	echo -e "\t\t- ${Light_red}Replace${Black} (old_value:new_value): ${Light_blue}$LC_old_value:$LC_new_value${Black}"
	sed -i "s|$LC_old_value|$LC_new_value|g" $LC_file_name
}
#------------------------------------------------------------------
function Fun_job_action() {
	local LC_actions=$(echo $1 | tr ",|;" "\n") #delete,create,update,enable,disable,no
	local LC_config_job_file=$2
	local LC_jenkins_address=$3
	local LC_cur_script_dir=$4
	local LC_jobname=$(Fun_get_filename_noext $LC_config_job_file)
	for LC_action in $LC_actions; do
		case $LC_action in
			(enable)  echo -e "\t\t- Enable job : ${Light_blue}$LC_jobname${Black}"
					  java -jar $LC_cur_script_dir/jenkins-cli.jar -s $LC_jenkins_address enable-job $LC_jobname;;
			(disable) echo -e "\t\t- Disable job: ${Light_blue}$LC_jobname${Black}"
					  java -jar $LC_cur_script_dir/jenkins-cli.jar -s $LC_jenkins_address disable-job $LC_jobname;;
			(delete)  echo -e "\t\t- Delete job : ${Light_red}$LC_jobname${Black}"
					  java -jar $LC_cur_script_dir/jenkins-cli.jar -s $LC_jenkins_address delete-job $LC_jobname;;
			(create)  echo -e "\t\t- Create job : ${Light_blue}$LC_jobname${Black}"
					  java -jar $LC_cur_script_dir/jenkins-cli.jar -s $LC_jenkins_address create-job $LC_jobname < $LC_config_job_file;;
			(update)  echo -e "\t\t- Update job : ${Light_red}$LC_jobname${Black}"
					  java -jar $LC_cur_script_dir/jenkins-cli.jar -s $LC_jenkins_address update-job $LC_jobname < $LC_config_job_file;;
		esac
	done
}
#------------------------------------------------------------------
function Fun_create_view() {
	local LC_jenkins_view=$1
	local LC_jenkins_address=$2
	local LC_cur_script_dir=$3
	echo >&2 -e "\n\n\t\t- Create Jenkins view: ${Light_blue}$LC_jenkins_view${Black}"
	java -jar $LC_cur_script_dir/jenkins-cli.jar -s $LC_jenkins_address create-view $LC_jenkins_view < $LC_cur_script_dir/../jenkins_view/noname.xml
	echo $LC_jenkins_view
}
#------------------------------------------------------------------
function Fun_add_view() {
	local LC_job_list=$(echo $1 | tr ",|;" "\n")
	for LC_job in $LC_job_list; do
		local LC_jenkins_view=$2
		local LC_jenkins_address=$3
		local LC_cur_script_dir=$4
		echo >&2 -e "\t\t- Add Jenkins job: ${Light_blue}$LC_job${Black} to view: ${Light_blue}$LC_jenkins_view${Black}"
		java -jar $LC_cur_script_dir/jenkins-cli.jar -s $LC_jenkins_address add-job-to-view $LC_jenkins_view $LC_job 
	done
}
#------------------------------------------------------------------
#------------------------------------------------------------------

