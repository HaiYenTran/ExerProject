package com.ericsson.becrux.base.common.watcher.strategy.impl;

import com.ericsson.becrux.base.common.watcher.strategy.ControllingStrategy;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Contain strategy for controlling Nodes in CI database.
 * @author dung.t.bui
 */
public abstract class BaseControllingStrategy implements ControllingStrategy {

    private static final String DATABASE_FOLDER = "db";

    private PrintStream logger;
    private File parentFolder;
    private File database; // this is the folder only for this strategy

    /**
     * Constructor.
     */
    public BaseControllingStrategy() throws IOException {
        this(null, null);
    }

    /**
     * Constructor.
     * @param parentPath
     */
    public BaseControllingStrategy(String parentPath) throws IOException {
        this(parentPath, null);
    }

    /**
     * Constructor.
     * @param parentPath
     * @param logger in case we want to log our process in a different way
     * @throws IOException
     */
    public BaseControllingStrategy(String parentPath, PrintStream logger) throws IOException{

        // set up the database
        if (parentPath != null)
            setUpStrategyDatabase(parentPath);

        // set up logger
        // TODO: logger can be null, need to find another way to implement LOGGER
        this.logger = logger;
    }

    /** {@inheritDoc} */
    @Override
    public boolean setUpStrategyDatabase(@Nonnull String parentPath) throws IOException{
        // validate parent path
        parentFolder = new File(parentPath);
        database = new File(parentFolder, DATABASE_FOLDER);

        createSubsFolder();

        // create node control table
        initializeData();

        return true;
    }

    /**
     * Create all subs folder.
     * Could be override for inheritance purpose.
     */
    protected void createSubsFolder() throws IOException {
        // create parentFolder if not exit
        if(!parentFolder.exists())
            parentFolder.mkdirs();

        // create database if not exit
        if (!database.exists())
            database.mkdirs();
    }

    /**
     * Init all base data if not have
     */
    protected abstract void initializeData();

    /** {@inheritDoc} */
    @Override
    public PrintStream getLogger() {
        return logger;
    }

    /** {@inheritDoc} */
    @Override
    public void setLogger(PrintStream logger) {
        this.logger = logger;
    }
}
