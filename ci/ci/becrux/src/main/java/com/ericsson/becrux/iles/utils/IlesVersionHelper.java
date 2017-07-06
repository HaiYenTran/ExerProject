package com.ericsson.becrux.iles.utils;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.ericsson.becrux.iles.data.Int;
import com.ericsson.becrux.iles.data.Mtas;

/**
 * Define all comparision method for all type of version.
 * This class is used for comparision purpose only.
 * NOTE: We will move the validation and comparision logic to external resource if the logic change frequently.
 */
public class IlesVersionHelper {
    private static final String SEQUENTIAL_VERSION_REGEX = "(\\d+\\.)?\\d+";
    private static final String RSTATE_VERSION_REGEX = "R\\d+[A-Z]+\\d*";
    private static final String MTAS_RELEASE_VERSION_REGEX = "^(vMTAS_)(\\d+)(_\\d)?(_LSV\\d+)";
    private static final String CUSTOM_VERSION_REGEX = "^(sw|bl)(.*)";

    private static final String INT_COMPONENT_TYPE = Int.class.getSimpleName();
    private static final String MTAS_COMPONENT_TYPE = Mtas.class.getSimpleName();

    /**
     * Default constructor.
     */
    private IlesVersionHelper(){}

    /**
     * Get Singleton Instance.
     * @return
     */
    public static IlesVersionHelper getInstance() {
        return Holder.INSTANCE;
    }

    // VALIDATION METHODS

    /**
     * Check if the input String has sequential version format.
     * Ex: 1.0, 2.1, 2.32
     * @param version the string version
     * @return true if correct format
     */
    public boolean isSequentialVersion(@Nonnull String version) {
        return isStringMatchedPattern(version, SEQUENTIAL_VERSION_REGEX);
    }

    /**
     * Check if the input String has RState version format.
     * Ex: R1A01, R31B
     * @param version the string version
     * @return true if correct format
     */
    public boolean isRStateVersion(@Nonnull String version) {
        return isStringMatchedPattern(version, RSTATE_VERSION_REGEX);
    }

    /**
     * Check if the input String has MTAS Release version format.
     * @param version the string version
     * @return true if correct format
     */
    public boolean isMTASReleaseVersion(@Nonnull String version) {
        return isStringMatchedPattern(version, MTAS_RELEASE_VERSION_REGEX);
    }

    /**
     * Check if the input String has Custom version format.
     * @param version the string version
     * @return true if correct format
     */
    public boolean isIlesCustomVersionFormat(@Nonnull String version) {
        return isStringMatchedPattern(version, CUSTOM_VERSION_REGEX);
    }

    /*
        Compare if a string matched with given pattern
     */
    private boolean isStringMatchedPattern(String str, String pattern) {
        return Pattern.compile(pattern).matcher(str).matches();
    }

    // COMPARISION METHODS

    /**
     * Compare between 2 sequential version.
     * @param version1 the string version
     * @param version2 the string version
     * @return 1 if version1 > version2, 0 if equal and -1 if version1 < version2
     */
    public int compareSequentialVersion(@Nonnull String version1, @Nonnull String version2) {
        // validation
        if (!isSequentialVersion(version1) || !isSequentialVersion(version2)) {
            throw new IllegalArgumentException("the versions are not Sequential version format.");
        }

        // compare
        // because we only got 1 sub level of version so we can convert it to float
        float version1Value = Float.valueOf(version1);
        float version2Value = Float.valueOf(version2);

        if (version1Value == version2Value) { return 0; }

        return version1Value > version2Value ? 1 : -1;
    }

    /**
     * Compare between 2 RState version.
     * @param version1
     * @param version2
     * @return 1 if version1 > version2, 0 if equal and -1 if version1 < version2
     */
    public int compareRStateVersion(@Nonnull String version1, @Nonnull String version2) {
        return compareRStateVersion(convertStringToRStateVersion(version1),convertStringToRStateVersion(version2));
    }

    /**
     * Compare between 2 RState version.
     * @param version1
     * @param version2
     * @return 1 if version1 > version2, 0 if equal and -1 if version1 < version2
     */
    private int compareRStateVersion(@Nonnull RStateVersion version1, @Nonnull RStateVersion version2) {
        if (version1.getMainPart() == version2.getMainPart()) {
            if (version1.getMiddlePart().equals(version2.getMiddlePart())) {
                if(version1.getSubPart() == version2.getSubPart()) {
                    return 0;
                } else {
                    return version1.getSubPart() > version2.getSubPart() ? 1 : -1;
                }
            } else {
                return version1.getMiddlePart().compareTo(version2.getMiddlePart()) > 0 ? 1 : -1;
            }
        } else {
            return version1.getMainPart() > version2.getMainPart() ? 1 : -1;
        }
    }

    /*
        Convert a string to RState version object.
     */
    private RStateVersion convertStringToRStateVersion(String ver) {
        if (!isRStateVersion(ver)) {
            throw new IllegalArgumentException("the versions are not RState version format.");
        }

        /*
            split string version into part with numbers/characters
            ex: ASD123S >> { ASD, 123, S}
         */
        List<String> versionParts = new LinkedList<>();
        Matcher matcher = Pattern.compile("[0-9]+|[A-Z]+").matcher(ver);
        while (matcher.find()) { versionParts.add(matcher.group()); }

        /*
            for the case RState don't have sub version. ex R1A
         */
        if (versionParts.size() == 3) { versionParts.add("0"); }

        return new RStateVersion(versionParts);
    }

    /**
     * Compare between 2 MTAS Release version.
     * @param version1 the string version
     * @param version2 the string version
     * @return 1 if version1 > version2, 0 if equal and -1 if version1 < version2
     */
    public int compareMTASReleaseVersion(@Nonnull String version1, @Nonnull String version2) {
        // validation
        if (!isMTASReleaseVersion(version1) || !isMTASReleaseVersion(version2)) {
            throw new IllegalArgumentException("the versions are not MTAS version format.");
        }

        MTASReleaseVersion mtasVer1 = convertStringToMTASReleaseVersion(version1);
        MTASReleaseVersion mtasVer2 = convertStringToMTASReleaseVersion(version2);

        if (mtasVer1.getPackageVersion() == mtasVer2.getPackageVersion()) {
            if (mtasVer1.getLsvVersion() == mtasVer2.getLsvVersion()) {
                return 0;
            } else {
                return mtasVer1.getLsvVersion() > mtasVer2.getLsvVersion()? 1 : -1;
            }
        } else {
            return mtasVer1.getPackageVersion() > mtasVer2.getPackageVersion()? 1 : -1;
        }
    }

    /*
        Convert a string to MTAS release version object.
        the format will be 'vMTAS_' + packageVersion + '_LSV' + LSV version
     */
    private MTASReleaseVersion convertStringToMTASReleaseVersion(String ver) {
        // this validation is duplicate with compareMTASReleaseVersion(), should we remove it ?
        if (!isMTASReleaseVersion(ver)) {
            throw new IllegalArgumentException("the versions are not MTAS release version format.");
        }

        List<String> versionParts = new LinkedList<>();

        // find package part
        Matcher matcher = Pattern.compile("(\\d+)(_\\d+)?(_)").matcher(ver);
        if(matcher.find()) {
            String packagePart = matcher.group(); // this will get the string like 1_1_
            packagePart = packagePart.substring(0, packagePart.length() - 1); // cut the last char '_'
            packagePart = packagePart.replace('_', '.'); // replace 1_1 to 1.1
            versionParts.add(packagePart);
        }

        // find LSV part
        matcher = Pattern.compile("(LSV\\d+)").matcher(ver);
        if(matcher.find()) {
            String lsvPart = matcher.group(); // this will get the string like LSV123
            lsvPart = lsvPart.substring(3);
            versionParts.add(lsvPart);
        }

        // convert from string to float for comparision
        float packageVersion = Float.valueOf(versionParts.get(0));
        float lsvVersion = Float.valueOf(versionParts.get(1));

        return new MTASReleaseVersion(packageVersion, lsvVersion);
    }

    /**
     * Compare 2 Iles Component Versions.
     * If product type = 'Int', will compare with sequential version format.
     * If product type = 'Mtas', will compare with MTAS release version format.
     * Others will compare with RState version formats.
     * @param version1
     * @param version2
     * @param productType
     * @return
     */
    public int compareIlesComponentVersions(@Nonnull String version1, @Nonnull String version2, @Nonnull String productType) {
        if (INT_COMPONENT_TYPE.equals(productType)) {
            return compareRStateVersion(getRStatePartFromStringVersion(version1),getRStatePartFromStringVersion( version2));
        } else if (MTAS_COMPONENT_TYPE.equals(productType)) {
            return compareMTASReleaseVersion(version1, version2);
        } else {
            // not handle the case that the input product type may be random and meaningless
            return compareRStateVersion(version1, version2);
        }
    }

    /*
        convert the string version that contain RState format (a part in there)
        ex:  INT_RANDOM_ASD_R1A01_UNKNOWN
     */
    private RStateVersion getRStatePartFromStringVersion(String ver) {
        Matcher matcher = Pattern.compile("(R[0-9]+[A-Z]+[0-9]*)").matcher(ver);
        if (matcher.find()) { return convertStringToRStateVersion(matcher.group()); }
        else { return null; }
    }

    // DEFINE THE FORMAT OF ALL VERSION

    /**
     * Present the RState version format.
     */
    private class RStateVersion {
        private String prefix = "R"; // this property only for clarify the meaning of code
        private int mainPart = 0;
        private String middlePart = "A";
        private int subPart = 0;

        /**
         * Constructors.
         * Assume RState version has 4 part { R, main part(number) , middle part(Alphabet), sub part(number) .
         *
         * @param versionParts
         */
        public RStateVersion(@Nonnull List<String> versionParts) {
            if(versionParts.size() != 4) {
                throw new IllegalArgumentException("not enough version part of RState format.");
            }

            this.prefix = versionParts.get(0);
            this.mainPart = Integer.valueOf(versionParts.get(1));
            this.middlePart = versionParts.get(2);
            this.subPart = Integer.valueOf(versionParts.get(3));
        }

        public String getPrefix() { return this.prefix; }

        public int getMainPart() { return this.mainPart; }

        public String getMiddlePart() { return this.middlePart; }

        public int getSubPart() {
            return subPart;
        }
    }

    /**
     * Present the MTAS Release Version format.
     */
    private class MTASReleaseVersion {
        private float packageVersion;
        private float lsvVersion;

        /**
         * Constructor.
         * Format of MTAS release version should be 'vMTAS <packageVersion> LSV <lsvVersion>'
         * @param packageVersion
         * @param lsvVersion
         */
        public MTASReleaseVersion(float packageVersion, float lsvVersion) {
            this.packageVersion = packageVersion;
            this.lsvVersion = lsvVersion;
        }

        public float getPackageVersion() {
            return packageVersion;
        }

        public float getLsvVersion() {
            return lsvVersion;
        }
    }

    // FOR SINGLETON

    /**
     * Inner class for lazy initialization Singleton.
     */
    private static class Holder {
        static final IlesVersionHelper INSTANCE = new IlesVersionHelper();
    }

}
