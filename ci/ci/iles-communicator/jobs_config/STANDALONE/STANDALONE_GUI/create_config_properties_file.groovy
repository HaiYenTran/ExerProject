import hudson.model.*
import groovy.json.JsonSlurper
import hudson.model.AbstractProject
import groovy.json.JsonSlurper
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

// Get variables from build parameters action
ILES_TOOLS = build.getEnvironment().get('ILES_TOOLS')
TESTEXEC = build.getEnvironment().get('TESTEXEC')
INT_VERSION = build.getEnvironment().get('INT_VERSION')
VERSION = build.getEnvironment().get('VERSION')
CONFIG_PROPERTIES_PATH = build.getEnvironment().get('config.properties')
ARTIFACT = build.getEnvironment().get('ARTIFACT')
TESTCASE_TAGS = build.getEnvironment().get('TESTCASE_TAGS')
STORE_PASSED_PCAP = build.getEnvironment().get('STORE_PASSED_PCAP')
JOB_DESCRIPTION = build.getEnvironment().get('JOB_DESCRIPTION')

// Define Global variables
customOption = "CUSTOM"
latestVersion = "LATEST"
// Example: "/proj/ims_lu/cba_cde/iles_ci/dev_db/db"
BASELINE_DB = ""
// The artifact path Global
ARTIFACT_PATH_GLOBAL = ""
CONFIG_PROPERTIES_GLOBAL = ""

/**
 * The variable is stored the default config.properties file. It's like the default value of CONFIG_PROPERTIES_PATH param
 * But we need define again when the CONFIG_PROPERTIES_PATH is empty and production case
 * Example: /proj/ims_lu/cba_cde/iles_ci/dev_db/tools/config_properties/standalone.properties
 */
CONFIG_PROPERTIES_DEFAULT = ""

/**
 * The variable will be add to INT_CONFIG parameter
 */
INT_VERSION_GUI = ""

/**
 * The variable will be transferred the config.properties to ILES CI
 */
CONFIG_PROPERTIES_GUI = ""

//TODO: Need to have a decision what INT version when user select CUSTOM
INT_VERSION_CUSTOM_DEFAULT = "1.0"

/**
 * Use for createConfigPropertiesFile function
 * The place to store any config[date].properties file will be used for testExec
 * TODO: find the way to delete the config.properties is doesn't used in this directory
 * Example: /proj/ims_lu/cba_cde/iles_ci/dev_db/tools/config_properties/tmp
 */
CONFIG_PROPERTIES_TMP = ""

/**
 * Create the map list to store the key and value from GUI
 * When we add/remove parameters from GUI for INT, we must update the map list here
 */
Map<String, String> mapParamsGUI = new HashMap<>()


/**
 * Create the config[date].properties file base on the default file and parameters from GUI
 * @param inputPath - the path of the default properties file
 * @param mapLists - the map lists contain the key and value of parameters on properties file
 * @return the config[date].properties file path
 */
def createConfigPropertiesFile (inputPath, mapLists) {
    def outputPath = CONFIG_PROPERTIES_TMP + "config" + DateTime.now().withZone(DateTimeZone.UTC).toString("MM-dd-yyyy-HH:mm:ss") + ".properties"
    BufferedReader br = new BufferedReader(new FileReader(inputPath))
    PrintWriter writer = new PrintWriter(outputPath, "UTF-8")
    try {
        StringBuilder sb = new StringBuilder()
        String line = br.readLine()

        while (line != null) {
            String lineWrite = ""
            String key = line.substring(0, line.indexOf("="))

            if (mapLists.containsKey(key)){
                writer.println(key + "=" + mapLists.get(key))
            }
            else {
                writer.println(line)
            }
            line = br.readLine()
        }
    } finally {
        br.close()
        writer.close()
    }
    return outputPath
}

/**
 * Get the state of a Node in json file format
 * @param databaseDir - path of database
 * @param jsonFile - name of Json file
 * @return state of node in Json file
 */
def getComponentState (databaseDir, jsonFile) {
    //Get Artifact from Json file
    def jsonFilePath = databaseDir + "/" + jsonFile
    def object = new JsonSlurper().parseText(new File(jsonFilePath).text)
    return object.state
}

/**
 * Get a list of approved versions for a specific IMS Node
 * @param databaseDir - path of database
 * @param node - name of IMS node
 * @return - list of approved versions for the IMS node
 */
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

/**
 * This function help to get latest approved version for INT
 * @param databaseDir - path of database 
 * @return the latest approved version
 */
def getLatestApprovedVersionINT (databaseDir) {
    def approvedVerisonList = []
    approvedVerisonList = getApprovedVersions(databaseDir, "INT")
    return approvedVerisonList[0]
}

/**
 * Get the path of Artifact for INT
 * @param databasePath - the path of database where store INT in Json format
 * @param intVersion - the version of INT to get the Artifact
 * @return the path of Artifact
 */
def getArtifactPath (databasePath, intVersion) {
    // Get the LATEST INT version
    if (intVersion == latestVersion){
        intVersion = getLatestApprovedVersionINT(databasePath)
    }
    //Get Artifact from Json file
    def INT_JSON = "INT_" + intVersion + ".json"
    def INT_PATH = databasePath + "/" + INT_JSON
    def object = new JsonSlurper().parseText(new File(INT_PATH).text)
    return object.artifact
}

/**
 * This function help to update the config.properties path.
 * @param config_properties_base - the config.properties base path will be used to update
 * @param mapParamsGUI - the map list to store the key and value from GUI
 * @return the path of updated config.properties
 */
def update_config_properties_path (config_properties_base, mapParamsGUI) {
    if (ARTIFACT_PATH_GLOBAL != ""){
        if (ARTIFACT_PATH_GLOBAL.contains("file://")) {
            ARTIFACT_PATH_GLOBAL = ARTIFACT_PATH_GLOBAL.replace("file://", "")
        }
        Path p = Paths.get(ARTIFACT_PATH_GLOBAL)
        String artifactName = p.getFileName().toString()
        String artifactPath = p.getParent().toString()
        mapParamsGUI.put("intTgzName", artifactName)
        mapParamsGUI.put("intTgzLocation", artifactPath)
    }
    else {
        throwException ("ERROR: Can't read the ARTIFACT file from database or the ARTIFACT is empty!")
    }
    if (TESTCASE_TAGS != null && TESTCASE_TAGS != ""){
        mapParamsGUI.put("testCaseTags", TESTCASE_TAGS)
    }
    if (STORE_PASSED_PCAP != null && STORE_PASSED_PCAP != ""){
        mapParamsGUI.put("storePassedTcPcap", STORE_PASSED_PCAP)
    }
    return createConfigPropertiesFile(CONFIG_PROPERTIES_GLOBAL, mapParamsGUI)
}

/**
 * Method to throw a Exception
 */
def void throwException (String errMsg) {
    badgeMasg = ""
    if (JOB_DESCRIPTION != null && JOB_DESCRIPTION != ""){
        badgeMasg = JOB_DESCRIPTION + "<br>"
    }
    badgeMasg = badgeMasg + "Status: ERROR<br>" + errMsg
    build.setDescription(badgeMasg)
    throw new hudson.AbortException(">>> " + errMsg)
}

// Get variables from advance setting of the Groovy build step
BASELINE_DB = binding.variables.get('DB')
if (BASELINE_DB == "" || BASELINE_DB == null) {
    throwException ("DB is not exist from Variable bindings, please add: DB=<ILES_DB_Path>")
} else {
    BASELINE_DB = BASELINE_DB + "/" // Just make sure DB path always have / at the end
}

CONFIG_PROPERTIES_DEFAULT = binding.variables.get('configDefault')
if (CONFIG_PROPERTIES_DEFAULT == "" || CONFIG_PROPERTIES_DEFAULT == null) {
    throwException ("configDefault is not exist from Variable bindings")
}

CONFIG_PROPERTIES_TMP = binding.variables.get('configTmp')
if (CONFIG_PROPERTIES_TMP == "" || CONFIG_PROPERTIES_TMP == null) {
    throwException ("configTmp is not exist from Variable bindings")
} else {
    CONFIG_PROPERTIES_TMP = CONFIG_PROPERTIES_TMP + "/"
}

// the variables to store it's PRODUCTION or STANDALONE
ENVSYSTEM = binding.variables.get('ENVSYSTEM')
if (ENVSYSTEM == "" || ENVSYSTEM == null) {
    throwException ("ENVSYSTEM is not exist from Variable bindings")
}

/**
 * Main function
 *
 * Process:
 *  Always update parameters from GUI into the base config.properties file
 *   + Production: update information into the config.properties default file
 *      ++ NODE GUI: only update the LATEST artifact in database
 *      ++ INT GUI: update the ARTIFACT, TESTCASE_TAGS, COLLECT_PASSED_PCAP from GUI
 *   + Standalone:
 *      ++ IF ILES_TOOLS = true: update the base config.properties file from GUI
 *         ELSE: update the base config.properties file from the config.properties default file
 *          +++ IF INT_VERSION == CUSTOM: get the ARTIFACT from GUI
 *              ELSE: get the ARTIFACT from DB
 *  Update the INT_VERSION_GUI
 *  Get the JOB_NAME
 *  Create the config.properties if any
 *  Create the build parameter action if any
 */
if (ENVSYSTEM == "STANDALONE"){
    if (TESTEXEC == "true") {
        if (ILES_TOOLS == "true"){
            if (CONFIG_PROPERTIES_PATH != ""){
                CONFIG_PROPERTIES_GLOBAL = CONFIG_PROPERTIES_PATH
            }
            else{
                throwException ("config.properties is empty!")
            }
        }
        else {
            CONFIG_PROPERTIES_GLOBAL = CONFIG_PROPERTIES_DEFAULT
        }
        // Standalone GUI
        if (INT_VERSION == customOption) {
            INT_VERSION_GUI = INT_VERSION_CUSTOM_DEFAULT
            if (ARTIFACT != ""){
                ARTIFACT_PATH_GLOBAL = ARTIFACT
            }
            else {
                throwException ("The ARTIFACT is empty!")
            }
        }
        else {
            if (INT_VERSION == latestVersion) {
                INT_VERSION_GUI = getLatestApprovedVersionINT(BASELINE_DB)
            }
            else {
                INT_VERSION_GUI = INT_VERSION
            }
            String artifactDB = getArtifactPath(BASELINE_DB, INT_VERSION)
            if (artifactDB != null && artifactDB != ""){
                ARTIFACT_PATH_GLOBAL = artifactDB
            }
            else {
                throwException ("ERROR: Can't read the ARTIFACT file from database!")
            }
        }
        CONFIG_PROPERTIES_GUI = update_config_properties_path(CONFIG_PROPERTIES_GLOBAL, mapParamsGUI)
    }
}
else if (ENVSYSTEM == "PRODUCTION") {
    CONFIG_PROPERTIES_GLOBAL = CONFIG_PROPERTIES_DEFAULT
    // PRODUCTION GUIs
    if (ARTIFACT == null) {
        // NODEs GUIs
        INT_VERSION_GUI = getLatestApprovedVersionINT(BASELINE_DB)
        String artifactDB = getArtifactPath(BASELINE_DB, "LATEST")
        if (artifactDB != null && artifactDB != ""){
            ARTIFACT_PATH_GLOBAL = artifactDB
        }
        else {
            throwException ("ERROR: Can't read the ARTIFACT file from database!")
        }
    }
    else {
        // INT GUI
        INT_VERSION_GUI = VERSION
        if (ARTIFACT != ""){
            ARTIFACT_PATH_GLOBAL = ARTIFACT
        }
        else {
            throwException ("The ARTIFACT is empty!")
        }
    }
    CONFIG_PROPERTIES_GUI = update_config_properties_path(CONFIG_PROPERTIES_GLOBAL, mapParamsGUI)
}
else {
    // Wrong env
    throwException ("The ENVSYSTEM parameter is wrong!")
}

// Add new parameters to Build Parameter Actions
newParaAction = null
paraValue = new ArrayList<StringParameterValue>()
if (CONFIG_PROPERTIES_GUI != ""){
    paraValue.add(new StringParameterValue("CONFIG_PROPERTIES_GUI", CONFIG_PROPERTIES_GUI))
}
if (INT_VERSION_GUI != ""){
    paraValue.add(new StringParameterValue("INT_VERSION_GUI", INT_VERSION_GUI))
}

if (paraValue != null && !paraValue.isEmpty()) {
    def oldParamAction = build.getAction(ParametersAction.class)

    if(oldParamAction != null) {
        newParaAction = oldParamAction.createUpdated(paraValue)
        build.actions.remove(oldParamAction)
    } else {
        newParaAction = new ParametersAction(paraValue)
    }

    build.addAction(newParaAction)
}
//END of script