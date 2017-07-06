package com.ericsson.becrux.base.common.eiffel;

import com.ericsson.becrux.base.common.data.Component;
import com.ericsson.becrux.base.common.eiffel.events.impl.BaseEventFactory;
import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.duraci.configuration.EiffelConfiguration;
import com.ericsson.duraci.configuration.EiffelJenkinsGlobalConfiguration;
import com.ericsson.duraci.eiffelmessage.messages.EiffelMessage;
import com.ericsson.duraci.eiffelmessage.sending.MessageSendWrapper;
import com.ericsson.duraci.eiffelmessage.sending.MessageSender;
import com.ericsson.duraci.eiffelmessage.sending.exceptions.EiffelMessageSenderException;
import com.ericsson.duraci.logging.JavaLoggerEiffelLog;

public class EiffelEventSender implements IEventSender {
    private static final String objectDisposedError = "Object disposed";

    private EiffelConfiguration configuration;
    private MessageSender sender;
    private MessageSendWrapper sendWrapper;
    private EiffelEventConverter converter;
    private boolean disposed;

    public EiffelEventSender() {
        this(new EiffelJenkinsGlobalConfiguration.Provider().provide());
    }

    public EiffelEventSender(EiffelConfiguration configuration) {
        this(configuration, new EiffelEventConverter(new BaseEventFactory()));
    }

    /**
     * Constructor.
     * @param configuration the Eiffel Configuration
     * @param converter the Converter for converting Event.
     */
    public EiffelEventSender(EiffelConfiguration configuration, EiffelEventConverter converter) {
        this.converter = converter;
        this.configuration = configuration;
        this.sendWrapper = new MessageSendWrapper(new JavaLoggerEiffelLog(
                MessageSender.class), configuration.getMessageBus(), configuration
                .getMessageSendQueue().getQueueLength());
        this.sender = new MessageSender(configuration, sendWrapper);
        this.disposed = false;
    }

    public void sendEvent(Event event) throws EiffelMessageSenderException {
        sendEvent(event, (String) null);
    }

    @Override
    public void sendEvent(Event event, Component target) throws EiffelMessageSenderException {
        sendEvent(event, target.getType().toLowerCase());
    }

    @Override
    public void sendEvent(Event event, String routingKey) throws EiffelMessageSenderException {
        if (disposed)
            throw new IllegalStateException(objectDisposedError);

        EiffelMessage eiffelMessage = converter.convertToEiffelMessage(event, configuration, routingKey);
        sender.send(eiffelMessage);
    }

    public void close() throws Exception{
        if (!disposed) {
            try {
                sender.dispose();
            } catch (Exception ex) {
                throw ex;
            }
            try {
                sendWrapper.dispose();
            } catch (Exception ex) {
                throw ex;
            }
            disposed = true;
        }
    }

    public EiffelEventConverter getConverter() {
        return converter;
    }

    public void setConverter(EiffelEventConverter converter) {
        this.converter = converter;
    }
}