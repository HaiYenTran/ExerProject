package test;

import java.util.Arrays;
import java.util.Queue;

import com.ericsson.duraci.eiffelmessage.sending.exceptions.EiffelMessageSenderException;
import com.ericsson.eiffel.becrux.core.EiffelConfigurationILES;
import com.ericsson.eiffel.becrux.core.EiffelEventReceiver;
import com.ericsson.eiffel.becrux.core.EiffelEventSender;
import com.ericsson.eiffel.becrux.core.EiffelException;
import com.ericsson.eiffel.becrux.data.Mtas;
import com.ericsson.eiffel.becrux.data.Node;
import com.ericsson.eiffel.becrux.events.Event;
import com.ericsson.eiffel.becrux.events.NBPEvent;
import com.ericsson.eiffel.becrux.events.OPBEvent;
import com.ericsson.eiffel.becrux.versions.Version;

public class Test {
	
	public static void main(String[] args) throws EiffelMessageSenderException, EiffelException {
		//HOW SEND EVENT TO US
		EiffelConfigurationILES configSender = new EiffelConfigurationILES();
		try(EiffelEventSender sender = new EiffelEventSender(configSender)) {
			OPBEvent opbEvent = new OPBEvent();
			opbEvent.setVote(true);
			opbEvent.setComment("comment");
			opbEvent.setSignum("ESOMEBODY");
			
			Node node = new Mtas(Version.create("2.0"));
			opbEvent.setProducts(Arrays.asList(node.getType()));
			opbEvent.setBaselines(Arrays.asList(node.getVersion()));
			sender.sendEvent(opbEvent, "ILESGuardian");
		}
		
		//HOW RECIEVE EVENT FOR US
		EiffelConfigurationILES config = new EiffelConfigurationILES();
		config.setTagName("leo");
		try(EiffelEventReceiver receiver = new EiffelEventReceiver(config)){
			EiffelConfigurationILES configILES = new EiffelConfigurationILES();
			configILES.setConsumerName("nwft-consumer");
			configILES.setTagName("leo");
			try(EiffelEventSender sender = new EiffelEventSender(configILES)) {
				receiver.start();
				NBPEvent nbpEvent = new NBPEvent();
				nbpEvent.setVote(true);
				nbpEvent.setComment("comment");
				nbpEvent.setSignum("ESOMEBODY");
				
				Node node = new Mtas(Version.create("2.0"));
				nbpEvent.setProducts(Arrays.asList(node.getType()));
				nbpEvent.setBaselines(Arrays.asList(node.getVersion()));
				sender.sendEvent(nbpEvent, "leo");
				receiver.waitForEvent();
				Queue<Event> receivedEvents = receiver.getEventQueue();
				System.out.println(receivedEvents.element().toJson());
			}
		}
	}

}
