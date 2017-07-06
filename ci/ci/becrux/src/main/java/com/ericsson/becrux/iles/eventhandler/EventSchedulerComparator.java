package com.ericsson.becrux.iles.eventhandler;

import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.base.common.eiffel.events.impl.BTFEvent;
import com.ericsson.becrux.base.common.eiffel.events.impl.ITREvent;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <ul>
 * <li>newer builds of the same node have priority</li>
 * <li>promotions have priority over loops</li>
 * </ul>
 *
 * @author emacmyc
 */
public class EventSchedulerComparator implements Comparator<Event>, Serializable {

    private static final long serialVersionUID = 1L;
    private static Map<Class<?>, Integer> typeOrder;

    public EventSchedulerComparator() {
        super();
        if (typeOrder == null)
            initializeTypeOrder();
    }

    private static void initializeTypeOrder() {
        typeOrder = new HashMap<>();
        typeOrder.put(null, null);
        typeOrder.put(Event.class, 1);
        typeOrder.put(ITREvent.class, 2);
        typeOrder.put(BTFEvent.class, 3);

    }

    private int compareVersion(String v1, String v2, boolean ascending) {
        if (v1 == null && v2 == null)
            return 0;

        if (ascending) {
            if (v1 == null && v2 != null)
                return -1;
            else if (v1 != null && v2 == null)
                return 1;
            else
                return v2.compareTo(v1);
        } else {
            if (v1 == null && v2 != null)
                return 1;
            else if (v1 != null && v2 == null)
                return -1;
            else
                return v1.compareTo(v2);
        }
    }

    private int compareVersion(List<String> v1, List<String> v2, boolean ascending) {
        if (v1 == null && v2 == null)
            return 0;

        if (ascending) {
            if (v1 == null && v2 != null)
                return -1;
            else if (v1 != null && v2 == null)
                return 1;
        } else {
            if (v1 == null && v2 != null)
                return 1;
            else if (v1 != null && v2 == null)
                return -1;
        }

        if (v1.size() != v2.size())
            throw new IllegalArgumentException("Lists do not have equal size");
        for (int i = 0; i < v1.size(); ++i) {
            String t1 = v1.get(i);
            String t2 = v2.get(i);

            int result = compareVersion(t1, t2, ascending);
            if (result != 0)
                return result;
        }
        return 0;
    }

    private int compareVersion(Event e1, Event e2) {

        if (e1 instanceof ITREvent && e2 instanceof ITREvent) {
            ITREvent ev1 = (ITREvent) e1;
            ITREvent ev2 = (ITREvent) e2;
            return compareVersion(ev1.getBaseline(), ev2.getBaseline(), true);
        } else if (e1 instanceof BTFEvent && e2 instanceof BTFEvent) {
            BTFEvent ev1 = (BTFEvent) e1;
            BTFEvent ev2 = (BTFEvent) e2;
            return compareVersion(ev1.getBaselines(), ev2.getBaselines(), false);
        }
        return 0;
    }

    @Override
    public int compare(Event e1, Event e2) {

        Integer c1 = null;
        Integer c2 = null;
        if (e1 != null)
            c1 = typeOrder.get(e1.getClass());
        if (e2 != null)
            c2 = typeOrder.get(e2.getClass());

        if (c1 == null && c2 == null)
            return 0;
        else if (c1 == null)
            return 1;
        else if (c2 == null)
            return -1;

        if (!c1.equals(c2))
            return (int) Math.signum(c2 - c1);

        return compareVersion(e1, e2);
    }

}
