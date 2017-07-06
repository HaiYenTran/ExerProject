package com.ericsson.becrux.base.common.data;

public enum VersionType {
    CUSTOM("CUSTOM"),
    RELEASE("RELEASE");

    private String strRep;
    VersionType(String strRep) {
        this.strRep = strRep;
    }

    @Override
    public String toString() {
        return this.strRep;
    }

    public static VersionType get(String type) throws Exception {
        for (VersionType versionType : VersionType.values()) {
            if (type.equals(versionType.toString()))
                return versionType;
        }

        throw new Exception("Given type: " + type + " not found in enum.");
    }
}
