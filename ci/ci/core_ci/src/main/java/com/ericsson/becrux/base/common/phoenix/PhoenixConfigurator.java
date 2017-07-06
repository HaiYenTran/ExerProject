package com.ericsson.becrux.base.common.phoenix;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hudson.FilePath;

public class PhoenixConfigurator {
    private static final String VALID_VARIABLE_NAME = "[a-zA-Z_]+[a-zA-Z0-9_]*";
    private static final String SURROUNDED_BY_QUOTES = "(^(['].*['])$)|(^[\"].*[\"]$)";
    private static final String VARIABLE_REGEX = "^[ ]*([a-zA-Z_]+[a-zA-Z0-9_]*)=(.*)[ ]*$";
    private static final String VNF_FILE="vnf_file";
	private static final String CI_ENGINE_PARAM="ci_engine.param";
	private static final String PARAM_FILE_NAME="deploy_VNF.param";
	private static final String PARAM_FILE_COMMENT="# created parameter file";

    private FilePath paramFilesDirectory;

	/**
	 * Constructor without full path where parameter file is present
	 */
    public PhoenixConfigurator(){

    }

	/**
	 * @param paramFilesDirectory FilePath to directory where parameter file is present
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public PhoenixConfigurator(FilePath paramFilesDirectory) throws IOException, InterruptedException {
		if(paramFilesDirectory.isDirectory()){
			this.paramFilesDirectory = paramFilesDirectory;
		}
	}

    public FilePath getParamFilesDirectory() {
        return paramFilesDirectory;
    }

    public void setParamFilesDirectory(FilePath paramFilesDirectory) {
        this.paramFilesDirectory = paramFilesDirectory;
    }

	/**
	 * @param fileName String with name of the file parameters
	 * @param variableName name of the variable to be modified
	 * @param value new variable content
	 * @throws IOException
	 * @throws InterruptedException
	 */
    public void addParameter(String fileName, String variableName, String value) throws IOException, InterruptedException{
		addOrModifyVariable(getFileName(fileName), variableName, value);
	}

    private FilePath getFileName(String fileName) throws IOException {
		if(paramFilesDirectory!=null){
			return new FilePath(paramFilesDirectory, fileName);
		}
		else{
			return new FilePath(new File(fileName));
		}
	}

	/**
	 * @param fileName String with name of the file parameters
	 * @param variableName name of the variable to be removed
	 * @throws IOException
	 * @throws InterruptedException
	 */
    public void removeParameter(String fileName, String variableName) throws IOException, InterruptedException{
		removeVariable(getFileName(fileName),variableName);
	}

    private void addOrModifyVariable(FilePath path, String variableName, String value) throws IOException, InterruptedException{
		List<String> lines = new ArrayList<>(readOrCreateFile(path));
		if(lines == null || lines.isEmpty())
			throw new IOException("Lines are empty");
		if(!isValidVariableName(variableName))
			return;
		String regex=getVariableRegex(variableName);
		ListIterator<String> i = lines.listIterator();
		boolean variableExists=false;
		while(i.hasNext()) {
		    if(i.next().matches(regex)){
		    	i.set(variableName+"="+prepareVariableFormat(value));
		    	variableExists=true;
		    }
		}
		if(!variableExists){
			lines.add(variableName+"="+prepareVariableFormat(value));
		}
		writeFile(path,lines);
	}

    private void removeVariable(FilePath path, String variableName) throws IOException, InterruptedException{
		List<String> lines = readOrCreateFile(path);
		if(!isValidVariableName(variableName))
		{
			return;
		}
		String regex=getVariableRegex(variableName);
		ListIterator<String> i = lines.listIterator();
		while(i.hasNext()) {
		    if(i.next().matches(regex)){
		    	i.remove();
		    }
		}
		writeFile(path,lines);
	}

    private String getVariableRegex(String variable) {
        return "^[ ]*(" + variable + ")=(.*)$";
    }

	/**
	 * Get all the parameters from param file
	 *
	 * @param fileName String with name of the file parameters
	 * @return HashMap containing key values string parameters
	 * @throws IOException
	 * @throws InterruptedException
	 */
    public HashMap<String,String> getVariables(String fileName) throws IOException, InterruptedException{
		HashMap<String,String> map = new HashMap<>();
		List<String> fileLines = readOrCreateFile(getFileName(fileName));
		for(String line : fileLines){
			Pattern pattern = Pattern.compile(VARIABLE_REGEX);
			Matcher matcher = pattern.matcher(line);
			String tempVariable=null;
			String tempValue=null;
			while (matcher.find()) {
				tempVariable = (matcher.group(1) != null) ? matcher.group(1) : "";
				tempValue = (matcher.group(2) != null) ? matcher.group(2) : "";
			}
			if (matcher.matches() && tempVariable!=null && tempValue!=null)
				map.put(tempVariable, tempValue);
		}
		return map;
	}

    private List<String> readOrCreateFile(FilePath path) throws IOException,InterruptedException{
    	try{
    		return new ArrayList<>(Arrays.asList(path.readToString().split(System.lineSeparator())));
    	}
    	catch(IOException e){
    		InputStream inputStream = new ByteArrayInputStream(PARAM_FILE_COMMENT.getBytes(StandardCharsets.UTF_8));
    		path.copyFrom(inputStream);
    		return new ArrayList<>(Arrays.asList(path.readToString().split(System.lineSeparator())));
    	}
    }

    private void writeFile(FilePath path,List<String> lines) throws IOException, InterruptedException{
		StringBuilder sb = new StringBuilder();
		lines.forEach(l -> sb.append(l).append(System.lineSeparator()));
		path.write(sb.toString(), StandardCharsets.UTF_8.name());
	}

    private boolean isValidVariableName(String variableName) {
        return variableName.matches(VALID_VARIABLE_NAME);
    }

    private String prepareVariableFormat(String variable) {
        if (variable.matches(SURROUNDED_BY_QUOTES))
            return variable;
        else {

            return "\"" + variable + "\"";
        }
    }

	/**
	 * Add vnf file variable to parameters file
	 * @param vnf_file value of this parameter
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void addVnfFile(String vnfValue) throws IOException, InterruptedException{
		addParameter(PARAM_FILE_NAME,VNF_FILE,vnfValue);
	}
}
