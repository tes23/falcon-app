package actors;

import com.google.inject.Inject;
import constants.ChannelName;
import redis.MessageListener;
import redis.RedisTool;

public class PersisterActor extends BaseActor {

    @Inject
    private MessageListener messageListener;

    @Override
    public void postStop() throws Exception {
        obtainRedisTool().unsubscribe(ChannelName.CHANNEL.name());
        //TODO: clean up connection
        super.postStop();
        System.out.println("postStop() on SubscriberActor");
    }

    @Override
    public void onReceive(Object message) throws Exception {
        System.out.println(this.getClass().getSimpleName() + " is called");

        if (message instanceof ChannelMessage) {
            final String msg = ((ChannelMessage) message).getMessage();

            RedisTool redisTool = obtainRedisTool();
            redisTool.persist(msg);
        }
    }
}
