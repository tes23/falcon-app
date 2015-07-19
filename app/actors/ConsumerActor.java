package actors;

import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import play.libs.akka.InjectedActorSupport;
import redis.ChannelMessage;
import scala.Option;

import javax.inject.Inject;

import static actors.ConsumerActorProtocol.ActorNamePath.*;


public class ConsumerActor extends BaseActor implements InjectedActorSupport{
    public static final String MESSAGE_BROADCASTER_PATH = USER_PATH + CONSUMER + "/" + MESSAGE_BROADCASTER;

    private LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

    @Inject
    private ConsumerActorProtocol.PublisherFactory publisherFactory;
    @Inject
    private ConsumerActorProtocol.SubscriberFactory subscriberFactory;
    @Inject
    private ConsumerActorProtocol.MessageBroadcasterFactory messageBroadcasterFactory;

    private ActorRef publisherActorRef;
    private ActorRef subscriberActorRef;
    private ActorRef messageBroadcasterActorRef;

    @Override
    public void preRestart(Throwable reason, Option<Object> message) throws Exception {
        super.preRestart(reason, message);
        System.out.println("preRestart(with params)");
//        for (ActorRef each : getContext().getChildren()) {
//            getContext().unwatch(each);
//            getContext().stop(each);
//        }
//        postStop();
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        System.out.println("ConsumerActor preStart() ");

        publisherActorRef = injectedChild(() -> publisherFactory.create(), PUBLISHER);
        subscriberActorRef = injectedChild(() -> subscriberFactory.create(), SUBSCRIBER);
        messageBroadcasterActorRef = injectedChild(() -> messageBroadcasterFactory.create(), MESSAGE_BROADCASTER);

        //TODO schedule at startup subscriber to make sure subscription is done before we  publish
        subscriberActorRef.tell(getSelf(), getSelf());
    }

    @Override
    public void postRestart(Throwable reason) throws Exception {
        super.postRestart(reason);
        System.out.println("postRestart()");
//        preStart();
//
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
    }

    @Override
    public void onReceive(Object message) throws Exception {
        logger.info("Received command:" + message);

        if(message instanceof ChannelMessage) {
            ChannelMessage channelMessage = (ChannelMessage) message;
            publisherActorRef.tell(channelMessage, getSelf());
            messageBroadcasterActorRef.tell(channelMessage, getSelf());

            sender().tell("Received message: " + channelMessage.getMessage(), self());
        } else {
            unhandled(message);
        }
    }
}
