/*
Name: getNodeVersion.groovy
Description: This script help to list all approved versions of a specific IMS Node
*/

import groovy.json.JsonSlurper

def databaseDir = DBPATH
def node = NODE

/**
 * Get the state of a Node in json file format
 * @param databaseDir
 * @param jsonFile
 * @return
 */
def getComponentState (databaseDir, jsonFile) {
    //Get Artifact from Json file
    def jsonFilePath = databaseDir + "/" + jsonFile
    def object = new JsonSlurper().parseText(new File(jsonFilePath).text)
    return object.state
}

/**
 * Get list of approved version for a specific IMS Node
 * @param databaseDir
 * @param node
 * @return 
 */
def getApprovedVersions (databaseDir, node) {
    def approvedVerisonList = [] // store all Nodes approved versions
    def sortedApprovedVerisonList = [] // store all Node approved versions after sorted
    new File(databaseDir).eachFileMatch(~'^[^.].*'){ f->
        if (f.isFile() && f.toString().contains(node))  {
            if (f.name.substring(0, f.name.indexOf("_")).equals(node)) {
                if (getComponentState(databaseDir, f.name) == "BASELINE_APPROVED") {
                    // Get Node has state = BASELINE_APPROVED
                    approvedVerisonList.add(f.name.substring(f.name.indexOf("_") + 1, f.name.indexOf(".json")))
                }
            }
        }
    }

// Sort the versions 
    sortedApprovedVerisonList = approvedVerisonList.sort().reverse()
    return sortedApprovedVerisonList
}

//=================Main=================
def newApprovedVerisonList = []
newApprovedVerisonList = getApprovedVersions(databaseDir, node)

// Add the LATEST to newApprovedVerisonList
newApprovedVerisonList.add(0, "LATEST")

if (node.equals("INT")) {
    newApprovedVerisonList.add("CUSTOM")
}
else {
    newApprovedVerisonList.add(0, "CUSTOM")
    newApprovedVerisonList.add(0, "NONE")
}

return newApprovedVerisonList