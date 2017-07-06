package com.ericsson.becrux.iles.leo.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnitType {
    public static final UnitType CI_TOP = new UnitType(1l, "CI_TOP");
    public static final UnitType NODE_INSTALLATION = new UnitType(2l, "CI_BU");
    public static final UnitType INSTALLATION = new UnitType(7l, "CI_Install");
    public static final UnitType NWFT = new UnitType(67l, "CI_NT");
    public static final UnitType MNBL = new UnitType(71l, "MNBL");
    //TODO: request to LEO to add new unit type for provisioning
    public static final UnitType PROVISIONING = new UnitType(65l, "CI_BUILD");
    public Long id;
    public String unitTypeLevel;
    public String unitTypeName;
    public String unitTypeReportName;
    public String unitTypeDescription;

    public UnitType(Long anId, String uTN) {
        id = anId;
        unitTypeName = uTN;
    }

    public static List<String> generateStaticList() {
        List<String> list = new ArrayList<>();

        list.add("CI_TOP");
        list.add("NODE_INSTALLATION");
        list.add("INSTALLATION");
        list.add("NWFT");
        list.add("MNBL");
        list.add("PROVISIONING");

        return list;
    }

    public static Map<String, String> generateMapListTypeName() {
        Map<String, String> mapList = new HashMap<>();

        mapList.put("CI_TOP", "CI_TOP");
        mapList.put("NODE_INSTALLATION", "CI_BU");
        mapList.put("INSTALLATION", "CI_Install");
        mapList.put("NWFT", "CI_NT");
        mapList.put("MNBL", "MNBL");
        mapList.put("PROVISIONING", "CI_BUILD");

        return mapList;
    }

    public static Map<String, Long> generateMapListId() throws Exception {
        Map<String, Long> mapList = new HashMap<>();
        mapList.put("CI_TOP", 1l);
        mapList.put("NODE_INSTALLATION", 2l);
        mapList.put("INSTALLATION", 7l);
        mapList.put("NWFT", 67l);
        mapList.put("MNBL", 71l);
        mapList.put("PROVISIONING", 65l);

        return mapList;
    }
}
