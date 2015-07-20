package actors;

import com.google.inject.Inject;
import constants.ChannelName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.MessageListener;
import redis.RedisTool;

public class SubscriberActor extends BaseActor {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriberActor.class);

    @Inject
    private MessageListener messageListener;

    @Override
    public void postStop() throws Exception {
        obtainRedisTool().unsubscribe(ChannelName.CHANNEL.name());
        //TODO: clean up connection
        super.postStop();
    }

    @Override
    public void onReceive(Object message) throws Exception {
        LOGGER.debug("Message received");

        RedisTool redisTool = obtainRedisTool();
        redisTool.subscribe(messageListener, ChannelName.CHANNEL.name());
    }
}
