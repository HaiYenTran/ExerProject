package com.ericsson.becrux.iles.configuration;

import com.ericsson.becrux.base.common.core.BecruxDirectory;
import com.ericsson.becrux.base.common.exceptions.BecruxDirectoryException;

/**
 * <p>ILES project directory with the whole content required
 * for the proper ILES CI Engine work.</p>
 * <p>ILES directory requires the following structure:
 * <pre>
 * iles_directory    .
 .
 |-- baseline
 |   |-- *.json (all baseline approved components are required)
 |-- eventDao
 |   `-- queue
 |-- manageBTFEvents
 |   `-- fem023
 |       `-- queue
 |-- tools
 |   |-- config.properties -> /proj/ims_lu/cba_cde/int_ci/deliveries/intX/config/LATEST (the latest config.properties file )
 |   |-- intX -> /proj/ims_lu/cba_cde/int_ci/deliveries/intX/LATEST (the latest Test execution jar file)
 |   |-- phoenix -> /proj/ims_lu/cba_cde/int_ci/deliveries/phoenix/LATEST (the latest Phoenix delivery)
 |   `-- provisioning -> /proj/ims_lu/cba_cde/int_ci/tools/provision/latest (the latest Provisioning delivery)
 |-- viseManager
    |-- production
    |   |-- channelreservations
    |   |-- visechannels
    |   `-- visepools
    |-- staging
    |   |-- channelreservations
    |   |-- visechannels
    |   `-- visepools
    `-- standalone
        |-- channelreservations
        |-- visechannels
        `-- visepools

 * </pre>
 *
 */
public class IlesDirectory extends BecruxDirectory {

    /**
     * Creates ILES directory object for the given path. If the directory structure
     * is invalid, the exception will be thrown.
     *
     * @param directory          string with the path
     * @param generateComponents do not throw exception if database is empty, create it
     * @throws BecruxDirectoryException thrown if the directory structure is invalid
     */
    public IlesDirectory(String directory, boolean generateComponents) throws BecruxDirectoryException {
        super(directory, generateComponents);
    }

    /**
     * Creates ILES directory object for the given path. If the directory structure
     * is invalid, the exception will be thrown.
     *
     * @param directory string with the path
     * @throws BecruxDirectoryException thrown if the directory structure is invalid
     */
    public IlesDirectory(String directory) throws BecruxDirectoryException {
        this(directory, false);
    }

}
