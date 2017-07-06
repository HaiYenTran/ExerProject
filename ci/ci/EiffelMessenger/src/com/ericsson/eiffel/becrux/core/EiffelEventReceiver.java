package com.ericsson.eiffel.becrux.core;

import java.util.LinkedList;
import java.util.Queue;

import com.ericsson.duraci.configuration.EiffelConfiguration;
import com.ericsson.duraci.eiffelmessage.binding.MessageBusBindings;
import com.ericsson.duraci.eiffelmessage.binding.MessageConsumer;
import com.ericsson.duraci.eiffelmessage.binding.configuration.BindingConfiguration;
import com.ericsson.duraci.eiffelmessage.binding.exceptions.BindingDisposalException;
import com.ericsson.duraci.eiffelmessage.binding.exceptions.EiffelMessageBindingException;
import com.ericsson.duraci.eiffelmessage.binding.exceptions.EiffelMessageConsumptionException;
import com.ericsson.duraci.eiffelmessage.binding.exceptions.RecoverableEiffelMessageConsumptionException;
import com.ericsson.duraci.eiffelmessage.messages.EiffelEvent;
import com.ericsson.duraci.eiffelmessage.messages.EiffelMessage;
import com.ericsson.eiffel.becrux.events.Event;

public class EiffelEventReceiver implements IEventReceiver
{
	private String fullBindingKey;
	private EiffelConfigurationILES ILESConfig;
	private EiffelConfiguration configuration;
	private MessageBusBindings bindings;
	private BindingConfiguration bindingConfiguration;
	private MessageConsumer consumer;

	private boolean disposed;
	
	private Queue<EiffelMessage> queue = new LinkedList<>();
	private final Object lock = new Object();

	private static final long defaultTimeout = 5000;
	private static final String objectDisposedError = "Object disposed";
	private static final String defaultTypeEvent = "experimental.generic";
	

	public EiffelEventReceiver(EiffelConfigurationILES ILESConfig) throws EiffelException
	{
		this.ILESConfig = ILESConfig;
		this.configuration= ILESConfig;
		this.fullBindingKey = buildBindingKey(ILESConfig.getTagName());
		init();
	}

	private void init() throws EiffelException
	{
		final String resultConsumerName = ILESConfig.getConsumerName();
		this.consumer = new MessageConsumer()
		{
			@Override
			public void consumeMessage(EiffelMessage message) throws EiffelMessageConsumptionException, RecoverableEiffelMessageConsumptionException
			{
				if(message != null)
				{
					synchronized(lock)
					{
						queue.add(message);
					}
				}
			}

			@Override
			public String getName()
			{
				return resultConsumerName;
			}
		};
		bindingConfiguration = new BindingConfiguration(false, true, consumer, fullBindingKey, null);
		disposed = false;
	}

	private String buildBindingKey(String tag)//Tag can be null
	{
		return defaultTypeEvent+"."+tag+"."+ILESConfig.getDomainId();
	}

	public String getFullBindingKey()
	{
		return fullBindingKey;
	}

	public void start() throws EiffelException
	{
		if(disposed)
			throw new IllegalStateException(objectDisposedError);

		if(!MessageBusBindings.checkConnection(configuration.getMessageBus()))
		{
			throw new EiffelException("Connection not possible to specified exchange");
		}
		if(bindings == null)
			bindings = new MessageBusBindings.Factory().create(configuration.getMessageBus(), configuration.getDomainId());
		try
		{
			if(!bindings.getAllBindingConfigurations().contains(bindingConfiguration))
			{
				bindings.add(bindingConfiguration);
				bindings.unPause(bindingConfiguration);
			}
		}
		catch(EiffelMessageBindingException ex)
		{
			throw new EiffelException(ex);
		}
	}
	
	public void stop() throws EiffelException
	{
		if(disposed)
			throw new IllegalStateException(objectDisposedError);

		try
		{
			if(bindings != null && bindings.getAllBindingConfigurations().contains(bindingConfiguration))
				bindings.remove(bindingConfiguration, false);
		}
		catch(EiffelMessageBindingException ex)
		{
			throw new EiffelException(ex);
		}
	}

	@Override
	public void close()
	{
		if(!disposed)
		{
			try
			{
				stop();
			}
			catch(EiffelException ex) {}
			try
			{
				if(bindings != null)
					bindings.dispose();
			}
			catch(BindingDisposalException ex) {}
			disposed = true;
		}
	}

	@Override
	public boolean isStarted()
	{
		if(!disposed && bindings != null)
			return bindings.getAllBindingConfigurations().contains(bindingConfiguration);
		return false;
	}

	public Queue<EiffelMessage> getEiffelMessageQueue()
	{
		if(disposed)
			throw new IllegalStateException(objectDisposedError);

		Queue<EiffelMessage> result = new LinkedList<>();
		synchronized(lock)
		{
			queue.stream().forEach(result::add);
			queue.clear();
		}
		return result;
	}

	public Queue<EiffelEvent> getEiffelEventQueue()
	{
		if(disposed)
			throw new IllegalStateException(objectDisposedError);

		Queue<EiffelEvent> result = new LinkedList<>();
		synchronized(lock)
		{
			queue.stream().map(m -> m.getEvent()).forEach(result::add);
			queue.clear();
		}
		return result;
	}

	public Queue<Event> getEventQueue()
	{
		if(disposed)
			throw new IllegalStateException(objectDisposedError);

		Queue<Event> result = new LinkedList<>();
		synchronized(lock)
		{
			for(EiffelMessage message : queue)
			{
				try
				{
					result.add(EiffelEventConverter.convertToEvent(message));
				}
				catch(NullPointerException ex)
				{
					continue;
				}
			}
			queue.clear();
		}
		return result;
	}

	public int size()
	{
		if(disposed)
			throw new IllegalStateException(objectDisposedError);

		synchronized(lock)
		{
			return queue.size();
		}
	}

	public void waitForEvent() throws EiffelException
	{
		waitForEvent(defaultTimeout);
	}

	public void waitForEvent(long timeout) throws EiffelException
	{
		waitForEvent(1, timeout);
	}

	public void waitForEvent(int count) throws EiffelException
	{
		waitForEvent(count, defaultTimeout);
	}

	public void waitForEvent(int count, long timeout) throws EiffelException
	{
		if(disposed)
			throw new IllegalStateException(objectDisposedError);

		if(timeout <= 0)
			throw new EiffelException("Invalid timeout value");

		long startTime = System.currentTimeMillis();
		try
		{
			while(true)
			{
				if((System.currentTimeMillis() - startTime) > timeout)
					throw new EiffelException("Timeout");
				if(size() >= count)
					return;

				Thread.sleep(timeout / 10);
			}
		}
		catch(InterruptedException ex)
		{
			throw new EiffelException(ex);
		}
	}
}