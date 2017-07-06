package com.ericsson.becrux.base.common.watcher.strategy;

import java.io.IOException;
import java.io.PrintStream;

/**
 * The strategy for Controlling service.
 * @author dung.t.bui
 */
public interface ControllingStrategy {

    /**
     * Create or Validate the database for
     * @param parentPath
     * @throws IOException
     * @return fail if having issue on the strategy database
     */
    boolean setUpStrategyDatabase(String parentPath) throws IOException;

    /**
     * Process strategy for controlling Nodes in database.
     * Everything will print in the build log.
     */
    void handle();

    /**
     * Get Logger.
     * @return
     */
    PrintStream getLogger();

    /**
     * Set Logger.
     * @param logger
     */
    void setLogger(PrintStream logger);
}
