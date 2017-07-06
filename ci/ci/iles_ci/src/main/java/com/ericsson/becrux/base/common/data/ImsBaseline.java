package com.ericsson.becrux.base.common.data;

import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImsBaseline {
    private Map<String, String> baseline;

    public ImsBaseline() {
    }

    public ImsBaseline(List<Component> components) {
        baseline = new HashMap<>();
        for (Component c : components) {
            putComponent(c);
        }
        if (!isCorrect()) {
            throw new IllegalArgumentException("All components are needed for ImsBaseline creation!");
        }
    }

    public Map<String, Version> getBaseline() {
        Map<String, Version> map = new HashMap<>();
        for (Map.Entry<String, String> entry : baseline.entrySet()) {
            map.put(entry.getKey(), Version.createReleaseVersion(entry.getValue()));
        }
        return map;
    }

    public void setBaseline(Map<String, Version> baseline) {
        Map<String, String> oldBaseline = this.baseline;
        try {
            for (Map.Entry<String, Version> entry : baseline.entrySet()) {
                this.baseline.put(entry.getKey(), entry.getValue().getVersion());
            }
            if (!isCorrect()) {
                throw new IllegalArgumentException("All components are needed for ImsBaseline creation!");
            }
        } catch (Exception ex) {
            this.baseline = oldBaseline;
            throw ex;
        }
    }

    /**
     * Adds or replaces given component in this IMS baseline.
     *
     * @param c Component which is put into IMS baseline
     */
    public void putComponent(Component c) {
        if (baseline == null) {
            baseline = new HashMap<String, String>();
        }
        baseline.put(c.getType(), c.getVersion().getVersion());
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this);
    }

    public boolean isCorrect() {
//        if (baseline == null)
//            return false;
//        for (String t : Node.getComponents()) {
//            if (!baseline.containsKey(t)) {
//                return false;
//            }
//        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((baseline == null) ? 0 : baseline.hashCode());
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
        ImsBaseline other = (ImsBaseline) obj;
        if (baseline == null) {
            if (other.baseline != null)
                return false;
        } else if (!baseline.equals(other.baseline))
            return false;
        return true;
    }


}
