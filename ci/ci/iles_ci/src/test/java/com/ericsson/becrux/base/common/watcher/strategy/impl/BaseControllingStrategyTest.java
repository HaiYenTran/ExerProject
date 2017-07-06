package com.ericsson.becrux.base.common.watcher.strategy.impl;

import com.ericsson.becrux.base.common.watcher.strategy.ControllingStrategy;
import com.google.common.io.Files;
import org.apache.commons.io.FileDeleteStrategy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.PrintStream;

import static org.junit.Assert.assertTrue;

/**
 * Tests {@link BaseControllingStrategy}
 */
public class BaseControllingStrategyTest {

    private File dummyFolder;

    @Before
    public void setUp() {
        dummyFolder = Files.createTempDir();
    }

    @Test
    public void testInit() throws Exception {
        ControllingStrategy strategy = new DummyControllingStrategy(dummyFolder.getPath());
        assertTrue(strategy != null);

        strategy = new DummyControllingStrategy(null);
        assertTrue(strategy != null);

        File logFile = new File(dummyFolder,"log");
        logFile.createNewFile();
        strategy = new DummyControllingStrategy(null, new PrintStream(logFile));
        assertTrue(strategy != null);
        strategy.getLogger().close();
    }

    @Test
    public void testSetUpStrategyDatabase() throws Exception {
        ControllingStrategy strategy = new DummyControllingStrategy(dummyFolder.getPath());

        assertTrue(strategy != null);
        strategy.setUpStrategyDatabase(dummyFolder.getPath());
    }

    @After
    public void cleanUp() throws Exception {
        if(dummyFolder.exists()) {
            FileDeleteStrategy.FORCE.delete(dummyFolder);
        }
    }
}
