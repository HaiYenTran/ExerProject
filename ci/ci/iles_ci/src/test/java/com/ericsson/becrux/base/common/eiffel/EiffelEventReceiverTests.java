package com.ericsson.becrux.base.common.eiffel;

import com.ericsson.becrux.base.common.eiffel.configuration.SecondaryBinding;
import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.base.common.eiffel.events.impl.BaseEventImpl;
import com.ericsson.becrux.base.common.eiffel.events.impl.TestInheritEventFactory;
import com.ericsson.becrux.base.common.eiffel.exceptions.EiffelException;
import com.ericsson.duraci.configuration.EiffelConfiguration;
import com.ericsson.duraci.configuration.EiffelJenkinsConfiguration;
import com.ericsson.duraci.configuration.EiffelJenkinsGlobalConfiguration;
import com.ericsson.duraci.datawrappers.MessageBus;
import com.ericsson.duraci.datawrappers.MessageSendQueue;
import com.ericsson.duraci.eiffelmessage.binding.MBConnFactoryProvider;
import com.ericsson.duraci.eiffelmessage.messages.EiffelMessage;
import com.ericsson.duraci.eiffelmessage.mmparser.clitool.EiffelConfig;
import com.rabbitmq.client.*;
import com.rabbitmq.client.impl.AMQImpl;
import jenkins.model.Jenkins;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.ExecutorService;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Jenkins.class,
        EiffelJenkinsConfiguration.class,
        EiffelJenkinsGlobalConfiguration.class,
        MBConnFactoryProvider.class,
        ConnectionFactory.class})

public class EiffelEventReceiverTests {
    private static final String host = "localhost";
    private static final String port = "8443";
    private static final String messageBusHost = host + ":" + port;
    private static String bindingKey = "experimental.generic.iles.eiffel021.seki.fem002";
    private static String domainId = "eiffel021.seki.fem002";
    private static String exchangeName = "mb001-eiffel021-test";
    private static String consumerName = "secondaryBindings";
    private static String routingKey = "test";
    private EiffelEventReceiver receiver;

    @Mock private ServerSocket serverSocket;
    @Mock private Socket clientSocket;
    @Mock private Jenkins jenkins;
    @Mock private EiffelJenkinsGlobalConfiguration.EiffelJenkinsGlobalConfigurationDescriptor descriptor;
    @Mock private ConnectionFactory connectionFactory;
    @Mock private Connection connection;
    @Mock private Channel channel;

    @Before
    public void setUp() throws Exception {
        receiver = createReceiver(getSecondaryBindings(), getConfiguration());

        mockPorts();
        mockJenkins();
        mockConnectionFactory();
        mockConnection();
        mockChannel();
    }

    private void mockPorts() throws IOException {
        when(serverSocket.accept()).thenReturn(clientSocket);
        PowerMockito.doAnswer(invocation -> null).when(clientSocket).connect(any(SocketAddress.class));
    }

    private void mockJenkins() {
        PowerMockito.mockStatic(Jenkins.class);
        PowerMockito.when(Jenkins.getInstance()).thenReturn(jenkins);
        PowerMockito.when(Jenkins.getInstance().getDescriptorOrDie(EiffelJenkinsGlobalConfiguration.class)).thenReturn(descriptor);
        PowerMockito.when(descriptor.getMessageBus()).thenReturn(new MessageBus(host, port));
        PowerMockito.when(descriptor.getMessageSendQueue()).thenReturn(new MessageSendQueue(10));
        PowerMockito.when(descriptor.getQueueLength()).thenReturn("10");
        PowerMockito.when(descriptor.getDomainId()).thenReturn(domainId);
        PowerMockito.when(descriptor.getMessageBusExchangeName()).thenReturn(exchangeName);
    }

    private void mockConnectionFactory() throws Exception {
        connectionFactory.setHost(host);
        connectionFactory.setPort(Integer.parseInt(port));
        when(connectionFactory.getPort()).thenReturn(Integer.parseInt(port));
        when(connectionFactory.getHost()).thenReturn(host);
        PowerMockito.mockStatic(ConnectionFactory.class);
        PowerMockito.whenNew(ConnectionFactory.class).withNoArguments().thenReturn(connectionFactory);
        PowerMockito.doReturn(connection).when(connectionFactory).newConnection();
        PowerMockito.doReturn(connection).when(connectionFactory).newConnection(any(Address[].class));
        PowerMockito.doReturn(connection).when(connectionFactory).newConnection(any(ExecutorService.class));
        PowerMockito.doReturn(connection).when(connectionFactory).newConnection(any(ExecutorService.class), any(Address[].class));

        PowerMockito.mockStatic(MBConnFactoryProvider.class);
        PowerMockito.when(MBConnFactoryProvider.setupFactory(any(ConnectionFactory.class), any(MessageBus.class))).thenReturn(connectionFactory);
        PowerMockito.when(MBConnFactoryProvider.createFactory(any(MessageBus.class))).thenReturn(connectionFactory);
    }

    private void mockConnection() throws IOException {
        when(connection.isOpen()).thenReturn(true);
        PowerMockito.doReturn(channel).when(connection).createChannel();
        PowerMockito.doReturn(channel).when(connection).createChannel(anyInt());
    }

    private void mockChannel() throws IOException {
        when(channel.isOpen()).thenReturn(true);
        when(channel.exchangeDeclarePassive(anyString())).thenReturn(new AMQImpl.Exchange.DeclareOk());
        when(channel.exchangeDeclare(anyString(),anyString())).thenReturn(new AMQImpl.Exchange.DeclareOk());
        when(channel.exchangeDeclare(anyString(),anyString(), anyBoolean())).thenReturn(new AMQImpl.Exchange.DeclareOk());
        when(channel.basicConsume(anyString(), anyBoolean(), any(Consumer.class))).thenReturn(receiver.getConsumer().getName());
    }

    /**************************************************************************
     * Constructor tests
     *************************************************************************/
    @Test
    public void testDefaultConstructor() throws Exception {
        EiffelEventReceiver receiver = new EiffelEventReceiver();
        assertTrue(receiver != null);
    }

    @Test
    public void testConstructorWithArgumentNegative() throws Exception {
        EiffelEventReceiver receiver = null;
        try {
            receiver = new EiffelEventReceiver("", null, false, "", null);
            fail("Empty tag in constructor should not be successful");
        }
        catch (EiffelException e) {
            assertTrue(e.getHtmlMessage().contains("Illegal tag"));
            assertTrue(receiver == null);
        }

        try {
            receiver = new EiffelEventReceiver("test", null, false, "", null);
            fail("Empty consumerName in constructor should not be successful");
        }
        catch (EiffelException e) {
            assertTrue(e.getHtmlMessage().contains("Illegal consumer name"));
            assertTrue(receiver == null);
        }
    }

    @Test
    public void testConstructorWithArgumentPositive() throws Exception {
        EiffelEventReceiver receiver;
        try {
            receiver = new EiffelEventReceiver("test", null, false, "test", null);
            assertTrue(receiver != null);
        }
        catch (EiffelException e) {
            fail(e.getHtmlMessage());
        }
    }

    @Test
    public void testConstructorConfigurationGiven() throws Exception {
        try {
            EiffelEventReceiver receiver = new EiffelEventReceiver("test", new EiffelConfig("domain", "some.exchange", "testhost:1337"), false, "test", null);
            assertTrue(receiver != null);
        }
        catch (EiffelException e) {
            fail(e.getHtmlMessage());
        }
    }

    @Test
    public void testConstructorSecondaryBindingsGiven() throws Exception {
        try {
            List<SecondaryBinding> secondaryBindings = new LinkedList<>();
            secondaryBindings.add(new SecondaryBinding("somekey", "MTAS"));
            EiffelEventReceiver receiver = new EiffelEventReceiver("test", null, false, "test", secondaryBindings);
            assertTrue(receiver != null);
        }
        catch (EiffelException e) {
            fail(e.getHtmlMessage());
        }
    }

    @Test
    public void testConstructorAllGiven() throws Exception {
        try {
            List<SecondaryBinding> secondaryBindings = new LinkedList<>();
            secondaryBindings.add(new SecondaryBinding("somekey", "MTAS"));
            EiffelEventReceiver receiver = new EiffelEventReceiver("test", new EiffelConfig("domain", "some.exchange", "testhost:1337"), false, "test", secondaryBindings);
            assertTrue(receiver != null);
        }
        catch (EiffelException e) {
            fail(e.getHtmlMessage());
        }
    }

    /**************************************************************************
     * Receiving.
     * TODO Bind queues together, find way to mock rabbitmq broker to share
     * TODO  the queues. Calling consumeMessage is a temporary solution
     *************************************************************************/
    private EiffelEventReceiver createReceiver(List<SecondaryBinding> secondaryBindings, EiffelConfiguration configuration) {
        try {
            return new EiffelEventReceiver(routingKey, configuration, false, consumerName, secondaryBindings);
        }
        catch (EiffelException e) {
            fail("Failed to create EiffelEventReceiver object with reason: " + e.getHtmlMessage());
            return null;
        }
    }

    private EiffelConfiguration getConfiguration() {
        return new EiffelConfig(domainId, exchangeName, messageBusHost);
    }

    private List<SecondaryBinding> getSecondaryBindings() {
        List<SecondaryBinding> secondaryBindings = new LinkedList<>();
        secondaryBindings.add(new SecondaryBinding(bindingKey, "MTAS"));
        return secondaryBindings;
    }

    @Test
    public void testReceivingDirect() throws Exception {
        TestInheritEventFactory eventFactory = new TestInheritEventFactory();
        EiffelEventConverter converter = new EiffelEventConverter(eventFactory);
        Event event = eventFactory.createEvent(BaseEventImpl.class.getSimpleName());
        EiffelMessage eiffelMessage = converter.convertToEiffelMessage(event, receiver.getConfiguration(), routingKey);

        receiver.start();
        receiver.getConsumer().consumeMessage(eiffelMessage);
        receiver.waitForEvent();
        Queue<Event> events = receiver.getEventQueue();
        assertEquals(event, events.element());
        assertNotNull(events.element().getSourceEiffelEvent());
        receiver.stop();
    }

    @Test
    public void testReceivingNoEventToConsume() throws EiffelException {
        try {
            receiver.start();
            receiver.waitForEvent();
        } catch (EiffelException e) {
            assertTrue(e.getHtmlMessage().equals("Timeout"));
            assertTrue(receiver.getEventQueue().isEmpty());
            receiver.stop();
        }
    }

    /*
     * TODO When queues are bound, update the queue during timeout
     */
    @Test
    public void testReceivingEventWithTimeout() throws Exception {
        receiver.start();
        TestInheritEventFactory eventFactory = new TestInheritEventFactory();
        EiffelEventConverter converter = new EiffelEventConverter(eventFactory);
        Event event = eventFactory.createEvent(BaseEventImpl.class.getSimpleName());
        EiffelMessage eiffelMessage = converter.convertToEiffelMessage(event, receiver.getConfiguration(), routingKey);
        receiver.getConsumer().consumeMessage(eiffelMessage);
        receiver.waitForEvent(20000);

        Queue<Event> events = receiver.getEventQueue();
        assertEquals(event, events.element());
        assertNotNull(events.element().getSourceEiffelEvent());
        receiver.stop();
    }

    @Test
    public void testReceivingNoEventToConsumeAfterTimeout() throws EiffelException {
        try {
            receiver.start();
            receiver.waitForEvent(20000);
        } catch (EiffelException e) {
            assertTrue(e.getHtmlMessage().equals("Timeout"));
            assertTrue(receiver.getEventQueue().isEmpty());
            receiver.stop();
        }
    }

    @Test
    public void testReceivingCloseBeforeStop() throws Exception {
        receiver.start();
        receiver.close();
        try {
            receiver.stop();
            fail("Close before stop is not allowed");
        }
        catch (IllegalStateException e) {}
    }

    /**************************************************************************
     * Test Group: Accessors
     *************************************************************************/
    @Test
    public void testSettersGetters() {
        assertNotNull(receiver.getConfiguration());
        assertNotNull(receiver.getBindings());
        assertNotNull(receiver.getBindingConfiguration());
        assertNotNull(receiver.getConsumer());
        assertNotNull(receiver.getConverter());
        assertTrue(receiver.getEiffelMessageQueue().isEmpty());
        assertTrue(receiver.getEventQueue().isEmpty());
        assertTrue(receiver.getEiffelEventQueue().isEmpty());
        assertFalse(receiver.getFullBindingKey().isEmpty());
    }

    /*
     * TODO Use this one later after the queues are bound
     */
    /*private QueueingConsumer.Delivery getDelivery() throws Exception {
        AMQP.BasicProperties properties = MessageProperties.PERSISTENT_BASIC.builder().contentType("application/json").contentEncoding("UTF-8").build();
        Envelope envelope = new Envelope(1234L, false, exchangeName, routingKey);
        QueueingConsumer.Delivery delivery = new QueueingConsumer.Delivery(envelope, properties, "HelloWorld!".getBytes());
        return delivery;
    }*/
}
