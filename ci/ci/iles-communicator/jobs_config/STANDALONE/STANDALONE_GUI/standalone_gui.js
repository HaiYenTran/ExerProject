/*
jQuery script name: standalone_gui.js
Description: This jQuery script to help display the Standalone GUI much more easier, just hide or show necessary parameters
Guide to install:
    - Copy to jenkins home: <Jenkins home>/userContent/style/
    - Install "simple theme" plugin
    - In global config: at "URL of theme JS": Enter the path of js script: https://fem028-eiffel021.rnd.ki.sw.ericsson.se:8443/jenkins/userContent/style/standalone_gui.js
*/
Q(function() {
    var rebuildPage = null != Q(location).attr('href').match(new RegExp("rebuild"));
    var viseJob = null != Q(location).attr('href').match(new RegExp("VISE"));
    // Email text box
    var jobDescriptionTextBox = Q("table.parameters").find("input[value='JOB_DESCRIPTION']").parentsUntil('table').filter("tbody");
    var emailTextBox = Q("table.parameters").find("input[value='EMAIL_NOTIFICATION']").parentsUntil('table').filter("tbody");

    //Define the const for "Custom" option
    var customOption = "CUSTOM"
    var isInstallCheck = false;
    var isProvCheck = false;
    var isTestExecCheck = false;

    function hideShowOptionTextBox() {
        if ((isInstallCheck == false) && (isProvCheck == false) && (isTestExecCheck == false)) {
            // Hide job description text box
            jobDescriptionTextBox.hide();
            // Hide email text box
            emailTextBox.hide();
        }
        else {
            // Show job description text box
            jobDescriptionTextBox.show();
            // Show email text box
            emailTextBox.show();
        }
    }

    // This function will check if no INSTALLATION, PROVISIONING, TESTEXEC tick, mean hide email text box
    // This function help to check if parameter is filled or not?
    function validateParameter(para){
        var warnMsg = "<strong> \* This field must not be empty!</strong>"
        var noWarnMsg = "";
        var invalid_bg = '#ffffb2';  //yellow
        var invalid_bor = '1px solid #ff0000'; //red
        Q("table.parameters").find("input[value=" + para+ "]").parent().find("strong").remove();
        Q("table.parameters").find("input[value=" + para+ "]").parent().append( warnMsg );

        if( !Q("table.parameters").find("input[value=" + para+ "]").next().val() ) {
            Q("table.parameters").find("input[value=" + para+ "]").next().css({
                'background-color' : invalid_bg,
                'border' : invalid_bor
            });
            Q("table.parameters").find("input[value=" + para+ "]").next().focus(function() {
                Q(this).css({
                    'background-color':'',
                    'border-color':'',
                    'border-width':''
                });
            });
        }
        Q("table.parameters").find("input[value=" + para+ "]").next().focusout(function() {
            if (Q("table.parameters").find("input[value=" + para+ "]").next().val() == ''){
                Q(this).css({
                    'background-color' : invalid_bg,
                    'border' : invalid_bor
                });
            }
        });
    }

    // Show the notification for MTAS_SW_or_BL
    var hiddenInstallation = new Array();
    hiddenInstallation[hiddenInstallation.length]=Q("table.parameters").find("input[value='MTAS_VERSION']").parentsUntil('table').filter("tbody");
    hiddenInstallation[hiddenInstallation.length]=Q("table.parameters").find("input[value='MTAS_SW_or_BL']").parentsUntil('table').filter("tbody");
    hiddenInstallation[hiddenInstallation.length]=Q("table.parameters").find("input[value='MTAS_PDB']").parentsUntil('table').filter("tbody");
    hiddenInstallation[hiddenInstallation.length]=Q("table.parameters").find("input[value='CSCF_VERSION']").parentsUntil('table').filter("tbody");
    hiddenInstallation[hiddenInstallation.length]=Q("table.parameters").find("input[value='CSCF_SW_or_BL']").parentsUntil('table').filter("tbody");
    hiddenInstallation[hiddenInstallation.length]=Q("table.parameters").find("input[value='CSCF_PDB']").parentsUntil('table').filter("tbody");
    hiddenInstallation[hiddenInstallation.length]=Q("table.parameters").find("input[value='IBCF_VERSION']").parentsUntil('table').filter("tbody");
    hiddenInstallation[hiddenInstallation.length]=Q("table.parameters").find("input[value='IBCF_SW_or_BL']").parentsUntil('table').filter("tbody");
    hiddenInstallation[hiddenInstallation.length]=Q("table.parameters").find("input[value='IBCF_PDB']").parentsUntil('table').filter("tbody");
    hiddenInstallation[hiddenInstallation.length]=Q("table.parameters").find("input[value='PCSCF_VERSION']").parentsUntil('table').filter("tbody");
    hiddenInstallation[hiddenInstallation.length]=Q("table.parameters").find("input[value='PCSCF_SW_or_BL']").parentsUntil('table').filter("tbody");
    hiddenInstallation[hiddenInstallation.length]=Q("table.parameters").find("input[value='PCSCF_PDB']").parentsUntil('table').filter("tbody");
    hiddenInstallation[hiddenInstallation.length]=Q("table.parameters").find("input[value='AGW_VERSION']").parentsUntil('table').filter("tbody");
    hiddenInstallation[hiddenInstallation.length]=Q("table.parameters").find("input[value='AGW_SW_or_BL']").parentsUntil('table').filter("tbody");
    hiddenInstallation[hiddenInstallation.length]=Q("table.parameters").find("input[value='AGW_PDB']").parentsUntil('table').filter("tbody");
    hiddenInstallation[hiddenInstallation.length]=Q("table.parameters").find("input[value='TRGW_VERSION']").parentsUntil('table').filter("tbody");
    hiddenInstallation[hiddenInstallation.length]=Q("table.parameters").find("input[value='TRGW_SW_or_BL']").parentsUntil('table').filter("tbody");
    hiddenInstallation[hiddenInstallation.length]=Q("table.parameters").find("input[value='TRGW_PDB']").parentsUntil('table').filter("tbody");

    var hiddenTestexec = new Array();
    hiddenTestexec[hiddenTestexec.length]=Q("table.parameters").find("input[value='INT_VERSION']").parentsUntil('table').filter("tbody");
    hiddenTestexec[hiddenTestexec.length]=Q("table.parameters").find("input[value='ARTIFACT']").parentsUntil('table').filter("tbody");
    hiddenTestexec[hiddenTestexec.length]=Q("table.parameters").find("input[value='TESTCASE_TAGS']").parentsUntil('table').filter("tbody");
    hiddenTestexec[hiddenTestexec.length]=Q("table.parameters").find("input[value='STORE_PASSED_PCAP']").parentsUntil('table').filter("tbody");
    hiddenTestexec[hiddenTestexec.length]=Q("table.parameters").find("input[value='USE_CUSTOM_PROPERTIES']").parentsUntil('table').filter("tbody");
    hiddenTestexec[hiddenTestexec.length]=Q("table.parameters").find("input[value='CONFIG_PROPERTIES_PATH']").parentsUntil('table').filter("tbody");

    var ilesTools = new Array();
    ilesTools[ilesTools.length]=Q("table.parameters").find("input[value='Phoenix']").parentsUntil('table').filter("tbody");
    ilesTools[ilesTools.length]=Q("table.parameters").find("input[value='Provisioning_exec']").parentsUntil('table').filter("tbody");
    ilesTools[ilesTools.length]=Q("table.parameters").find("input[value='Test_exec']").parentsUntil('table').filter("tbody");
    ilesTools[ilesTools.length]=Q("table.parameters").find("input[value='config.properties']").parentsUntil('table').filter("tbody");

    if (!viseJob){
        // return if it's not the standalone GUI
        return;
    }

    // ILES_TOOLS, INSTALLATION, PROVISIONING, TESTEXEC always uncheck
    Q("table.parameters").find("input[value='ILES_TOOLS']").next().children().attr('checked',false);
    Q("table.parameters").find("input[value='INSTALLATION']").next().children().attr('checked',false);
    Q("table.parameters").find("input[value='PROVISIONING']").next().children().attr('checked',false);
    Q("table.parameters").find("input[value='TESTEXEC']").next().children().attr('checked',false);
    // At the beginning hide unnecessary options
    if( ! rebuildPage ) {
        // Show ILES_TOOLS check box
        Q("table.parameters").find("input[value='ILES_TOOLS']").parentsUntil('table').filter("tbody").show();
        // Hide all parameters for ILES_TOOLS
            for(i=0; i<ilesTools.length; i++)
                    ilesTools[i].hide();
       Q("table.parameters").find("input[value='INSTALLATION']").parentsUntil('table').filter("tbody").show();
        // Hide all parameters for INSTALLATION
            for(i=0; i<hiddenInstallation.length; i++)
                    hiddenInstallation[i].hide();

        Q("table.parameters").find("input[value='PROVISIONING']").parentsUntil('table').filter("tbody").show();

        Q("table.parameters").find("input[value='TESTEXEC']").parentsUntil('table').filter("tbody").show();

        // Hide all parameters for TESTEXEC
            for(i=0; i<hiddenTestexec.length; i++)
                    hiddenTestexec[i].hide();
        // Hide email text box
        hideShowOptionTextBox();
    }


    // If user tick ILES_TOOLS, show all options for all ILES Tools (Phoenix, provisioning, TestExec) + config.propertise
    Q("input[value='ILES_TOOLS'] + input:checkbox").on("change", function() {
        if( rebuildPage ) {
            // If rebuildPage -> show all parameters
            return;
        }
        if( Q(this).prop('checked') ) {
            Q("table.parameters").find("input[value='Phoenix']").parentsUntil('table').filter("tbody").show();
            validateParameter('Phoenix')
            
            Q("table.parameters").find("input[value='Provisioning_exec']").parentsUntil('table').filter("tbody").show();
            validateParameter('Provisioning_exec')
            
            Q("table.parameters").find("input[value='Test_exec']").parentsUntil('table').filter("tbody").show();
            validateParameter('Test_exec')
            
            Q("table.parameters").find("input[value='config.properties']").parentsUntil('table').filter("tbody").show();
            validateParameter('config.properties')
        } else {
            // Hide all parameters for ILES_TOOLS
            for(i=0; i<ilesTools.length; i++) {
                ilesTools[i].hide();
            }
        }
    });
    // If user tick "INSTALLATION" -> some advanced parameters are visible
    Q("input[value='INSTALLATION'] + input:checkbox").on("change", function() {
        if( rebuildPage ) {
            // If rebuildPage -> show all parameters
            return;
        }
        if( Q(this).prop('checked') ) {
            isInstallCheck = true;

            // Show advanced options
            Q("table.parameters").find("input[value='MTAS_VERSION']").parentsUntil('table').filter("tbody").show();
            Q("table.parameters").find("input[value='CSCF_VERSION']").parentsUntil('table').filter("tbody").show();
            Q("table.parameters").find("input[value='IBCF_VERSION']").parentsUntil('table').filter("tbody").show();
            Q("table.parameters").find("input[value='PCSCF_VERSION']").parentsUntil('table').filter("tbody").show();
            Q("table.parameters").find("input[value='AGW_VERSION']").parentsUntil('table').filter("tbody").show();
            Q("table.parameters").find("input[value='TRGW_VERSION']").parentsUntil('table').filter("tbody").show();

            Q("input[value$='MTAS_VERSION'] + select").on("change", function() {
                if( this.value == customOption ) {
                    Q("table.parameters").find("input[value='MTAS_SW_or_BL']").parentsUntil('table').filter("tbody").show();
                    Q("table.parameters").find("input[value='MTAS_PDB']").parentsUntil('table').filter("tbody").show();
                } else {
                    Q("table.parameters").find("input[value='MTAS_SW_or_BL']").parentsUntil('table').filter("tbody").hide();
                    Q("table.parameters").find("input[value='MTAS_PDB']").parentsUntil('table').filter("tbody").hide();
                }
            });

            Q("input[value='CSCF_VERSION'] + select").on("change", function() {
                if( this.value == customOption ) {
                    Q("table.parameters").find("input[value='CSCF_SW_or_BL']").parentsUntil('table').filter("tbody").show();
                    Q("table.parameters").find("input[value='CSCF_PDB']").parentsUntil('table').filter("tbody").show();
                } else {
                    Q("table.parameters").find("input[value='CSCF_SW_or_BL']").parentsUntil('table').filter("tbody").hide();
                    Q("table.parameters").find("input[value='CSCF_PDB']").parentsUntil('table').filter("tbody").hide();
                }
            });

            Q("input[value$='PCSCF_VERSION'] + select").on("change", function() {
                if( this.value == customOption ) {
                    Q("table.parameters").find("input[value='PCSCF_SW_or_BL']").parentsUntil('table').filter("tbody").show();
                    Q("table.parameters").find("input[value='PCSCF_PDB']").parentsUntil('table').filter("tbody").show();
                } else {
                    Q("table.parameters").find("input[value='PCSCF_SW_or_BL']").parentsUntil('table').filter("tbody").hide();
                    Q("table.parameters").find("input[value='PCSCF_PDB']").parentsUntil('table').filter("tbody").hide();
                }
            });

            Q("input[value$='IBCF_VERSION'] + select").on("change", function() {
                if( this.value == customOption ) {
                    Q("table.parameters").find("input[value='IBCF_SW_or_BL']").parentsUntil('table').filter("tbody").show();
                    Q("table.parameters").find("input[value='IBCF_PDB']").parentsUntil('table').filter("tbody").show();
                } else {
                    Q("table.parameters").find("input[value='IBCF_SW_or_BL']").parentsUntil('table').filter("tbody").hide();
                    Q("table.parameters").find("input[value='IBCF_PDB']").parentsUntil('table').filter("tbody").hide();
                }
            });

            Q("input[value$='AGW_VERSION'] + select").on("change", function() {
                if( this.value == customOption ) {
                    Q("table.parameters").find("input[value='AGW_SW_or_BL']").parentsUntil('table').filter("tbody").show();
                    Q("table.parameters").find("input[value='AGW_PDB']").parentsUntil('table').filter("tbody").show();
                } else {
                    Q("table.parameters").find("input[value='AGW_SW_or_BL']").parentsUntil('table').filter("tbody").hide();
                    Q("table.parameters").find("input[value='AGW_PDB']").parentsUntil('table').filter("tbody").hide();
                }
            });

            Q("input[value$='TRGW_VERSION'] + select").on("change", function() {
                if( this.value == customOption ) {
                    Q("table.parameters").find("input[value='TRGW_SW_or_BL']").parentsUntil('table').filter("tbody").show();
                    Q("table.parameters").find("input[value='TRGW_PDB']").parentsUntil('table').filter("tbody").show();
                } else {
                    Q("table.parameters").find("input[value='TRGW_SW_or_BL']").parentsUntil('table').filter("tbody").hide();
                    Q("table.parameters").find("input[value='TRGW_PDB']").parentsUntil('table').filter("tbody").hide();
                }
            });

            // check previous status
            // MTAS
            if (Q("table.parameters").find("input[value='MTAS_VERSION']").next().val() == customOption){
                Q("table.parameters").find("input[value='MTAS_SW_or_BL']").parentsUntil('table').filter("tbody").show();
                Q("table.parameters").find("input[value='MTAS_PDB']").parentsUntil('table').filter("tbody").show();
            } else {
                Q("table.parameters").find("input[value='MTAS_SW_or_BL']").parentsUntil('table').filter("tbody").hide();
                Q("table.parameters").find("input[value='MTAS_PDB']").parentsUntil('table').filter("tbody").hide();
            }
            // CSCF
            if (Q("table.parameters").find("input[value='CSCF_VERSION']").next().val() == customOption){
                Q("table.parameters").find("input[value='CSCF_SW_or_BL']").parentsUntil('table').filter("tbody").show();
                Q("table.parameters").find("input[value='CSCF_PDB']").parentsUntil('table').filter("tbody").show();
            } else {
                Q("table.parameters").find("input[value='CSCF_SW_or_BL']").parentsUntil('table').filter("tbody").hide();
                Q("table.parameters").find("input[value='CSCF_PDB']").parentsUntil('table').filter("tbody").hide();
            }
            // PCSCF
            if (Q("table.parameters").find("input[value='PCSCF_VERSION']").next().val() == customOption){
                Q("table.parameters").find("input[value='PCSCF_SW_or_BL']").parentsUntil('table').filter("tbody").show();
                Q("table.parameters").find("input[value='PCSCF_PDB']").parentsUntil('table').filter("tbody").show();
            } else {
                Q("table.parameters").find("input[value='PCSCF_SW_or_BL']").parentsUntil('table').filter("tbody").hide();
                Q("table.parameters").find("input[value='PCSCF_PDB']").parentsUntil('table').filter("tbody").hide();
            }
            // IBCF
            if (Q("table.parameters").find("input[value='IBCF_VERSION']").next().val() == customOption){
                Q("table.parameters").find("input[value='IBCF_SW_or_BL']").parentsUntil('table').filter("tbody").show();
                Q("table.parameters").find("input[value='IBCF_PDB']").parentsUntil('table').filter("tbody").show();
            } else {
                Q("table.parameters").find("input[value='IBCF_SW_or_BL']").parentsUntil('table').filter("tbody").hide();
                Q("table.parameters").find("input[value='IBCF_PDB']").parentsUntil('table').filter("tbody").hide();
            }
            // AGW
            if (Q("table.parameters").find("input[value='AGW_VERSION']").next().val() == customOption){
                Q("table.parameters").find("input[value='AGW_SW_or_BL']").parentsUntil('table').filter("tbody").show();
                Q("table.parameters").find("input[value='AGW_PDB']").parentsUntil('table').filter("tbody").show();
            } else {
                Q("table.parameters").find("input[value='AGW_SW_or_BL']").parentsUntil('table').filter("tbody").hide();
                Q("table.parameters").find("input[value='AGW_PDB']").parentsUntil('table').filter("tbody").hide();
            }
            // TRGW
            if (Q("table.parameters").find("input[value='TRGW_VERSION']").next().val() == customOption){
                Q("table.parameters").find("input[value='TRGW_SW_or_BL']").parentsUntil('table').filter("tbody").show();
                Q("table.parameters").find("input[value='TRGW_PDB']").parentsUntil('table').filter("tbody").show();
            } else {
                Q("table.parameters").find("input[value='TRGW_SW_or_BL']").parentsUntil('table').filter("tbody").hide();
                Q("table.parameters").find("input[value='TRGW_PDB']").parentsUntil('table').filter("tbody").hide();
            }

            // Validate all Custum NODE_SW_or_BL
            validateParameter('MTAS_SW_or_BL')

            validateParameter('CSCF_SW_or_BL')

            validateParameter('PCSCF_SW_or_BL')

            validateParameter('IBCF_SW_or_BL')

            validateParameter('AGW_SW_or_BL')

            validateParameter('TRGW_SW_or_BL')
        }
        else {
            isInstallCheck = false;
            // If user untick "INSTALLATION" -> hide all advanced parameters
            for(i=0; i<hiddenInstallation.length; i++)
                hiddenInstallation[i].hide();
        }

        // Hide/show email textbox
        hideShowOptionTextBox();
        //Debug code
        //alert("isInstallCheck: " + isInstallCheck + ", isProvCheck: " + isProvCheck + ", isTestExecCheck: " + isTestExecCheck);
    });

    // If user tick "PROVISIONING"
    Q("input[value='PROVISIONING'] + input:checkbox").on("change", function() {
        if( rebuildPage )
            return;
        if( Q(this).prop('checked') ) {
            isProvCheck = true;
        } else {
            // If user untick "PROVISIONING"
            isProvCheck = false;
        }
        // Hide/show email textbox
        hideShowOptionTextBox();
        //Debug code
        //alert("isInstallCheck: " + isInstallCheck + ", isProvCheck: " + isProvCheck + ", isTestExecCheck: " + isTestExecCheck);
    });

    // If user tick "TESTEXEC" -> some advanced parameters are visible
    Q("input[value='TESTEXEC'] + input:checkbox").on("change", function() {
        if( rebuildPage )
            return;
        if( Q(this).prop('checked') ) {
            isTestExecCheck = true;

            // Show advanced options
            for(i=0; i<hiddenTestexec.length; i++)
                hiddenTestexec[i].show();

            // Hide ARTIFACT
            Q("table.parameters").find("input[value='ARTIFACT']").parentsUntil('table').filter("tbody").hide();

            Q("input[value$='INT_VERSION'] + select").on("change", function() {
                if( this.value == customOption ) {
                    Q("table.parameters").find("input[value='ARTIFACT']").parentsUntil('table').filter("tbody").show();
                } else {
                    Q("table.parameters").find("input[value='ARTIFACT']").parentsUntil('table').filter("tbody").hide();
                }
            });

            // check previous status
            // INT
            if (Q("table.parameters").find("input[value='INT_VERSION']").next().val() == customOption){
                Q("table.parameters").find("input[value='ARTIFACT']").parentsUntil('table').filter("tbody").show();
            } else {
                Q("table.parameters").find("input[value='ARTIFACT']").parentsUntil('table').filter("tbody").hide();
            }

            // Show the notification for ARTIFACT
            validateParameter('ARTIFACT')
        }
        else {
            isTestExecCheck = false;
            // If user untick "TESTEXEC" -> hide all advanced parameters
            for(i=0; i<hiddenTestexec.length; i++)
                hiddenTestexec[i].hide();
        }
        // Hide/show email textbox
        hideShowOptionTextBox();
        //Debug code
        //alert("isInstallCheck: " + isInstallCheck + ", isProvCheck: " + isProvCheck + ", isTestExecCheck: " + isTestExecCheck);
    });
});
