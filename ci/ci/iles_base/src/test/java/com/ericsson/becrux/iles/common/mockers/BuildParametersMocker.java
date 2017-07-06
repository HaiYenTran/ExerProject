package com.ericsson.becrux.iles.common.mockers;

import com.ericsson.becrux.base.common.core.CommonParamenterValue;
import com.ericsson.becrux.base.common.core.NwftParameterValue;
import com.ericsson.becrux.base.common.data.Component;
import com.ericsson.becrux.base.common.data.Version;
import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.base.common.eiffel.parameters.EiffelEventParameterValue;
import com.ericsson.becrux.base.common.loop.ComponentParameterValue;
import com.ericsson.becrux.base.common.loop.PhaseStatus;
import com.ericsson.becrux.base.common.loop.ResultParameterValue;
import com.ericsson.becrux.base.common.testexec.TestStatus;
import com.ericsson.becrux.base.common.utils.BuildParametersExtractor;
import com.ericsson.becrux.base.common.vise.ViseChannel;
import com.ericsson.becrux.base.common.vise.parameters.ReservedViseChannelParameterValue;
import com.ericsson.becrux.iles.data.IlesComponentFactory;
import com.ericsson.becrux.iles.data.Int;
import com.ericsson.becrux.iles.eiffel.events.EventParamenterValue;
import com.ericsson.becrux.iles.leo.parameters.InitLeoParameterValue;
import hudson.model.AbstractBuild;
import org.powermock.api.mockito.PowerMockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

/******************************************************************************
 * Mocking of build parameters {@link BuildParametersExtractor}.
 *
 * All add-methods in this class returns the instance of this mocker class,
 * e.g. it is designed for consecutive calls.
 * Once all parameters has been added, the expression has to be finished
 * with a call to finishMock().
 * This makes it easy to expand for future parameters.
 *
 * Example:
 * BuildParametersMocker.newMock().addComponentParameterValue("key", 1).
 *                                 addInitLeoParameterValue(4).
 *                                 addCommonParamenterValue("key", "value", 2).
 *                                 finishMock();
 *****************************************************************************/
public class BuildParametersMocker {
    private BuildParametersExtractor extractor;
    private List<NwftParameterValue> values;

    private BuildParametersMocker() throws Exception {
        values = new ArrayList<>();
        extractor = mock(BuildParametersExtractor.class);
        PowerMockito.whenNew(BuildParametersExtractor.class).withArguments(any(AbstractBuild.class)).thenReturn(extractor);
        PowerMockito.doAnswer(invocation -> null).when(extractor).addNwftParameter(any());
        PowerMockito.doAnswer(invocation -> null).when(extractor).addNwftParameters();
    }

    /**************************************************************************
     * Create an object that wraps build parameter mocking functionality.
     *
     * @return build parameter mock object wrapper
     *************************************************************************/
    public static BuildParametersMocker newMock() throws Exception {
        return new BuildParametersMocker();
    }

    /**************************************************************************
     * Get this mock.
     *
     * @return build parameter mock object wrapper
     *************************************************************************/
    public BuildParametersMocker getMock() {
        return this;
    }

    /**************************************************************************
     * Add a build parameter of type {@link ResultParameterValue} to the
     * mock.
     *
     * @return this mock
     *************************************************************************/
    public BuildParametersMocker addResultParameterValue(PhaseStatus phaseStatus, int noOfComponentsToMock) {
        for (int i = 0; i < noOfComponentsToMock; ++i) {
            Map<String, String> details = new HashMap<>();
            details.put("key", "value");
            Map<TestStatus, Integer> testStatus = new HashMap<>();
            testStatus.put(TestStatus.PASSED, 0);
            ResultParameterValue comp = new ResultParameterValue("test", phaseStatus, details, testStatus);
            values.add(comp);
        }
        return this;
    }

    /**************************************************************************
     * Add a build parameter of type {@link EiffelEventParameterValue} to the
     * mock.
     *
     * @return this mock
     *************************************************************************/
    public BuildParametersMocker addEiffelEventParameterValue(Event event, int noOfComponentsToMock) {
        for (int i = 0; i < noOfComponentsToMock; ++i) {
            EiffelEventParameterValue comp = new EiffelEventParameterValue("event", event, "test");
            values.add(comp);
        }

        return this;
    }

    /**************************************************************************
     * Add a build parameter of type {@link ComponentParameterValue} to the
     * mock.
     *
     * @return this mock
     *************************************************************************/
    public BuildParametersMocker addComponentParameterValue(String name, int noOfComponentsToMock) throws Exception {
        for (int i = 0; i < noOfComponentsToMock; ++i) {
            Int intComp = (Int) IlesComponentFactory.getInstance().create(Int.class.getSimpleName(), Version.createReleaseVersion("R1A01"));
            intComp.setArtifact("https://test");
            ComponentParameterValue comp = new ComponentParameterValue(name, intComp, "test");
            values.add(comp);
        }

        return this;
    }

    public BuildParametersMocker addComponentParameterValue(String name, Component component, int noOfComponentsToMock) throws Exception {
        for (int i = 0; i < noOfComponentsToMock; ++i) {
            ComponentParameterValue comp = new ComponentParameterValue(name, component, "test");
            values.add(comp);
        }

        return this;
    }

    /**************************************************************************
     * Add a build parameter of type {@link EventParamenterValue} to the
     * mock.
     *
     * @return this mock
     *************************************************************************/
    public BuildParametersMocker addEventParamenterValue (Event event, int noOfComponentsToMock) throws Exception {
        for (int i = 0; i < noOfComponentsToMock; ++i) {
            EventParamenterValue comp = new EventParamenterValue(event);
            values.add(comp);
        }
        return this;
    }

    /**************************************************************************
     * Add a build parameter of type {@link ReservedViseChannelParameterValue}
     * to the mock.
     *
     * @return this mock
     *************************************************************************/
    public BuildParametersMocker addReservedViseChannelParameterValue(String name, int noOfComponentsToMock) throws Exception {
        for (int i = 0; i < noOfComponentsToMock; ++i) {
            ReservedViseChannelParameterValue comp = new ReservedViseChannelParameterValue(name, new ViseChannel("VISE030"+i), "test");
            values.add(comp);
        }
        return this;
    }

    /**************************************************************************
     * Add a build parameter of type {@link InitLeoParameterValue}
     * to the mock.
     *
     * @return this mock
     *************************************************************************/
    public BuildParametersMocker addInitLeoParameterValue(int noOfComponentsToMock) throws Exception {
        for (int i = 0; i < noOfComponentsToMock; ++i) {
            InitLeoParameterValue comp = new InitLeoParameterValue();
            values.add(comp);
        }
        return this;
    }

    /**************************************************************************
     * Add a build parameter of type {@link CommonParamenterValue}
     * to the mock.
     *
     * @return this mock
     *************************************************************************/
    public BuildParametersMocker addCommonParamenterValue(String key, String value, int numberOfActionsToMock) throws Exception {
        for (int i = 0; i < numberOfActionsToMock; ++i) {
            CommonParamenterValue comp = new CommonParamenterValue(key, value);
            values.add(comp);
        }
        return this;
    }

    /**************************************************************************
     * Stop mocking of parameters. A call to getAllNwftParametersOfType(type)
     * will return all build parameters equal to type.
     *************************************************************************/
    public void finishMock() {
        PowerMockito.doAnswer(invocation -> {
            final Class<?> argClass = (Class<?>)invocation.getArguments()[0];
            List<NwftParameterValue> retParams = new ArrayList<>();
            for (NwftParameterValue value : values) {
                if (value.getClass().equals(argClass)) {
                    retParams.add(value);
                }
            }
            return retParams;
        }).when(extractor).getAllNwftParametersOfType(any());
    }
}
