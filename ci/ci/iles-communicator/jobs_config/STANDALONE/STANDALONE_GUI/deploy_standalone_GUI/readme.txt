This script is only used for standalone GUI.
For MANAGE job, we just create manual.

Some step for run the script:
1. Please check and update the correct information in the Default_VISE_GUI.xml file.
   However, you can use the file was make by yourself.

2. Update information in the viseList_config.txt file.
   For each parameters in the file already have the description.
   Please check and update them.
   
   Note: please choose the correct option: delete/create/update
   
3. Run the deploy script
   cd to directory was included this script
   ./deploy.sh viseList_config.txt
   
   Example:
   #!/bin/bash
   cd /proj/ims_lu/cba_cde/int_ci/deliveries/ILES_CI/R1A1719/ILES_Communicator/job_config/Standalone_GUI/deployment_script
   ./deploy.sh viseList_config.txt
   
   Note: this script is requested the eif021 use.
   If you don't have permission of this user, you must run in Jenkins.
   
   Referent: https://fem002-eiffel021.rnd.ki.sw.ericsson.se:8443/jenkins/view/Management/job/Deploy_Standalone_GUI/
   
4. Advance
   The viseList_config.txt file is included all VISEs.
   If you want to run with some VISEs, you can clone to another file from viseList_config.txt.
   Example: Clone viseList_config.txt to viseList_02.txt to run for VISE02 group.
   Then update the viseList_02.txt (remove other VISE with VISE02 group)
   ./deploy.sh viseList_02.txt