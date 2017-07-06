package com.ericsson.becrux.base.common.mockers;

import com.ericsson.becrux.base.common.core.NwftParameterValue;
import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.base.common.utils.BuildParametersExtractor;
import com.ericsson.becrux.base.common.eiffel.parameters.EiffelEventParameterValue_ToSend;
import hudson.model.AbstractBuild;
import org.powermock.api.mockito.PowerMockito;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

/******************************************************************************
 * Mocking of build buildstep {@link BuildParametersExtractor}.
 *
 * All add-methods in this class returns the instance of this mocker class,
 * e.g. it is designed for consecutive calls.
 * Once all buildstep has been added, the expression has to be finished
 * with a call to finishMock().
 * This makes it easy to expand for future buildstep.
 *
 * Example:
 * BuildParametersMocker.newMock().addComponentParameterValue("key", 1).
 *                                 addInitLeoParameterValue(4).
 *                                 addCommonParamenterValue("key", "value", 2).
 *                                 finishMock();
 *****************************************************************************/
public class BuildParametersMocker {
    protected BuildParametersExtractor extractor;
    protected List<NwftParameterValue> values;

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
     * Add a build parameter of type {@link EiffelEventParameterValue_ToSend} to the
     * mock.
     *
     * @return this mock
     *************************************************************************/
    public BuildParametersMocker addEiffelEventParameterValue(Event event, int noOfComponentsToMock) {
        for (int i = 0; i < noOfComponentsToMock; ++i) {
            EiffelEventParameterValue_ToSend comp = new EiffelEventParameterValue_ToSend("event", event, "test");
            values.add(comp);
        }

        return this;
    }

    /**************************************************************************
     * Stop mocking of buildstep. A call to getAllNwftParametersOfType(type)
     * will return all build buildstep equal to type.
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