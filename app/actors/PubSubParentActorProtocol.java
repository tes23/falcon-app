package actors;

import akka.actor.Actor;

public class PubSubParentActorProtocol {

    public static class Message {
        public String name;

        public Message(String name) {
            this.name = name;
        }
    }

    public interface PublisherFactory {
        Actor create();
    }

    public interface SubscriberFactory {
        Actor create();
    }

    public interface MessageBroadcasterFactory {
        Actor create();
    }

    public static final class ActorNamePath {
        public static final String USER_PATH = "akka://application/user/";

        public static final String PUB_SUB_PARENT = "pubsub-parent-actor";
        public static final String SUBSCRIBER = "subscriber-actor";
        public static final String PUBLISHER = "publisher-actor";
        public static final String MESSAGE_BROADCASTER = "message-broadcaster-actor";
    }
}
