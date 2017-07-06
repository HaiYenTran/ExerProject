package com.ericsson.becrux.iles.utils;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test {@link IlesVersionHelper}
 */
public class IlesVersionHelperTest {
    private final IlesVersionHelper versionHelper =  IlesVersionHelper.getInstance();

    /**
     * Test {@link IlesVersionHelper#isSequentialVersion(String)} in successful case.
     */
    @Test
    public void testValidateSequentialVersionSuccess() {
        String sequentialVersion = "1.0";
        assertTrue(versionHelper.isSequentialVersion(sequentialVersion));
    }

    /**
     * Test {@link IlesVersionHelper#isSequentialVersion(String)} in fail case.
     */
    @Test
    public void testValidateSequentialVersionFail() {
        String sequentialVersion = "1A.0";
        assertFalse(versionHelper.isSequentialVersion(sequentialVersion));
    }

    /**
     * Test {@link IlesVersionHelper#isRStateVersion(String)} (String)} in successful case.
     */
    @Test
    public void testValidateRStateVersionSuccess() {
        String rstateVersion1 = "R1A01";
        String rstateVersion2 = "R1A";
        String rstateVersion3 = "R1AX";
        assertTrue(versionHelper.isRStateVersion(rstateVersion1));
        assertTrue(versionHelper.isRStateVersion(rstateVersion2));
        assertTrue(versionHelper.isRStateVersion(rstateVersion3));
    }

    /**
     * Test {@link IlesVersionHelper#isRStateVersion(String)} in fail case.
     */
    @Test
    public void testValidateRStateVersionFail() {
        String rstateVersion = "RAR01";
        assertFalse(versionHelper.isRStateVersion(rstateVersion));
    }

    /**
     * Test {@link IlesVersionHelper#isMTASReleaseVersion(String)} (String)} in successful case.
     * NOTEs: This test may change follow MTAS release version format changes
     */
    @Test
    public void testValidateMTASReleaseVersionSuccess() {
        String mtasVersion1 = "vMTAS_2_0_LSV2";
        String mtasVersion2 = "vMTAS_2_LSV2";
        assertTrue(versionHelper.isMTASReleaseVersion(mtasVersion1));
        assertTrue(versionHelper.isMTASReleaseVersion(mtasVersion2));
    }

    /**
     * Test {@link IlesVersionHelper#isMTASReleaseVersion(String)} in fail case.
     * NOTEs: This test may change follow MTAS release version format changes
     */
    @Test
    public void testValidateMTASReleaseVersionFail() {
        String mtasVersion1 = "vMTAS 1";
        String mtasVersion2 = "vMTAS_2_0_3_LSV2";
        assertFalse(versionHelper.isMTASReleaseVersion(mtasVersion1));
        assertFalse(versionHelper.isMTASReleaseVersion(mtasVersion2));
    }

    /**
     * Test {@link IlesVersionHelper#isIlesCustomVersionFormat(String)}
     */
    @Test
    public void testValidateCustomVersion() {
        String customVersion1 = "sw_PCSCF_R1A01";
        String customVersion2 = "sw=PCSCF_R1A01";
        String customVersion3 = "bl_PCSCF_R1A01";
        String customVersion4 = "PCSCF_R1A01";

        assertTrue(versionHelper.isIlesCustomVersionFormat(customVersion1));
        assertTrue(versionHelper.isIlesCustomVersionFormat(customVersion2));
        assertTrue(versionHelper.isIlesCustomVersionFormat(customVersion3));
        assertFalse(versionHelper.isIlesCustomVersionFormat(customVersion4));
    }

    /**
     * Test {@link IlesVersionHelper#compareSequentialVersion(String, String)}.
     * Cases: newer, equal, older
     */
    @Test
    public void testCompareSequentialVersion() {
        String ver1 = "1.0";
        String ver2 = "1.0";
        String ver3 = "0.91";
        String ver4 = "2.1";

        assertTrue(versionHelper.compareSequentialVersion(ver1, ver2) == 0); // equal
        assertTrue(versionHelper.compareSequentialVersion(ver1, ver3) == 1); // newer
        assertTrue(versionHelper.compareSequentialVersion(ver1, ver4) == -1); // older
    }

    /**
     * Test {@link IlesVersionHelper#compareRStateVersion(String, String)}.
     * Cases: newer, equal, older
     */
    @Test
    public void testCompareRStateVersion() {
        String ver1 = "R1A02";
        String ver2 = "R1A02";
        String ver3 = "R1A01";
        String ver4 = "R2A";

        assertTrue(versionHelper.compareRStateVersion(ver1, ver2) == 0); // equal
        assertTrue(versionHelper.compareRStateVersion(ver1, ver3) == 1); // newer
        assertTrue(versionHelper.compareRStateVersion(ver1, ver4) == -1); // older
    }

    /**
     * Test {@link IlesVersionHelper#compareMTASReleaseVersion(String, String)}.
     * Cases: newer, equal, older
     * NOTEs: This test may change follow MTAS release version format changes
     */
    @Test
    public void testCompareMTASReleaseVersion() {
        String ver1 = "vMTAS_2_0_LSV2";
        String ver2 = "vMTAS_2_LSV2";
        String ver3 = "vMTAS_2_0_LSV1";
        String ver4 = "vMTAS_3_0_LSV1";

        assertTrue(versionHelper.compareMTASReleaseVersion(ver1, ver2) == 0); // equal
        assertTrue(versionHelper.compareMTASReleaseVersion(ver1, ver3) == 1); // newer
        assertTrue(versionHelper.compareMTASReleaseVersion(ver1, ver4) == -1); // older
    }

    @Test
    public void testCompareIntVersion() {
        String intVer1 = "INT-R2PR_latest";
        String intVer2 = "INT-R2PR_latest";
        String intVer3 = "INT-R1PR_latest";
        String intVer4 = "INT-R3PR_latest";
        assertTrue(versionHelper.compareIlesComponentVersions(intVer1, intVer2, "Int") == 0);
        assertTrue(versionHelper.compareIlesComponentVersions(intVer1, intVer3, "Int") == 1);
        assertTrue(versionHelper.compareIlesComponentVersions(intVer1, intVer4, "Int") == -1);
    }
}
