package actors;

import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import play.libs.Akka;
import play.mvc.WebSocket;
import redis.ChannelMessage;

import java.util.HashMap;
import java.util.Map;

public class MessageBroadcasterActor extends UntypedActor {
    private Map<String, WebSocket.Out<String>> clients = new HashMap<>();

    public static void registerClient(final String id, WebSocket.In<String> in, WebSocket.Out<String> out) {
        ActorSelection actor = Akka.system().actorSelection(ConsumerActor.MESSAGE_BROADCASTER_PATH);
        actor.tell(new RegistrationMessage(id, out), null);

        //nothing to do when a client joins
        in.onMessage(s -> {});

        in.onClose(() -> actor.tell(new UnRegistrationMessage(id), null));
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof RegistrationMessage) {
            RegistrationMessage regMessage  = (RegistrationMessage) message;
            clients.put(regMessage.id, regMessage.channel);
            System.out.println("New client is arrived with id:" + ((RegistrationMessage) message).id + ", available clients:" + clients.size());
        } else if(message instanceof ChannelMessage) {
            ChannelMessage channelMessage = (ChannelMessage) message;
            clients.forEach((id, channel) -> channel.write(channelMessage.getName()));
            System.out.println("Message sent to clients:" + channelMessage.getName() + ", available clients:" + clients.size());
        } else if(message instanceof UnRegistrationMessage) {
            UnRegistrationMessage unRegMessage = (UnRegistrationMessage) message;
            clients.remove(unRegMessage.id);
            System.out.println("Client quits with id:" + ((UnRegistrationMessage) message).id + ", available clients:" + clients.size());
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
