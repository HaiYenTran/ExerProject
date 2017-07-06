package com.ericsson.becrux.base.common.core;

import com.ericsson.becrux.base.common.data.Version;

import java.util.List;
import java.util.Map;

/**
 * The common Becrux factory definition.
 */
public interface Factory<T> {

    /**
     * Deserialize the specified Json into an object of T type.
     * @param json
     * @return an object of T type.
     */
    T fromJson(String json);

    /**
     * Serialize the T type object into its equivalent Json representation.
     * @param object
     * @return
     */
    String toJson(T object);

    /**
     * Serialize the T type objects into its equivalent Json representation.
     * @param objs
     * @return
     */
    String convertCollectionToJson(T... objs);

    /**
     * Serialize the T type objects into its equivalent Json representation.
     * @param objs
     * @return
     */
    String convertCollectionToJson(List<T> objs);

    /**
     * Deserialize the specified Json into list objects of T type.
     * @param str
     * @return an object of T type.
     */
    List<T> fromJsonToList(String str);

    /**
     * Get all register classes in the Factory.
     * @return
     */
    Map<String, Class<? extends T>> getRegisteredClasses();

    /**
     * Get name of registered classes.
     * @return
     */
    List<String> getRegisteredClassNames();

}
