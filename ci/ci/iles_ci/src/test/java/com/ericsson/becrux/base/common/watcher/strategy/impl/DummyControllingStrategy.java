package com.ericsson.becrux.base.common.watcher.strategy.impl;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Dummy inheritance from {@link BaseControllingStrategy} for testing.
 */
public class DummyControllingStrategy extends BaseControllingStrategy {

    public DummyControllingStrategy(String parentPath) throws IOException {
            super(parentPath);
    }

    public DummyControllingStrategy(String parentPath, PrintStream logger) throws IOException {
        super(parentPath, logger);
    }

    @Override
    public void handle() {

    }

    @Override
    protected void initializeData() {

    }
}
