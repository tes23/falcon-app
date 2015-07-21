package actors;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.MessageListener;
import redis.RedisTool;

import static actors.ConsumerActorProtocol.ChannelName.CHANNEL;

public class SubscriberActor extends BaseActor {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriberActor.class);

    @Inject
    private MessageListener messageListener;

    @Override
    public void postStop() throws Exception {
        obtainRedisTool().unsubscribe(CHANNEL.name());
        obtainRedisTool().shutdownSubscriberConnection();
        super.postStop();
    }

    @Override
    public void onReceive(Object message) throws Exception {
        LOGGER.debug("Message received");

        RedisTool redisTool = obtainRedisTool();
        redisTool.subscribe(messageListener, CHANNEL.name());
    }
}
