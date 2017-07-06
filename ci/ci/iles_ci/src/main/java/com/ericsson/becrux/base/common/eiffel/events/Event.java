package com.ericsson.becrux.base.common.eiffel.events;

import com.ericsson.becrux.base.common.eventhandler.EventValidationResult;
import com.ericsson.becrux.base.common.utils.StringHelper;
import com.ericsson.duraci.eiffelmessage.messages.EiffelEvent;
import com.ericsson.duraci.eiffelmessage.messages.EiffelMessage;
import com.google.gson.Gson;

import java.util.*;

public abstract class Event {
    private final String ID = StringHelper.generateID(this.getClass().getSimpleName());

    protected static transient String productExtensionRegex = "^.*.((tar.gz)|(tgz)|(tar)|(ova)|(ova.gz))$";
    //private static String PARENT_PAGEKAGE = "com.ericsson.becrux";
    private static volatile transient Map<String, Class> eventMap; //Map of classes inheriting from this one
    private static volatile transient Gson serializer; //Serializer object
    private static final transient Object lock = new Object(); //Lock for initialization of above

    // Held internally to perform polymorphic deserialization from json
    private String type;
    //Event come from Queue or Eiffel message Receiver
    private boolean isEventFromQueue = false;
    // CIid for LEO
    private String buildId;
    private transient EiffelMessage sourceEiffelMessage;

    private transient EiffelEvent sourceEiffelEvent;

    public Event() {
        this.type = this.getClass().getSimpleName();
    }

    public String getType() {
        return type;
    }

    public boolean isEventFromQueue() {
        return isEventFromQueue;
    }

    public void setEventFromQueue(boolean eventFromQueue) {
        isEventFromQueue = eventFromQueue;
    }

    public String getBuildId() {
        return buildId;
    }

    public void setBuildId(String buildId) {
        this.buildId = buildId;
    }

    public EiffelMessage getSourceEiffelMessage() {
        return sourceEiffelMessage;
    }

    public void setSourceEiffelMessage(EiffelMessage sourceEiffelMessage) {
        this.sourceEiffelMessage = sourceEiffelMessage;
    }

    public EiffelEvent getSourceEiffelEvent() {
        if (sourceEiffelEvent == null && sourceEiffelMessage != null)
            return sourceEiffelMessage.getEvent();
        return sourceEiffelEvent;
    }

    public void setSourceEiffelEvent(EiffelEvent sourceEiffelEvent) {
        this.sourceEiffelEvent = sourceEiffelEvent;
    }

    public abstract EventValidationResult validate();

    public String getID() { return this.ID; }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((buildId == null) ? 0 : buildId.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ID.hashCode();

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Event other = (Event) obj;
        if (!this.getID().equals(other.getID())) { return false; }
//        if (buildId == null) {
//            if (other.buildId != null)
//                return false;
//        } else if (!buildId.equals(other.buildId))
//            return false;
//        if (type == null) {
//            if (other.type != null)
//                return false;
//        } else if (!type.equals(other.type))
//            return false;
        return true;
    }
}
