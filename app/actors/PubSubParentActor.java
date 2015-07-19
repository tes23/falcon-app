package actors;

import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import play.libs.akka.InjectedActorSupport;
import redis.ChannelMessage;
import scala.Option;

import javax.inject.Inject;

public class PubSubParentActor extends BaseActor implements InjectedActorSupport{
    public static final String NAME = "pubsub-parent-actor";
    private LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

//    @Inject @Named(PublisherActor.NAME)
    private ActorRef publisherActorRef;
    @Inject
    PubSubParentActorProtocol.PublisherFactory childPublisherFactory;
    @Inject
    PubSubParentActorProtocol.SubscriberFactory childSubscriberFactory;

//    @Inject @Named(SubscriberActor.NAME)
    private ActorRef subscriberActorRef;

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
        System.out.println("preStart() ");

        publisherActorRef = injectedChild(() -> childPublisherFactory.create(), PublisherActor.NAME);
        subscriberActorRef = injectedChild(() -> childSubscriberFactory.create(), SubscriberActor.NAME);

        subscriberActorRef.tell(getSelf(), getSelf());
    }

    @Override
    public void postRestart(Throwable reason) throws Exception {
        super.postRestart(reason);
        System.out.println("postRestart()");
//        preStart();
//
//        //TODO schedule at startup subscriber to make sure subscription is done before we  publish
//        subscriberActorRef.tell(getSelf(), getSelf());
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
    }

    @Override
    public void onReceive(Object message) throws Exception {
        logger.info("Received command" + message);

        if(message instanceof PubSubParentActorProtocol.Message) {
            PubSubParentActorProtocol.Message protocolMessage = (PubSubParentActorProtocol.Message) message;

            ChannelMessage channelMessage = new ChannelMessage(protocolMessage.name);
            publisherActorRef.tell(channelMessage, getSelf());

            sender().tell("Received name: " + protocolMessage.name, self());
        } else {
            unhandled(message);
        }
    }
}