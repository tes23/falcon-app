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
}
