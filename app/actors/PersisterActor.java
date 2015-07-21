package actors;

import models.ChannelMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.RedisTool;

public class PersisterActor extends BaseActor {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersisterActor.class);

    @Override
    public void postStop() throws Exception {
        obtainRedisTool().shutdownPersisterConnection();
        super.postStop();
    }

    @Override
    public void onReceive(Object message) throws Exception {
        LOGGER.debug("Message received");

        if (message instanceof ChannelMessage) {
            final String msg = ((ChannelMessage) message).getMessage();

            RedisTool redisTool = obtainRedisTool();
            redisTool.persist(msg);
        }
    }
}
