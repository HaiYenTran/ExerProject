package com.ericsson.becrux.base.common.eiffel;

// TODO: refactor these test
public class EiffelTests
{
//	private static final String domainId = "eiffel021.seki.fem002";
//	private static final String exchangeName = "mb001-eiffel021-test";
//	private static final String messageBusHost = "amqps://mb001-eiffel021.rnd.ki.sw.ericsson.se";
//	private static final String routingKey = "test";
//	private List<SecondaryBinding> secondaryBindings;
//
//	@Before
//	public void initialization() throws IOException, TimeoutException
//	{
//		MessageBus mb = new MessageBus(messageBusHost, exchangeName, null, true);
//		if(!MessageBusBindings.createExchange(mb))
//			throw new IOException("MessageBus creation failed");
//
//		secondaryBindings = new LinkedList<SecondaryBinding>();
//		secondaryBindings.add(new SecondaryBinding("experimental.generic.iles.eiffel021.seki.fem004", "MTAS"));
//	}
//
//	@Test
//	public void eiffelDirectSendReceiveTest() throws EiffelException, EiffelMessageSenderException
//	{
//		EiffelConfiguration configuration = new EiffelConfig(domainId, exchangeName, messageBusHost);
//		try(EiffelEventReceiver receiver = new EiffelEventReceiver(routingKey, configuration, false,secondaryBindings))
//		{
//			try(EiffelEventSender sender = new EiffelEventSender(configuration))
//			{
//				receiver.start();
//				BaseEventImpl event = new BaseEventImpl();
//				sender.sendEvent(event, routingKey);
//				receiver.waitForEvent();
//				Queue<Event> receivedEvents = receiver.getEventQueue();
//				assertEquals(event, receivedEvents.element());
//				assertNotNull(receivedEvents.element().getSourceEiffelEvent());
//			}
//		}
//	}

//	@Test
//	public void eiffelReceiveWithoutRoutingKeyTest() throws EiffelException, EiffelMessageSenderException
//	{
//		EiffelConfiguration configuration = new EiffelConfig(domainId, exchangeName, messageBusHost);
//		try(EiffelEventReceiver receiver = new EiffelEventReceiver(configuration, false, secondaryBindings))
//		{
//			try(EiffelEventSender sender = new EiffelEventSender(configuration))
//			{
//				receiver.start();
//				ITREvent event = new ITREvent();
//				sender.sendEvent(event, routingKey);
//				receiver.waitForEvent();
//				Queue<Event> receivedEvents = receiver.getEventQueue();
//				assertEquals(event, receivedEvents.element());
//				assertNotNull(receivedEvents.element().getSourceEiffelEvent());
//			}
//		}
//	}
//
//	@Test
//	public void eiffelMultiTransientReceiverTest() throws EiffelException, EiffelMessageSenderException
//	{
//		EiffelConfiguration configuration = new EiffelConfig(domainId, exchangeName, messageBusHost);
//		try(EiffelEventReceiver receiver1 = new EiffelEventReceiver(routingKey, configuration, false,secondaryBindings))
//		{
//			try(EiffelEventReceiver receiver2 = new EiffelEventReceiver(routingKey, configuration, false,secondaryBindings))
//			{
//				try(EiffelEventSender sender = new EiffelEventSender(configuration))
//				{
//					receiver1.start();
//					receiver2.start();
//					ITREvent event = new ITREvent();
//					sender.sendEvent(event, routingKey);
//					receiver1.waitForEvent();
//					receiver2.waitForEvent();
//					Queue<Event> receivedEvents1 = receiver1.getEventQueue();
//					Queue<Event> receivedEvents2 = receiver2.getEventQueue();
//					assertEquals(event, receivedEvents1.element());
//					assertNotNull(receivedEvents1.element().getSourceEiffelEvent());
//					assertEquals(event, receivedEvents2.element());
//					assertNotNull(receivedEvents2.element().getSourceEiffelEvent());
//				}
//			}
//
//		}
//	}
//
//	@Test
//	public void eiffelMultiTransientSenderTest() throws EiffelException, EiffelMessageSenderException
//	{
//		EiffelConfiguration configuration = new EiffelConfig(domainId, exchangeName, messageBusHost);
//		try(EiffelEventReceiver receiver = new EiffelEventReceiver(routingKey, configuration, false,secondaryBindings))
//		{
//			try(EiffelEventSender sender = new EiffelEventSender(configuration);
//				EiffelEventSender sender2 = new EiffelEventSender(configuration))
//			{
//				receiver.start();
//				ITREvent event = new ITREvent();
//				sender.sendEvent(event, routingKey);
//				ITREvent event2 = new ITREvent();
//				sender2.sendEvent(event2, routingKey);
//				receiver.waitForEvent(2);
//				Queue<Event> receivedEvents = receiver.getEventQueue();
//				assertEquals(2, receivedEvents.size());
//				assertNotNull(receivedEvents.element().getSourceEiffelEvent());
//			}
//		}
//	}
//
//	@Test
//	public void eiffelMultiStartStopTest() throws EiffelException, EiffelMessageSenderException
//	{
//		EiffelConfiguration configuration = new EiffelConfig(domainId, exchangeName, messageBusHost);
//		try(EiffelEventReceiver receiver = new EiffelEventReceiver(routingKey, configuration, false,secondaryBindings))
//		{
//			try(EiffelEventSender sender = new EiffelEventSender(configuration))
//			{
//				receiver.start();
//				ITREvent ev1 = new ITREvent();
//				sender.sendEvent(ev1, routingKey);
//				receiver.waitForEvent();
//				Queue<Event> receivedEvents = receiver.getEventQueue();
//				assertEquals(1, receivedEvents.size());
//				assertEquals(ev1, receivedEvents.remove());
//				receiver.stop();
//				receiver.start();
//				BTFEvent ev2 = new BTFEvent();
//				sender.sendEvent(ev2, routingKey);
//				receiver.waitForEvent();
//				receivedEvents = receiver.getEventQueue();
//				assertEquals(1, receivedEvents.size());
//			}
//		}
//	}
//
//	@Test
//	public void eiffelIncorrectNumberOfStartStopTest() throws EiffelException, EiffelMessageSenderException
//	{
//		EiffelConfiguration configuration = new EiffelConfig(domainId, exchangeName, messageBusHost);
//		try(EiffelEventReceiver receiver = new EiffelEventReceiver(routingKey, configuration, false,secondaryBindings))
//		{
//			try(EiffelEventSender sender = new EiffelEventSender(configuration))
//			{
//				receiver.start();
//				receiver.start();
//				ITREvent event = new ITREvent();
//				sender.sendEvent(event, routingKey);
//				receiver.start();
//				receiver.waitForEvent();
//				receiver.stop();
//				Queue<Event> receivedEvents = receiver.getEventQueue();
//				receiver.stop();
//				assertEquals(1, receivedEvents.size());
//				assertEquals(event, receivedEvents.remove());
//			}
//		}
//	}
//
//	@Test
//	public void eiffelStopBeforeStartTest() throws EiffelException
//	{
//		EiffelConfiguration configuration = new EiffelConfig(domainId, exchangeName, messageBusHost);
//		try(EiffelEventReceiver receiver = new EiffelEventReceiver(routingKey, configuration, false,secondaryBindings))
//		{
//			receiver.stop();
//			receiver.start();
//		}
//	}
//
//	@Test(expected = IllegalStateException.class)
//	public void eiffelEventSenderDisposedDetectionTest() throws EiffelMessageSenderException
//	{
//		EiffelConfiguration configuration = new EiffelConfig(domainId, exchangeName, messageBusHost);
//		EiffelEventSender sender = null;
//		try
//		{
//			sender = new EiffelEventSender(configuration);
//			sender.close();
//			ITREvent event = new ITREvent();
//			sender.sendEvent(event);
//		}
//		finally
//		{
//			try
//			{
//				sender.close();
//			}
//			catch(Exception ex) {}
//		}
//	}
//
//	@Test(expected = IllegalStateException.class)
//	public void eiffelEventReceiverDisposedDetectionTest() throws EiffelException
//	{
//		EiffelConfiguration configuration = new EiffelConfig(domainId, exchangeName, messageBusHost);
//		EiffelEventReceiver receiver = null;
//		try
//		{
//			receiver = new EiffelEventReceiver(routingKey, configuration, false,secondaryBindings);
//			receiver.start();
//			receiver.close();
//			receiver.start();
//		}
//		finally
//		{
//			try
//			{
//				receiver.close();
//			}
//			catch(Exception ex) {}
//		}
//	}
//
//	@Test
//	public void eiffelEventReceiverIsStartedDetectionTest()
//	{
//		EiffelConfiguration configuration = new EiffelConfig(domainId, exchangeName, messageBusHost);
//		try(EiffelEventReceiver receiver = new EiffelEventReceiver(configuration, false, secondaryBindings))
//		{
//			assertFalse(receiver.isStarted());
//			receiver.start();
//			assertTrue(receiver.isStarted());
//			receiver.stop();
//			assertFalse(receiver.isStarted());
//		}
//		catch(Exception ex)
//		{
//			fail(ex.getMessage());
//		}
//	}
//
//	@Test(expected = EiffelMessageSenderException.class)
//	public void eiffelNonExistantExchangeSenderTest() throws EiffelMessageSenderException
//	{
//		EiffelConfiguration configuration = new EiffelConfig(domainId, UUID.randomUUID().toString(), messageBusHost);
//		try(EiffelEventSender sender = new EiffelEventSender(configuration))
//		{
//			sender.sendEvent(new ITREvent(), routingKey);
//		}
//	}
//
//	@Test(expected = EiffelException.class)
//	public void eiffelNonExistantExchangeReceiverTest() throws EiffelException
//	{
//		EiffelConfiguration configuration = new EiffelConfig(domainId, UUID.randomUUID().toString(), messageBusHost);
//		try(EiffelEventReceiver receiver = new EiffelEventReceiver(configuration, false, secondaryBindings))
//		{
//			receiver.start();
//		}
//	}
//
//	@Test(expected = EiffelMessageSenderException.class)
//	public void eiffelNonExistantHostSenderTest() throws EiffelMessageSenderException
//	{
//		EiffelConfiguration configuration = new EiffelConfig(domainId, exchangeName, UUID.randomUUID().toString());
//		try(EiffelEventSender sender = new EiffelEventSender(configuration))
//		{
//			sender.sendEvent(new ITREvent(), routingKey);
//		}
//	}
//
//	@Test(expected = EiffelException.class)
//	public void eiffelNonExistantHostReceiverTest() throws EiffelException
//	{
//		EiffelConfiguration configuration = new EiffelConfig(domainId, exchangeName, UUID.randomUUID().toString());
//		try(EiffelEventReceiver receiver = new EiffelEventReceiver(configuration, false, secondaryBindings))
//		{
//			receiver.start();
//		}
//	}
//
//	@Test
//	public void eiffelMultiEventFromOtherFEMReciver() throws EiffelException, EiffelMessageSenderException
//	{
//		EiffelConfiguration configuration = new EiffelConfig(domainId, exchangeName, messageBusHost);
//		EiffelConfiguration configuration2 = new EiffelConfig("eiffel021.seki.fem004", exchangeName, messageBusHost);
//		try(EiffelEventReceiver receiver = new EiffelEventReceiver(routingKey, configuration, false,secondaryBindings))
//		{
//			try(EiffelEventSender sender = new EiffelEventSender(configuration))
//			{
//				receiver.start();
//				ITREvent ev1 = new ITREvent();
//				sender.sendEvent(ev1, routingKey);
//				receiver.waitForEvent();
//				Queue<Event> receivedEvents = receiver.getEventQueue();
//				assertEquals(1, receivedEvents.size());
//				assertEquals(ev1, receivedEvents.remove());
//				receiver.stop();
//			}
//
//			try(EiffelEventSender sender2 = new EiffelEventSender(configuration2))
//			{
//				receiver.start();
//				ITREvent ev1 = new ITREvent();
//				sender2.sendEvent(ev1, "iles");
//				receiver.waitForEvent();
//				Queue<Event> receivedEvents = receiver.getEventQueue();
//				assertEquals(1, receivedEvents.size());
//				assertEquals(ev1, receivedEvents.remove());
//				receiver.stop();
//			}
//		}
//	}
//
//	public void eiffelCloseNotStartedReceiverTest() throws EiffelException
//	{
//		EiffelConfiguration configuration = new EiffelConfig(domainId, exchangeName, messageBusHost);
//		try(EiffelEventReceiver receiver = new EiffelEventReceiver(configuration, false, secondaryBindings))
//		{
//		}
//	}
}
