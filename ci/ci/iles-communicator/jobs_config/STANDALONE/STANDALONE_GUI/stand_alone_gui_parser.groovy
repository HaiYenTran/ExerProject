/*
Name: stand_alone_gui_parser.groovy
Author: xthengu
Description: This script help to collect all information from Standalone GUI and parse to ITR sender build step properly.
This script should be executed in "Execute system Groovy script"
*/
import hudson.model.*
import groovy.json.JsonSlurper
import hudson.model.AbstractProject

def customOption = "CUSTOM"
def latestVersion = "LATEST"
def noneOption = "NONE"
def DEBUG = "NO"

// Please add DB=<database_path> in "Execute system Groovy script" -> Advance ->Variable bindings
// Check if DB from input
def dbPath = binding.variables.get('DB')
//def dbPath = "/proj/ims_lu/cba_cde/iles_ci/ci4ci/db"
if (dbPath == "" || dbPath == null) {
    throwException ("DB is not exist from Variable bindings, please add: DB=<ILES_DB_Path>")
} else {
    dbPath = dbPath + "/" // Just make sure DB path always have / at the end
}

// Get the state of a Node in json file format
def getComponentState (databaseDir, jsonFile) {
    //Get Artifact from Json file
    def jsonFilePath = databaseDir + "/" + jsonFile
    def object = new JsonSlurper().parseText(new File(jsonFilePath).text)
    return object.state
}

// Get list of approved version for a specific IMS Node
def getApprovedVersions (databaseDir, node) {
    def approvedVerisonList = [] // store all Node approved versions
    def sortedApprovedVerisonList = [] // store all Node approved versions after sorted
    new File(databaseDir).eachFileMatch(~'^[^.].*'){ f->
        if (f.isFile() && f.toString().contains(node))  {
            if (f.name.substring(0, f.name.indexOf("_")).equals(node)) {
                if (getComponentState(databaseDir, f.name) == "BASELINE_APPROVED") {
                    // Get Node have state = BASELINE_APPROVED
                    approvedVerisonList.add(f.name.substring(f.name.indexOf("_") + 1, f.name.indexOf(".json")))
                }
            }
        }
    }

// Sort the versions 
sortedApprovedVerisonList = approvedVerisonList.sort().reverse()
return sortedApprovedVerisonList
}

// Get the latest approved version for an IMS Node
def getLatestVersion (databaseDir, node) {
    def approvedVerisonList = []
    approvedVerisonList = getApprovedVersions(databaseDir, node)
    return approvedVerisonList[0]
}

// Method to throw a Exception
def void throwException (String errMsg) {
    badgeMasg = ""
    if (JOB_DESCRIPTION != null && JOB_DESCRIPTION != ""){
        badgeMasg = JOB_DESCRIPTION + "<br>"
    }
    badgeMasg = badgeMasg + "Status: ERROR<br>" + errMsg
    build.setDescription(badgeMasg)
    throw new hudson.AbortException(errMsg)
}

// Get all information from build parameters
def ILES_TOOLS = build.getEnvironment().get('ILES_TOOLS')
def PHOENIX_TOOL_PATH = build.getEnvironment().get('Phoenix')
def PROVISIONING_TOOL_PATH = build.getEnvironment().get('Provisioning_exec')
def TESTEXEC_TOOL_PATH = build.getEnvironment().get('Test_exec')
def INSTALLATION = build.getEnvironment(listener).get('INSTALLATION')
def PROVISIONING = build.getEnvironment(listener).get('PROVISIONING')
def TESTEXEC = build.getEnvironment(listener).get('TESTEXEC')
JOB_DESCRIPTION = build.getEnvironment().get('JOB_DESCRIPTION')

// Show DEBUG log:
if (DEBUG == "YES") {
    println("")
    println("====>>PROCESS EXECUTED:====>>")
    println("INSTALLATION : " + INSTALLATION)
    println("PROVISIONING : " + PROVISIONING)
    println("TESTEXEC     : " + TESTEXEC)
    println("")
}

// Validation when no process chosen
if ((INSTALLATION == "false") && (PROVISIONING == "false") && (TESTEXEC == "false")) {
    throwException ("No process is chosen !")
}

// Define variables
def MTAS_VERSION = ""
def MTAS_VERSION_GUI = ""
def MTAS_PDB = ""

def CSCF_VERSION = ""
def CSCF_VERSION_GUI = ""
def CSCF_PDB = ""

def PCSCF_VERSION = ""
def PCSCF_VERSION_GUI = ""
def PCSCF_PDB = ""

def IBCF_VERSION = ""
def IBCF_VERSION_GUI = ""
def IBCF_PDB = ""

def AGW_VERSION = ""
def AGW_VERSION_GUI = ""
def AGW_PDB = ""

def TRGW_VERSION = ""
def TRGW_VERSION_GUI = ""
def TRGW_PDB = ""


def MTAS_INSTALLABLE = "false"
def CSCF_INSTALLABLE = "false"
def PCSCF_INSTALLABLE = "false"
def IBCF_INSTALLABLE = "false"
def AGW_INSTALLABLE = "false"
def TRGW_INSTALLABLE = "false"
def INT_VERSION = ""

def PHOENIX_TOOL_PATH_GUI = ""
def PROVISIONING_TOOL_PATH_GUI = ""
def TESTEXEC_TOOL_PATH_GUI = ""

if (ILES_TOOLS == "true") {
    if (INSTALLATION == "true") {
        PHOENIX_TOOL_PATH_GUI = (PHOENIX_TOOL_PATH != "") ? PHOENIX_TOOL_PATH : throwException ("ILES_TOOLS: Phoenix is empty!")
    }

    if (PROVISIONING == "true") {
        PROVISIONING_TOOL_PATH_GUI = (PROVISIONING_TOOL_PATH != "") ? PROVISIONING_TOOL_PATH : throwException ("ILES_TOOLS: Provisioning_exec is empty!")
    }

    if (TESTEXEC == "true") {
        TESTEXEC_TOOL_PATH_GUI = (TESTEXEC_TOOL_PATH != "") ? TESTEXEC_TOOL_PATH : throwException ("ILES_TOOLS: Test_exec is empty!")
    }
}
if (INSTALLATION == "true") {
    // Get IMS Node Versions from build environment variables
    MTAS_VERSION = build.getEnvironment(listener).get('MTAS_VERSION')
    CSCF_VERSION = build.getEnvironment(listener).get('CSCF_VERSION')
    PCSCF_VERSION = build.getEnvironment(listener).get('PCSCF_VERSION')
    IBCF_VERSION = build.getEnvironment(listener).get('IBCF_VERSION')
    AGW_VERSION = build.getEnvironment(listener).get('AGW_VERSION')
    TRGW_VERSION = build.getEnvironment(listener).get('TRGW_VERSION')

    // Validation IMS Node Versions
    //-> MTAS
    if (MTAS_VERSION == customOption) {
        MTAS_PDB = build.getEnvironment(listener).get('MTAS_PDB')
        MTAS_VERSION_GUI = build.getEnvironment(listener).get('MTAS_SW_or_BL')
        if (MTAS_VERSION_GUI == "") {
            throwException ("MTAS_SW_or_BL is empty!")
        }
    } else if (MTAS_VERSION == latestVersion) {
        MTAS_VERSION_GUI = getLatestVersion(dbPath, "MTAS")
    } else {
        MTAS_VERSION_GUI = MTAS_VERSION
    }
    //-> CSCF
    if (CSCF_VERSION == customOption) {
        CSCF_PDB = build.getEnvironment(listener).get('CSCF_PDB')
        CSCF_VERSION_GUI = build.getEnvironment(listener).get('CSCF_SW_or_BL')
        if (CSCF_VERSION_GUI == "") {
            throwException ("CSCF_SW_or_BL is empty!")
        }
    } else if (CSCF_VERSION == latestVersion) {
        CSCF_VERSION_GUI = getLatestVersion(dbPath, "CSCF")
    } else {
        CSCF_VERSION_GUI = CSCF_VERSION
    }

    //-> PCSCF
    if (PCSCF_VERSION == customOption) {
        PCSCF_PDB = build.getEnvironment(listener).get('PCSCF_PDB')
        PCSCF_VERSION_GUI = build.getEnvironment(listener).get('PCSCF_SW_or_BL')
        if (PCSCF_VERSION_GUI == "") {
            throwException ("PCSCF_SW_or_BL is empty!")
        }
    } else if (PCSCF_VERSION == latestVersion) {
        PCSCF_VERSION_GUI = getLatestVersion(dbPath, "PCSCF")
    } else {
        PCSCF_VERSION_GUI = PCSCF_VERSION
    }

    //-> IBCF
    if (IBCF_VERSION == customOption) {
        IBCF_PDB = build.getEnvironment(listener).get('IBCF_PDB')
        IBCF_VERSION_GUI = build.getEnvironment(listener).get('IBCF_SW_or_BL')
        if (IBCF_VERSION_GUI == "") {
            throwException ("IBCF_SW_or_BL is empty!")
        }
    } else if (IBCF_VERSION == latestVersion) {
        IBCF_VERSION_GUI = getLatestVersion(dbPath, "IBCF")
    } else {
        IBCF_VERSION_GUI = IBCF_VERSION
    }

    //-> AGW
    if (AGW_VERSION == customOption) {
        AGW_PDB = build.getEnvironment(listener).get('AGW_PDB')
        AGW_VERSION_GUI = build.getEnvironment(listener).get('AGW_SW_or_BL')
        if (AGW_VERSION_GUI == "") {
            throwException ("AGW_SW_or_BL is empty!")
        }
    } else if (AGW_VERSION == latestVersion) {
        AGW_VERSION_GUI = getLatestVersion(dbPath, "AGW")
    } else {
        AGW_VERSION_GUI = AGW_VERSION
    }

    //-> TRGW
    if (TRGW_VERSION == customOption) {
        TRGW_PDB = build.getEnvironment(listener).get('TRGW_PDB')
        TRGW_VERSION_GUI = build.getEnvironment(listener).get('TRGW_SW_or_BL')
        if (TRGW_VERSION_GUI == "") {
            throwException ("TRGW_SW_or_BL is empty!")
        }
    } else if (TRGW_VERSION == latestVersion) {
        TRGW_VERSION_GUI = getLatestVersion(dbPath, "TRGW")
    } else {
        TRGW_VERSION_GUI = TRGW_VERSION
    }

    // Set installable for each IMS Node
    // If MTAS_VERSION_GUI is None, so no need to install MTAS -> MTAS_INSTALLABLE = false
    MTAS_INSTALLABLE = (MTAS_VERSION == noneOption) ? "false" : "true"
    CSCF_INSTALLABLE = (CSCF_VERSION == noneOption) ? "false" : "true"
    PCSCF_INSTALLABLE = (PCSCF_VERSION == noneOption) ? "false" : "true"
    IBCF_INSTALLABLE = (IBCF_VERSION == noneOption) ? "false" : "true"
    AGW_INSTALLABLE = (AGW_VERSION == noneOption) ? "false" : "true"
    TRGW_INSTALLABLE = (TRGW_VERSION == noneOption) ? "false" : "true"

    // Print DEBUG logs
    if (DEBUG == "YES") {
        println("====>> IMS NODES VERSION ====>>")
        println("MTAS_VERSION  : " + MTAS_VERSION_GUI  + " -> MTAS_INSTALLABLE  : "  + MTAS_INSTALLABLE)
        println("CSCF_VERSION  : " + CSCF_VERSION_GUI  + " -> CSCF_INSTALLABLE  : "  + CSCF_INSTALLABLE)
        println("PCSCF_VERSION : " + PCSCF_VERSION_GUI + " -> PCSCF_INSTALLABLE : "  + PCSCF_INSTALLABLE)
        println("IBCF_VERSION  : " + IBCF_VERSION_GUI  + " -> IBCF_INSTALLABLE  : "  + IBCF_INSTALLABLE)
        println("AGW_VERSION  : " + AGW_VERSION_GUI  + " -> AGW_INSTALLABLE  : "  + AGW_INSTALLABLE)
        println("TRGW_VERSION  : " + TRGW_VERSION_GUI  + " -> TRGW_INSTALLABLE  : "  + TRGW_INSTALLABLE)
        println("")
    }
}

// Validation
def IS_ALL_NODE_VERSIONS_NONE = "false";
if (MTAS_VERSION == noneOption && CSCF_VERSION == noneOption && PCSCF_VERSION == noneOption && IBCF_VERSION == noneOption && AGW_VERSION == noneOption && TRGW_VERSION == noneOption) {
    IS_ALL_NODE_VERSIONS_NONE = "true"; //Mean when all Node Versions are NONE
}

if (INSTALLATION == "true" && PROVISIONING == "false" && TESTEXEC == "false" && IS_ALL_NODE_VERSIONS_NONE == "true") {
    throwException ("Can't run INSTALLATION with all NODE_VESION = " + noneOption)
}
if (INSTALLATION == "true" && IS_ALL_NODE_VERSIONS_NONE == "true") {
    INSTALLATION = "false" //Skip Installation of no SW is provided
}
// Update variable
def INSTALLATION_GUI = (INSTALLATION == null) ? "" : INSTALLATION
def PROVISIONING_GUI = (PROVISIONING == null) ? "" : PROVISIONING
def TESTEXEC_GUI = (TESTEXEC == null) ? "" : TESTEXEC

// Show DEBUG log:
if (DEBUG == "YES") {
    println("")
    println("====>>PROCESS CHOSEN:====>>")
    println("INSTALLATION_GUI : " + INSTALLATION_GUI)
    println("PROVISIONING_GUI : " + PROVISIONING_GUI)
    println("TESTEXEC_GUI     : " + TESTEXEC_GUI)
    println("")
}

// Add new parameters to Build Parameter Actions
def newParaAction = null
def paraValue = new ArrayList<StringParameterValue>()
paraValue.add(new StringParameterValue("INSTALLATION_GUI", INSTALLATION_GUI))
paraValue.add(new StringParameterValue("PROVISIONING_GUI", PROVISIONING_GUI))
paraValue.add(new StringParameterValue("TESTEXEC_GUI", TESTEXEC_GUI))

paraValue.add(new StringParameterValue("MTAS_VERSION_GUI", MTAS_VERSION_GUI))
paraValue.add(new StringParameterValue("MTAS_INSTALLABLE", MTAS_INSTALLABLE))

paraValue.add(new StringParameterValue("CSCF_VERSION_GUI", CSCF_VERSION_GUI))
paraValue.add(new StringParameterValue("CSCF_INSTALLABLE", CSCF_INSTALLABLE))

paraValue.add(new StringParameterValue("PCSCF_VERSION_GUI", PCSCF_VERSION_GUI))
paraValue.add(new StringParameterValue("PCSCF_INSTALLABLE", PCSCF_INSTALLABLE))

paraValue.add(new StringParameterValue("IBCF_VERSION_GUI", IBCF_VERSION_GUI))
paraValue.add(new StringParameterValue("IBCF_INSTALLABLE", IBCF_INSTALLABLE))

paraValue.add(new StringParameterValue("AGW_VERSION_GUI", AGW_VERSION_GUI))
paraValue.add(new StringParameterValue("AGW_INSTALLABLE", AGW_INSTALLABLE))

paraValue.add(new StringParameterValue("TRGW_VERSION_GUI", TRGW_VERSION_GUI))
paraValue.add(new StringParameterValue("TRGW_INSTALLABLE", TRGW_INSTALLABLE))

paraValue.add(new StringParameterValue("PHOENIX_TOOL_PATH_GUI", PHOENIX_TOOL_PATH_GUI))
paraValue.add(new StringParameterValue("PROVISIONING_TOOL_PATH_GUI", PROVISIONING_TOOL_PATH_GUI))
paraValue.add(new StringParameterValue("TESTEXEC_TOOL_PATH_GUI", TESTEXEC_TOOL_PATH_GUI))

//Replace INSTALLATION in Parameter Action
//--> TODO: create new variable INSTALLATION_GUI

def oldParamAction = build.getAction(ParametersAction.class)

if(oldParamAction != null) {
  newParaAction = oldParamAction.createUpdated(paraValue)
  build.actions.remove(oldParamAction)
} else {
  newParaAction = new ParametersAction(paraValue)
}

build.addAction(newParaAction)
//END of script