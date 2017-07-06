package com.ericsson.becrux.base.common.mockers;

import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.Result;
import hudson.model.Run;
import hudson.tasks.junit.TestResult;
import hudson.tasks.junit.TestResultAction;
import javafx.util.Pair;
import org.powermock.api.mockito.PowerMockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.CALLS_REAL_METHODS;

/**
 * Created by zguntom on 4/11/17.
 */
public class AbstractBuildMocker {
    private AbstractBuild abstractBuild;
    private RunMocker runMocker;
    private List<Action> actions;
    private FilePath workspace;
    private Map<String, String> envVars = new HashMap<>();

    private AbstractBuildMocker(Pair<String, String>... envVars) throws Exception {
        for (Pair<String, String> envVar : envVars) {
            this.envVars.put(envVar.getKey(), envVar.getValue());
        }

        this.actions = new ArrayList<>();
        this.runMocker = RunMocker.createMock();

        this.abstractBuild = PowerMockito.mock(AbstractBuild.class, invocation -> {
            if (Run.class.equals(invocation.getMethod().getReturnType())) {
                return this.runMocker;
            }
            else if (invocation.getMethod().getName().equals("toString")) {
                return "AbstractBuild";
            }
            else if (invocation.getMethod().getName().equals("addAction")) {
                Action action = (Action)invocation.getArguments()[0];
                actions.add(action);
            }
            else if (invocation.getMethod().getName().equals("getAction")) {
                if(invocation.getArguments()[0] == TestResultAction.class) {
                    TestResultAction action = PowerMockito.mock(TestResultAction.class);
                    TestResult result = PowerMockito.mock(TestResult.class);
                    PowerMockito.when(action.getResult()).thenReturn(result);
                    return action;
                } else {
                    return null;
                }
            }
            else if (invocation.getMethod().getName().equals("setResult")) {
                return null;
            }
            else if (invocation.getMethod().getName().equals("getUrl")) {
                return "https://localhost:8888";
            }
            else if (invocation.getMethod().getName().equals("getEnvVars")) {
                return this.envVars;
            }
            else {
                return CALLS_REAL_METHODS.answer(invocation);
            }

            return null;
        });

        this.workspace = PowerMockito.mock(FilePath.class);
        PowerMockito.mockStatic(FilePath.class);
        PowerMockito.whenNew(FilePath.class).withAnyArguments().thenReturn(this.workspace);
        PowerMockito.when(this.workspace.getRemote()).thenReturn("remote-cmd");
        PowerMockito.when(this.workspace.copyRecursiveTo(any())).thenReturn(1);
        PowerMockito.when(this.abstractBuild.getWorkspace()).thenReturn(workspace);
        PowerMockito.when(this.abstractBuild.getResult()).thenReturn(Result.SUCCESS);
    }

    public static AbstractBuildMocker createMock(Pair<String, String>... envVars) throws Exception {
        return new AbstractBuildMocker(envVars);
    }

    public AbstractBuild getMock() {
        return this.abstractBuild;
    }
}
