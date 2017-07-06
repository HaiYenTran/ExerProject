package com.ericsson.becrux.base.common.eiffel;

import com.ericsson.becrux.base.common.eiffel.configuration.SecondaryBinding;
import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.base.common.eiffel.events.impl.BaseEventFactory;
import com.ericsson.becrux.base.common.eiffel.exceptions.EiffelException;
import com.ericsson.duraci.configuration.EiffelConfiguration;
import com.ericsson.duraci.configuration.EiffelJenkinsGlobalConfiguration;
import com.ericsson.duraci.eiffelmessage.binding.MessageBusBindings;
import com.ericsson.duraci.eiffelmessage.binding.MessageConsumer;
import com.ericsson.duraci.eiffelmessage.binding.configuration.BindingConfiguration;
import com.ericsson.duraci.eiffelmessage.binding.exceptions.BindingDisposalException;
import com.ericsson.duraci.eiffelmessage.binding.exceptions.EiffelMessageBindingException;
import com.ericsson.duraci.eiffelmessage.binding.exceptions.EiffelMessageConsumptionException;
import com.ericsson.duraci.eiffelmessage.binding.exceptions.RecoverableEiffelMessageConsumptionException;
import com.ericsson.duraci.eiffelmessage.messages.EiffelEvent;
import com.ericsson.duraci.eiffelmessage.messages.EiffelMessage;
import com.ericsson.duraci.eiffelmessage.messages.events.EiffelGenericEvent;
import com.ericsson.duraci.eiffelmessage.sending.MessageSender;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

public class EiffelEventReceiver implements IEventReceiver {
    private static final String buildRoutingKeyMethodName = "buildRoutingKey";
    private static final String defaultConsumerName = "nwft-consumer";
    private static final String objectDisposedError = "Object disposed";
    private static final long defaultTimeout = 5000;
    private final Object lock = new Object();
    private String fullBindingKey;
    private EiffelConfiguration configuration;
    private MessageBusBindings bindings;
    private BindingConfiguration bindingConfiguration;
    private MessageConsumer consumer;
    private boolean durable;
    private List<SecondaryBinding> secondaryBindings;
    private boolean disposed;
    private Queue<EiffelMessage> queue = new LinkedList<>();

    private EiffelEventConverter converter;

    public EiffelEventReceiver() throws EiffelException {
        this(null, null, true, null, null);
    }

    public EiffelEventReceiver(String tag, EiffelConfiguration configuration, boolean durable, String consumerName, List<SecondaryBinding> secondaryBindings) throws EiffelException {
        this(tag, configuration, durable, consumerName, secondaryBindings, new EiffelEventConverter(new BaseEventFactory()));
    }

    public EiffelEventReceiver(String tag, EiffelConfiguration configuration, boolean durable, String consumerName, List<SecondaryBinding> secondaryBindings, EiffelEventConverter converter) throws EiffelException {
        this.converter = converter;
        init(tag, configuration, durable, consumerName, secondaryBindings);
    }

    private void init(String tag, EiffelConfiguration configuration, boolean durable, String consumerName, List<SecondaryBinding> secondaryBindings) throws EiffelException {
        if (tag != null && tag.length() <= 0)
            throw new EiffelException("Illegal tag");
        if (consumerName != null && consumerName.length() <= 0)
            throw new EiffelException("Illegal consumer name");

		/*
         * Download configuration from Jenkins Global Configuration if configuration param is null
		 */
        this.configuration = configuration;
        if (this.configuration == null)
            this.configuration = new EiffelJenkinsGlobalConfiguration.Provider().provide();

		/*
		 * Create full binding key for us, this key triggers ourself
		 * This key contains type of Event, we use : experimental.generic
		 * If tag param is null, full binding key contains (*)
		 */
        this.fullBindingKey = buildBindingKey(tag);
        if (tag == null && fullBindingKey.contains(EiffelMessage.DEFAULT_ROUTING_KEY_TAG_WORD))
            fullBindingKey = fullBindingKey.replace(EiffelMessage.DEFAULT_ROUTING_KEY_TAG_WORD, "*");
        this.durable = durable;

		/*
		 * Adding secondary bindings to listening events from other domain
		 */
        this.secondaryBindings = secondaryBindings;

        if (consumerName == null)
            consumerName = defaultConsumerName;
        if (!durable)
            consumerName += "-" + UUID.randomUUID().toString();
        final String resultConsumerName = consumerName;
        this.consumer = new MessageConsumer() {
            @Override
            public void consumeMessage(EiffelMessage message) throws EiffelMessageConsumptionException, RecoverableEiffelMessageConsumptionException {
                if (message != null) {
                    synchronized (lock) {
                        queue.add(message);
                    }
                }
            }

            @Override
            public String getName() {
                return resultConsumerName;
            }
        };
        bindingConfiguration = new BindingConfiguration(false, durable, consumer, getAllBindingKeys(), null, null);
        bindings = new MessageBusBindings.Factory().create(this.configuration.getMessageBus(), this.configuration.getDomainId());

        disposed = false;
    }

    private String buildBindingKey(String tag) throws EiffelException //Tag can be null
    {
        EiffelEvent event = EiffelGenericEvent.Factory.create("NoName");
        EiffelMessage message = converter.convertToEiffelMessage(event, configuration, tag);

        MessageSender sender = null;
        try {
            sender = new MessageSender.Factory(configuration).create();

            try {
                Method m = sender.getClass().getDeclaredMethod(buildRoutingKeyMethodName, EiffelMessage.class);
                m.setAccessible(true);
                return (String) m.invoke(sender, message);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                throw new EiffelException(ex);
            }
        } finally {
            try {
                if (sender != null)
                    sender.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public String getFullBindingKey() {
        return fullBindingKey;
    }

    public void start() throws EiffelException {
        if (disposed)
            throw new IllegalStateException(objectDisposedError);

        if (!MessageBusBindings.checkConnection(configuration.getMessageBus())) {
            throw new EiffelException("Connection not possible to specified exchange");
        }
        try {
            if (!bindings.getAllBindingConfigurations().contains(bindingConfiguration)) {
                bindings.dispose();
                bindings.add(bindingConfiguration);
                bindings.unPause(bindingConfiguration);
            }
        } catch (EiffelMessageBindingException ex) {
            throw new EiffelException(ex);
        }
    }

    private List<String> getAllBindingKeys() {
        List<String> bindingKeys = new LinkedList<>();

        bindingKeys.add(fullBindingKey);
        if (secondaryBindings != null) {
            for (SecondaryBinding binding : secondaryBindings)
                bindingKeys.add(binding.getBindingKey());
        }

        return bindingKeys;
    }

    public void stop() throws EiffelException {
        if (disposed)
            throw new IllegalStateException(objectDisposedError);

        try {
            if (bindings != null && bindings.getAllBindingConfigurations().contains(bindingConfiguration))
                bindings.remove(bindingConfiguration, !durable);
        } catch (EiffelMessageBindingException ex) {
            throw new EiffelException(ex);
        }
    }

    @Override
    public void close() throws Exception {
        if (!disposed) {
            try {
                stop();
            } catch (EiffelException ex) {
                throw ex;
            }
            try {
                if (bindings != null)
                    bindings.dispose();
            } catch (BindingDisposalException ex) {
                throw ex;
            }
            disposed = true;
        }
    }

    @Override
    public boolean isStarted() {
        if (!disposed && bindings != null)
            return bindings.getAllBindingConfigurations().contains(bindingConfiguration);
        return false;
    }

    public Queue<EiffelMessage> getEiffelMessageQueue() {
        if (disposed)
            throw new IllegalStateException(objectDisposedError);

        Queue<EiffelMessage> result = new LinkedList<>();
        synchronized (lock) {
            queue.stream().forEach(result::add);
            queue.clear();
        }
        return result;
    }

    public Queue<EiffelEvent> getEiffelEventQueue() {
        if (disposed)
            throw new IllegalStateException(objectDisposedError);

        Queue<EiffelEvent> result = new LinkedList<>();
        synchronized (lock) {
            queue.stream().map(m -> m.getEvent()).forEach(result::add);
            queue.clear();
        }
        return result;
    }

    public Queue<Event> getEventQueue() {
        if (disposed)
            throw new IllegalStateException(objectDisposedError);

        Queue<Event> result = new LinkedList<>();
        synchronized (lock) {
            for (EiffelMessage message : queue) {
                try {
                    result.add(converter.convertToEvent(message));
                } catch (NullPointerException ex) {
                    continue;
                }
            }
            queue.clear();
        }
        return result;
    }

    public int size() {
        if (disposed)
            throw new IllegalStateException(objectDisposedError);

        synchronized (lock) {
            return queue.size();
        }
    }

    public void waitForEvent() throws EiffelException {
        waitForEvent(defaultTimeout);
    }

    public void waitForEvent(long timeout) throws EiffelException {
        waitForEvent(1, timeout);
    }

    public void waitForEvent(int count, long timeout) throws EiffelException {
        if (disposed)
            throw new IllegalStateException(objectDisposedError);

        if (timeout <= 0)
            throw new EiffelException("Invalid timeout value");

        long startTime = System.currentTimeMillis();
        try {
            while (true) {
                if ((System.currentTimeMillis() - startTime) > timeout)
                    throw new EiffelException("Timeout");
                if (size() >= count)
                    return;

                Thread.sleep(timeout / 10);
            }
        } catch (InterruptedException ex) {
            throw new EiffelException(ex);
        }
    }

    public EiffelConfiguration getConfiguration() {
        return configuration;
    }

    public MessageBusBindings getBindings() {
        return bindings;
    }

    public BindingConfiguration getBindingConfiguration() {
        return bindingConfiguration;
    }

    public MessageConsumer getConsumer() {
        return consumer;
    }

    public EiffelEventConverter getConverter() {
        return converter;
    }

    public void setConverter(EiffelEventConverter converter) {
        this.converter = converter;
    }
}