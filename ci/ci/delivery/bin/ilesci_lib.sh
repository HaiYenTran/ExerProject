 #!/bin/bash
########################### REFERENCE #############################
Black='\033[0;30m'		; Dark_gray='\033[1;30m'
Red='\033[0;31m'		; Light_red='\033[1;31m'
Green='\033[0;32m'		; Light_green='\033[1;32m'
Brown_orange='\033[0;33m'	; Yellow='\033[1;33m'
Blue='\033[0;34m'		; Light_blue='\033[1;34m'
Purple='\033[0;35m'		; Light_purple='\033[1;35m'
Cyan='\033[0;36m'		; Light_cyan='\033[1;36m'
Light_gray='\033[0;37m'		; White='\033[1;37m'
No_color='\033[0m'		; Bold='\033[1m'
Underline='\033[4m'		; NoBold='\033[0m' 
GD_div============================================================
GD_div=$GD_div$GD_div
GD_width=57
GD_line="\t${Blue}%$GD_width.${GD_width}s${Black}\n"
GD_option="\t\t${Green}%-40s ${Black}%-90s\n"
########################### DECLATION #############################
#---------------------------- Library -----------------------------
function Fun_highlight_header() {
    printf >&2 "\n$GD_line" "$GD_div"; echo >&2 -e "\t\t\t${Bold}${Light_red}* $@${NoBold}${Black}"; printf >&2 "$GD_line" "$GD_div"
}
#------------------------------------------------------------------
function Fun_is_value() {
    if [[ $1 != -* ]] && [[ "$1" != "" ]]; then
		echo true
    else
		echo false
    fi
}
#------------------------------------------------------------------
function Fun_remove_last_slash_if_any() {
	[ "${1: -1}" == "/" ] && echo ${1%?} || echo $1
}
#------------------------------------------------------------------
function Fun_get_mail(){
    echo $(/usr/bin/ldapsearch -b o=ericsson -h ecd.ericsson.se -x -LLL -D uid=fvactool,ou=users,ou=internal,o=ericsson -w Ericsson111 "(&(uid=$1))" | grep mail: | awk '{print $2}')
} 
#------------------------------------------------------------------
function module
{
    eval $(/app/modules/0/bin/modulecmd bash "$@")
}
export -f module
#------------------------------------------------------------------
function Fun_get_filename_noext(){
    local LC_file=${1##*/}; echo ${LC_file%.*}
}
#------------------------------------------------------------------
