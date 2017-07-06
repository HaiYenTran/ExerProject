package com.ericsson.becrux.base.common.data;

import java.io.Serializable;

import static com.google.common.primitives.Ints.min;

public class Version implements Comparable<Version> {
    private String version;
    private VersionType type;

    public Version() {
        // Gson needs a public non-argument constructor
    }

    private Version(String version, VersionType type) {
        if (version == null || version.length() <= 0)
            throw new IllegalArgumentException("Version cannot be null or empty");

        this.version = version;
        this.type = type;
    }

    public static Version createVersion(String version, VersionType type) throws IllegalArgumentException {
        return new Version(version, type);
    }

    public static Version createReleaseVersion(String version) throws IllegalArgumentException {
        return createVersion(version, VersionType.RELEASE);
    }

    public static Version createCustomVersion(String version) throws IllegalArgumentException {
        return createVersion(version, VersionType.CUSTOM);
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public VersionType getVersionType() {
        return this.type;
    }

    @Override
    public int compareTo(Version that) {
        if (this == that)
            return 0;

        if (that == null)
            return 1;

        char[] thisChars = this.version.toCharArray();
        char[] thatChars = that.getVersion().toCharArray();
        for (int i = 0; i < min(thisChars.length, thatChars.length); ++i) {
            if (thisChars[i] > thatChars[i])
                return 1;
            if (thatChars[i] > thisChars[i])
                return -1;
        }

        if (thisChars.length == thatChars.length)
            return 0;
        return thisChars.length > thatChars.length ? 1 : -1;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        return prime + ((this.version == null) ? 0 : this.version.hashCode());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;

        if (other == null)
            return false;

        if (!(other instanceof Version))
            return false;

        Version otherVersion = (Version) other;
        if (!this.version.equals(otherVersion.getVersion()))
            return false;

        return true;
    }
}