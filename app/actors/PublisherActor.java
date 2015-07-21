package actors;

import models.ChannelMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.RedisTool;

import static actors.ConsumerActorProtocol.ChannelName.CHANNEL;

public class PublisherActor extends BaseActor {
    private static final Logger LOGGER = LoggerFactory.getLogger(PublisherActor.class);

    @Override
    public void postStop() throws Exception {
        super.postStop();
        //TODO: clean up connection
    }

    @Override
    public void onReceive(Object message) throws Exception {
        LOGGER.debug("Message received");

        if (message instanceof ChannelMessage) {
            final String msg = ((ChannelMessage) message).getMessage();

            RedisTool redisTool = obtainRedisTool();
            redisTool.publish(CHANNEL.name(), msg);
        } else {
            unhandled(message);
        }
    }


}
