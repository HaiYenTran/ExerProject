package com.ericsson.becrux.base.common.mockers;

import hudson.model.Run;
import org.powermock.api.mockito.PowerMockito;

import static org.mockito.Mockito.CALLS_REAL_METHODS;

/**
 * Created by zguntom on 4/11/17.
 */
public class RunMocker {
    private Run run;

    private RunMocker() {
        this.run = PowerMockito.mock(Run.class, invocation -> {
            if (Run.class.equals(invocation.getMethod().getReturnType())) {
                return run;
            }
            else if (invocation.getMethod().getName().equals("getFullName")) {
                return "RunMock";
            }
            else if (invocation.getMethod().getName().equals("toString")) {
                return "RunMock";
            }
            else {
                return CALLS_REAL_METHODS.answer(invocation);
            }
        });
    }

    public static RunMocker createMock() {
        return new RunMocker();
    }

    public Run getMock() {
        return run;
    }
}
