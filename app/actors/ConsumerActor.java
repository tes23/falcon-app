package actors;

import akka.actor.ActorRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.akka.InjectedActorSupport;
import models.ChannelMessage;

import javax.inject.Inject;

import static actors.ConsumerActorProtocol.ActorNamePath.*;


public class ConsumerActor extends BaseActor implements InjectedActorSupport{
    private final static Logger LOGGER = LoggerFactory.getLogger(ConsumerActor.class);

    public static final String MESSAGE_BROADCASTER_PATH = USER_PATH + CONSUMER + "/" + MESSAGE_BROADCASTER;

    @Inject
    private ConsumerActorProtocol.PersisterFactory persisterFactory;
    @Inject
    private ConsumerActorProtocol.PublisherFactory publisherFactory;
    @Inject
    private ConsumerActorProtocol.SubscriberFactory subscriberFactory;
    @Inject
    private ConsumerActorProtocol.MessageBroadcasterFactory messageBroadcasterFactory;

    private ActorRef persisterActorRef;
    private ActorRef publisherActorRef;
    private ActorRef subscriberActorRef;
    private ActorRef messageBroadcasterActorRef;

    @Override
    public void preStart() throws Exception {
        LOGGER.debug("preStart()");
        super.preStart();

        persisterActorRef = injectedChild(() -> persisterFactory.create(), PERSISTER);
        publisherActorRef = injectedChild(() -> publisherFactory.create(), PUBLISHER);
        subscriberActorRef = injectedChild(() -> subscriberFactory.create(), SUBSCRIBER);
        messageBroadcasterActorRef = injectedChild(() -> messageBroadcasterFactory.create(), MESSAGE_BROADCASTER);

        subscriberActorRef.tell(getSelf(), getSelf());
    }

    @Override
    public void onReceive(Object message) throws Exception {
        LOGGER.info("Received command:" + message);

        if(message instanceof ChannelMessage) {
            ChannelMessage channelMessage = (ChannelMessage) message;
            publisherActorRef.tell(channelMessage, getSelf());
            persisterActorRef.tell(channelMessage, getSelf());

            sender().tell("Received message: " + channelMessage.getMessage(), self());
        } else {
            unhandled(message);
        }
    }
}
