package actors;

import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.Akka;
import play.mvc.WebSocket;
import models.ChannelMessage;

import java.util.HashMap;
import java.util.Map;

public class MessageBroadcasterActor extends UntypedActor {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageBroadcasterActor.class);

    private Map<String, WebSocket.Out<String>> clients = new HashMap<>();

    public static void registerClient(final String id, WebSocket.In<String> in, WebSocket.Out<String> out) {
        ActorSelection actor = selectMessageBroadcasterActor();
        actor.tell(new RegistrationMessage(id, out), null);

        //nothing to do when a client joins
        in.onMessage(s -> {});

        in.onClose(() -> actor.tell(new UnRegistrationMessage(id), null));
    }

    public static void notifyClients(String message) {
        ActorSelection actor = selectMessageBroadcasterActor();
        actor.tell(new ChannelMessage(message), null);
    }

    private static ActorSelection selectMessageBroadcasterActor() {
        return Akka.system().actorSelection(ConsumerActor.MESSAGE_BROADCASTER_PATH);
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof RegistrationMessage) {
            RegistrationMessage regMessage  = (RegistrationMessage) message;

            clients.put(regMessage.id, regMessage.channel);
            LOGGER.debug("New client is arrived with id:" + regMessage.id + ", available clients:" + clients.size());
        } else if(message instanceof ChannelMessage) {
            ChannelMessage channelMessage = (ChannelMessage) message;

            clients.forEach((id, channel) -> channel.write(channelMessage.getMessage()));
            LOGGER.debug("Message sent to clients:" + channelMessage.getMessage() + ", available clients:" + clients.size());
        } else if(message instanceof UnRegistrationMessage) {
            UnRegistrationMessage unRegMessage = (UnRegistrationMessage) message;

            clients.remove(unRegMessage.id);
            LOGGER.debug("Client quits with id:" + unRegMessage.id + ", available clients:" + clients.size());
        }
    }

    public static class RegistrationMessage {
        public String id;
        public WebSocket.Out<String> channel;

        public RegistrationMessage(String id, WebSocket.Out<String> channel) {
            this.id = id;
            this.channel = channel;
        }
    }

    public static class UnRegistrationMessage {
        public String id;

        public UnRegistrationMessage(String id) {
            this.id = id;
        }
    }
}
