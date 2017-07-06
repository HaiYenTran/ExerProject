package com.ericsson.eiffel.becrux.core;

import java.util.Map;

import com.ericsson.duraci.configuration.EiffelConfiguration;
import com.ericsson.duraci.datawrappers.Arm;
import com.ericsson.duraci.datawrappers.MessageBus;
import com.ericsson.duraci.datawrappers.MessageSendQueue;

public class EiffelConfigurationILES implements EiffelConfiguration{
	private static final String defaultDomainId = "eiffel021.seki.fem002";
	private static final String defaultExchangeName = "mb001-eiffel021";
	private static final String messageBusHost = "amqps://mb001-eiffel021.rnd.ki.sw.ericsson.se";
	private static final String defaultTagName = "iles";
	private static final String defaultConsumerName = "leo";
	private static final String componentName="femleo-eiffel021";
	
	private String domainId = defaultDomainId;
	private String exchangeName = defaultExchangeName;
	private String tagName = defaultTagName;
	private String consumerName = defaultConsumerName;

	public String getDomainId() {
		return domainId;
	}


	public void setDomainId(String domainId) {
		this.domainId = domainId;
	}


	public String getExchangeName() {
		return exchangeName;
	}


	public void setExchangeName(String exchangeName) {
		this.exchangeName = exchangeName;
	}


	public String getTagName() {
		return tagName;
	}


	public void setTagName(String tagName) {
		this.tagName = tagName;
	}


	public String getConsumerName() {
		return consumerName;
	}


	public void setConsumerName(String consumerName) {
		this.consumerName = consumerName;
	}


	@Override
	public Map<String, Arm> getArms() {
		return null;
	}


	@Override
	public MessageBus getMessageBus() {
		return new MessageBus(messageBusHost, exchangeName, componentName, true);
	}


	@Override
	public MessageSendQueue getMessageSendQueue() {
		MessageSendQueue queue = new MessageSendQueue(5000);
		return queue;
	}
}
