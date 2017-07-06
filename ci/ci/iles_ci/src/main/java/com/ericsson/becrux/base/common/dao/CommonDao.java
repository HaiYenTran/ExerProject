package com.ericsson.becrux.base.common.dao;

import java.nio.file.Path;

/**
 * Define the Common Dao with methods.
 *
 * Created by emiwaso on 2016-12-05.
 */
public interface CommonDao {

    /**
     * Get the DAO path.
     * @return
     */
    Path getPath();

    /**
     * Set the DAO path.
     * @param path the path to Dao
     */
    void setPath(String path);

    /**
     * In case all the queue lock not initialize correctly
     */
    void checkSynchronizeLock();

    /**
     * Validate the DAO, checking for issues.
     * @return
     * @throws Exception if anything fail
     */
    boolean validate() throws Exception;

}
