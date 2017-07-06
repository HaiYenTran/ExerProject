package com.ericsson.becrux.base.common.core;

import com.ericsson.becrux.base.common.data.Version;
import com.ericsson.becrux.base.common.data.VersionSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Abstract Factory for creating and converting type T to json.
 */
public abstract class AbstractFactory<T> implements Factory<T> {

    protected volatile RuntimeTypeAdapterFactory<T> runtimeTypeAdapterFactory;
    protected Class<T> clazz;
    protected Map<String, Class<? extends T>> registeredClasses;

    /**
     * Get Gson Serializer.
     * @return {@link Gson}
     */
    protected Gson getSerializer() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapterFactory(runtimeTypeAdapterFactory);
        gsonBuilder.registerTypeAdapter(Version.class, new VersionSerializer());
        return gsonBuilder.create();
    }

    /**
     * Constructor.
     * @param clazz the base class type creative.
     */
    protected AbstractFactory(Class<T> clazz) {
        this.clazz = clazz;
        runtimeTypeAdapterFactory = RuntimeTypeAdapterFactory.of(clazz);
        registeredClasses = new HashMap<>();
    }

    /**
     * Register a sub class of T to serializer.
     * @param classes list of sub class
     */
    public void registerSubtype(List<Class<? extends T>> classes) {
        if (classes != null && !classes.isEmpty()) {
            classes.forEach(c -> registerSubtype(c));
        }
    }

    /**
     * Register a sub classes of T to serializer.
     * @param clazz class extended T
     */
    public synchronized void registerSubtype(Class<? extends T> clazz) {
        registeredClasses.put(clazz.getSimpleName(), clazz);
        runtimeTypeAdapterFactory.registerSubtype(clazz, clazz.getSimpleName());
    }

    /** {@inheritDoc} */
    @Override
    public T fromJson(String json) {
        return getSerializer().fromJson(json, clazz);
    }

    /** {@inheritDoc} */
    @Override
    public String toJson(T object) {
        return getSerializer().toJson(object);
    }

    /** {@inheritDoc} */
    @Override
    public String convertCollectionToJson(List<T> objs) {
        // can't reuse getSerializer here ...
        return new Gson().toJson(objs);
    }

    /** {@inheritDoc} */
    @Override
    public String convertCollectionToJson(T... objs) {
        // can't reuse getSerializer here ...
        return new Gson().toJson(objs);
    }

    /** {@inheritDoc} */
    @Override
    public List<T> fromJsonToList(String str) {
        return this.getSerializer().fromJson(str, new ListOfGeneric<T>(clazz));
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Class<? extends T>> getRegisteredClasses() {
        return registeredClasses;
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getRegisteredClassNames() {
        List<String> classNames = new ArrayList<>();
        getRegisteredClasses().forEach((k,v) -> classNames.add(k));
        return classNames;
    }

    /**
     * solution for generic gson deserialization: http://stackoverflow.com/a/14139700/6087130
     * @param <X>
     */
    protected class ListOfGeneric<X> implements ParameterizedType {

        private Class<?> wrapped;

        public ListOfGeneric(Class<X> wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[] {wrapped};
        }

        @Override
        public Type getRawType() {
            return List.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }
}
