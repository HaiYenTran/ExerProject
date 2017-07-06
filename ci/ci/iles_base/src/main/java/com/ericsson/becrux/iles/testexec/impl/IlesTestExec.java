package com.ericsson.becrux.iles.testexec.impl;

import com.ericsson.becrux.base.common.testexec.TestExec;
import com.ericsson.becrux.iles.testexec.IlesTestExecProperties;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link TestExec}
 */
public class IlesTestExec implements TestExec {

    private static final String BUILD_ID_PARAM_NAME = "buildId";
    private static final String REAL_TEST_EXECUTE_COMMAND = "java -jar {0} -c {1} -e {2} -t {3} {4} {5} {6}";
    private static final String DEFAULT_TEST_EXECUTE_COMMAND = "java -jar {0} -h";

    private IlesTestExecProperties properties;

    /**
     * Constructor.
     *
     */
    public IlesTestExec(IlesTestExecProperties properties) {
        this.properties = properties;
    }

    /** {@inheritDoc} */
    @Override
    public boolean validateProperties() throws Exception {
        // validate Text exec jar file
        File testExecJar = new File(getProperties().getIntXLocation());
        if (!testExecJar.exists()) {
            throw new FileNotFoundException("Can not find the Test Execution Jar file");
        }

        if(getProperties().getViseChannel() == null) {
            throw new NullPointerException("Vise channel not provided.");
        }

        if (getProperties().getCfgFile() == null) {
            throw new FileNotFoundException("Can not find the config.properties file");
        }


        // TODO: review this validation (should Test execution be dependent from Leo?)
        /*String jobId = getProperties().getJobID();
        if(jobId == null || jobId.isEmpty()) {
            throw new NullPointerException("JobId wasn't provided.");
        }*/

        return true;
    }

    /** {@inheritDoc} */
    @Override
    public String generateExecuteCommand() throws Exception {
        /* CMD: java -jar intX-fat-[version].jar -c, --config      /path/to/config.properties
		 * 										 -e, --executor    root@ttcn.server
		 * 									     -g, --gateway     userid@gateway.server
		 *										 -t, --testchannel name
		 * more info:
		 * https://openalm.lmera.ericsson.se/plugins/mediawiki/wiki/int/index.php/Test_Execution#Run_Network_tests
		 *
		*/
        List<String> commandParams = new ArrayList<>();
        commandParams.add(properties.getIntXLocation());
        commandParams.add(properties.getCfgFile());
        commandParams.add(properties.getExecutionServer());
        commandParams.add(properties.getViseChannel().getFullName());
        commandParams.add(properties.getGatewayServer() == null || properties.getGatewayServer().isEmpty()?
                "" : " -g " + properties.getGatewayServer());
        commandParams.add(properties.isUploadToInsight() ? " -i " : "");
        commandParams.add(properties.isUseSandboxPdbInstance()? " -s ": "");

        String cmd;
        if(getProperties().isRealTestExec())  {
            cmd = MessageFormat.format(REAL_TEST_EXECUTE_COMMAND, commandParams.toArray());
        } else {
            cmd = MessageFormat.format(DEFAULT_TEST_EXECUTE_COMMAND, commandParams.toArray());
        }

        return cmd;
    }

    public void setProperties(IlesTestExecProperties properties) {
        this.properties = properties;
    }

    /**
     * Getter.
     * @return
     */
    public IlesTestExecProperties getProperties() {
        return properties;
    }
}
