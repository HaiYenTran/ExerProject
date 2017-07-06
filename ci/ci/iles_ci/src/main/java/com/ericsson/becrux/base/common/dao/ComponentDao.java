package com.ericsson.becrux.base.common.dao;

import com.ericsson.becrux.base.common.core.ComponentFactory;
import com.ericsson.becrux.base.common.data.Component;

import java.io.IOException;
import java.util.List;

/**
 * The DAO for Components.
 */
public interface ComponentDao extends CommonDao {

    /**
     * Get {@link ComponentFactory}
     * @return
     */
    ComponentFactory getNodeFactory();

    /**
     * Set {@link ComponentFactory}
     * @param nodeFactory
     */
    void setNodeFactory(ComponentFactory nodeFactory);

    /**
     * Saves component to the database
     *
     * @param component - component which will be saved
     * @throws IOException in case of problems with database access
     */
    void saveComponent(Component component) throws IOException;

    /**
     * Loads specific component in specific version from the database
     *
     * @param type    type of the component which will be loaded
     * @param version version of the component which will be loaded
     * @return component or null if component doesn't exist in database
     * @throws IOException in case of problems with database access
     */
    Component loadComponent(String type, String version) throws IOException;

    /**
     * Loads newest specific component from the database
     *
     * @param type type of the component which will be loaded
     * @return component or null if component doesn't exist in database
     * @throws IOException in case of problems with database access
     */
    Component loadNewestComponent(String type) throws IOException;

    /**
     * Loads newest component in specific state from the database
     *
     * @param type   type of the component which will be loaded
     * @param states only load component with one these states
     * @return component or null if component doesn't exist in database
     * @throws IOException in case of problems with database access
     */
    Component loadNewestComponent(String type, Component.State... states) throws IOException;

    /**
     * Removes a specific component from the database
     *
     * @param type    type of the component which will be removed
     * @param version version of the component which will be removed
     * @throws IOException in case of problems with database access
     */
    void removeComponent(String type, String version) throws IOException;

    /**
     * Retrieves state of the specified component
     *
     * @param component component which state we want to get
     * @return component state or null if given component does not exist
     * @throws IOException in case of problems with database access
     */
    Component.State loadComponentState(Component component) throws IOException;

    /**
     * Retrieves state of component specified with type and version
     *
     * @param type    type of the component to retrieve
     * @param version version of the component to retrieve
     * @return component state of null if given component does not exist
     * @throws IOException in case of problems with database access
     */
    Component.State loadComponentState(String type, String version) throws IOException;

    /**
     * Sets the state of specified component in the database
     *
     * @param component component which will have it's state changed
     * @param state     state to which it will be set
     * @throws IOException in case of problems with database access
     */
    void saveComponentState(Component component, Component.State state) throws IOException;

    /**
     * Sets the state of specified component in the database
     *
     * @param type    type of the component which will have it's state changed
     * @param version version of the component which will have it's state changed
     * @param state   state to which it will be set
     * @throws IOException in case of problems with database access
     */
    void saveComponentState(String type, String version, Component.State state) throws IOException;

    /**
     * Loads all component by type.
     * @param type
     * @return
     * @throws IOException
     */
    List<Component> loadAllComponents(String type, Component.State... states) throws IOException;
}
