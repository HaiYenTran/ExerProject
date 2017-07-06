package com.ericsson.becrux.base.common.utils;

import java.util.Date;

//Represents an object with date that tells us time when this object has been passed to this class
public final class Timestamped<T> {
    private final T object;
    private final Date date;

    public Timestamped(T object) {
        if (object == null)
            throw new NullPointerException("Object cannot be null");
        this.date = new Date(); //Create date with current time
        this.object = object;
    }

    public T getObject() {
        return object;
    }

    public Date getDate() {
        return date;
    }
}