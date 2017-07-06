 #!/bin/bash 
#------------------------------------------------------------------
function Fun_process_loop() {
	local LC_delivery_config_dir="../delivery_config"
	while test $# -gt 0; do
		IFS=':' read -r -a Env_param_array <<< "$1"
		local LC_env=${Env_param_array[0]}
		local LC_jenkins=${Env_param_array[1]}
		local LC_port=${Env_param_array[2]}
		case "$LC_env" in
			(prod*) LC_config_file="$LC_delivery_config_dir/production_jenkins.cfg"
					LC_env_full="PRODUCTION";;
			(stand*) LC_config_file="$LC_delivery_config_dir/standalone_jenkins.cfg"
					LC_env_full="STANDALONE";;
			(node*) LC_config_file="$LC_delivery_config_dir/ims_node_jenkins.cfg"
					LC_env_full="IMS NODE";;
			(gui*)  LC_config_file="$LC_delivery_config_dir/gui_jenkins.cfg"
					LC_env_full="GUI";;
			(prewash*) LC_config_file="$LC_delivery_config_dir/prewash_jenkins.cfg"
					LC_env_full="PREWASH";;
		esac

		Fun_highlight_header "DEPLOY $LC_env_full JENKINS"
		echo -en "\t--> Starting process for input env: ${Light_red}$LC_env${Black}. "
		[[ -n $LC_jenkins ]] && echo -en "Jenkins name: ${Light_red}$LC_jenkins${Black}. "
		[[ -n $LC_port    ]] && echo -en "Port: ${Light_red}$LC_port${Black}. "
		echo -e "\n"

		echo -e "\t\t- Parsing config file: ${Light_blue}$LC_config_file${Black}"		
		while IFS= read -r Config_line; do
			Config_line="$(echo -e "${Config_line}" | tr -d '[:space:]')"
			IFS=':' read -r -a LC_config_value <<< "$Config_line"
			case "$Config_line" in
				(\#*|\}|*\(\)\{|"") ;; #skip
				(*:no) echo -e "\t\t- Skip processing: ${Light_red}$Config_line${Black}";;
				(version:*)       [[ -n $GV_version ]] && LC_config_value[1]=$GV_version
							      Fun_checkout_branch   ${LC_config_value[1]} $GP_git_home;;
				(build:*)         Fun_build_project        ${LC_config_value[1]} $GP_git_home $GP_delivery_temp_dir;;
				(jenkins_param:*) [[ -z $LC_jenkins ]] && LC_jenkins=${LC_config_value[2]}
								  [[ -z $LC_port    ]] &&    LC_port=${LC_config_value[3]}
								  LC_jenkins_address=$(Fun_jenkins_address $LC_jenkins $LC_port);; 
				(deploy:*)        Fun_deploy_software ${LC_config_value[1]} $LC_jenkins_address $GP_git_home $GP_cur_script_dir;;
				(job:*)	LC_config_job_file=$(Fun_clone_job ${LC_config_value[1]} $GP_delivery_temp_dir $GP_cur_script_dir)
				[[ -f $LC_config_job_file ]] && echo -e "\n\n\t\t- Updating job: ${Light_blue}${LC_config_value[1]}${Black}";;
				(config_file:*)   LC_config_job_file=$(Fun_clone_config ${LC_config_value[1]} $GP_delivery_temp_dir $GP_cur_script_dir)
				[[ -f $LC_config_job_file ]] && echo -e "\n\n\t\t- Updating config: ${Light_blue}${LC_config_value[1]}${Black}";;
				(update_value:*)  Fun_update_value_by_tag ${LC_config_value[1]} ${LC_config_value[2]} $LC_config_job_file;;
				(replace_value:*) Fun_replace_all_value ${LC_config_value[1]} ${LC_config_value[2]} $LC_config_job_file;;
				(job_action:*)	  Fun_job_action    ${LC_config_value[1]} $LC_config_job_file $LC_jenkins_address $GP_cur_script_dir;;
				(jenkins_view:*)  LC_jenkins_view=$(Fun_create_view ${LC_config_value[1]} $LC_jenkins_address $GP_cur_script_dir);;
				(job_list:*)      Fun_add_view ${LC_config_value[1]} $LC_jenkins_view $LC_jenkins_address $GP_cur_script_dir;;
				(manage_jenkins:*) Fun_manage_jenkins ${LC_config_value[1]} $LC_jenkins_address $GP_cur_script_dir;;
				(*) echo -e "\t\t- Unknown to process: ${Light_red}$Config_line${Black}"
			esac
		done < "$LC_config_file"
		shift
	done
}
#------------------------------------------------------------------
function Fun_deploy_software() {
	local LC_projects=$(echo $1 | tr ",|;" "\n")
	local LC_jenkins_address=$2
	local LC_git_home=$3
	local LC_cur_script_dir=$4
	for LC_project in $LC_projects; do
		local LC_hpi=$(find $LC_git_home/ci/$LC_project/target/ -type f -name "${LC_project}*.hpi")
		echo -e "\n\n\t\t- Deploy software ${Light_red}$LC_project${Black} on target: ${Light_red}$LC_jenkins_address${Black}"
		java -jar $LC_cur_script_dir/jenkins-cli.jar -s $LC_jenkins_address install-plugin ${LC_hpi}
	done
}
#------------------------------------------------------------------
function Fun_jenkins_address() {
	local LC_jenkins_server=$1
	local LC_port=$2
	IFS='_' read -r -a LC_fem <<< "$LC_jenkins_server"; LC_fem=${LC_fem[0]}
	if [[ $LC_port -eq 8443 ]]; then 
		echo "https://${LC_fem}-eiffel021.rnd.ki.sw.ericsson.se:${LC_port}/jenkins"
	else
		echo "http://${LC_fem}-eiffel021.rnd.ki.sw.ericsson.se:${LC_port}"
	fi
}
#------------------------------------------------------------------
function Fun_manage_jenkins() {
	local LC_action_list=$(echo $1 | tr ",|;" "\n")
	for LC_action in $LC_action_list; do
		local LC_jenkins_address=$2
		local LC_cur_script_dir=$3
		case $LC_action in
			(restart) echo -e "\n\n\t\t- Restarting Jenkins: ${Light_red}$LC_jenkins_address${Black}..."
			java -jar $LC_cur_script_dir/jenkins-cli.jar -s $LC_jenkins_address restart;;
		esac
	done
}
#------------------------------------------------------------------

