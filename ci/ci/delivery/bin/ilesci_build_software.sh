 #!/bin/bash 
#------------------------------------------------------------------
function Fun_generate_rsa() {
	echo -e "\n\t${Red}${Bold}--> Git clone failed due to lacking SSH RSA key on Gerrit.${Black}${NoBold}\n"
	echo -e "\t- Generate SSH RSA keygen: ${Red}${Bold}true${Black}${NoBold}." 
	echo -e "\t- Command: ssh-keygen -q -t rsa -f ~/.ssh/id_rsa. Key value:"
	grep "$USER" ~/.ssh/id_rsa.pub >/dev/null
	[ $? != 0 ] && echo -e  'y\n'|ssh-keygen -q -t rsa -f ~/.ssh/id_rsa >/dev/null
	echo -e "\n\t${Blue}$(cat ~/.ssh/id_rsa.pub)${Black}"
	echo -e "\n\t- Register RSA on link: ${Red}${Bold}http://gerrit.ericsson.se/#/settings/ssh-keys${Black}${NoBold}"
	echo -e "\t${Red}${Bold}--> Start command again after added key onto Gerrit.${Black}${NoBold}\n"	
}
#------------------------------------------------------------------
function Fun_checkout_branch() {
	local LC_git_tag=$1
	local LC_git_home=$2
	module add git
	git config --global log.date local            
	git config --global push.default simple        
	git config --global merge.conflictstyle diff3 
	rm -rf $LC_git_home
	mkdir -p $LC_git_home
	export SSH_AUTH_SOCK=0
	echo -e "\t\t- Fetch fresh ILES CI version: ${Light_red}$LC_git_tag${Black} to ${Light_red}$LC_git_home${Black}\n"
LC_git_tag=""
	git clone -b ci_dev $LC_git_tag ssh://$USER@gerrit.ericsson.se:29418/INT $LC_git_home
	[ $? != 0 ] && Fun_generate_rsa && exit
}
#------------------------------------------------------------------
function Fun_build_project() {
	local LC_projects=$(echo $1 | tr ",|;" "\n")
	local LC_git_home=$2
	local LC_delivery_temp_dir=$3
	[ -d $LC_delivery_temp_dir ] || mkdir -p $LC_delivery_temp_dir
	for LC_project in $LC_projects; do
		LC_project_dir=$LC_git_home/ci/$LC_project
		LC_jenkins_log=$LC_delivery_temp_dir/mvn_build_${LC_project}.log
		LC_start_time=`date +%s`
		echo -e "\n\t\t- Building ${Red}${Bold}${LC_project}${NoBold}${Black} project by Maven command (~1 mins)..."	
		echo -e "\t\t- Note: Log file: ${Light_blue}$LC_jenkins_log${Black}\n"		
		case $LC_project in
			(core_ci) 
				mvn clean install -T 1C -f $LC_project_dir/pom.xml -Dmaven.test.skip=true -Dmaven.javadoc.skip=true -Denforcer.skip=true -Djacoco.skip=true -Dfindbugs.skip=true | tee $LC_jenkins_log;; #> $LC_jenkins_log 2>&1
			(becrux)
				mvn clean package -T 1C -f $LC_project_dir/pom.xml -Dmaven.test.skip=true -Dmaven.javadoc.skip=true -Denforcer.skip=true -Djacoco.skip=true -Dfindbugs.skip=true | tee $LC_jenkins_log;; #> $LC_jenkins_log 2>&1
			(iles-communicator)
				mvn clean package -T 1C -f $LC_project_dir/pom.xml -Dmaven.test.skip=true -Dmaven.javadoc.skip=true -Denforcer.skip=true -Djacoco.skip=true -Dfindbugs.skip=true | tee $LC_jenkins_log;; #> $LC_jenkins_log 2>&1
			(*) echo -e "\t\t- Unknown to build: ${Light_red}$LC_project${Black}"
		esac	
		grep "\[INFO\] BUILD SUCCESS" $LC_jenkins_log &>/dev/null
		if [ $? -eq 0 ]; then
			echo -en "\t\t- Build result: ${Red}${Bold}DONE${NoBold}${Black}. " 
			echo -e "Run time: $(expr `date +%s` - $LC_start_time) (sec)"
		else 

			echo -en "\t- Build result: ${Red}${Bold}FAILED${NoBold}${Black}."
			echo -e "Run time: $(expr `date +%s` - $LC_start_time) (sec). Exit!!!"
			exit
		fi
	done
}
#------------------------------------------------------------------




#------------------------------------------------------------------ 
