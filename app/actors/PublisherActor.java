package actors;

import constants.ChannelName;
import redis.ChannelMessage;
import redis.RedisTool;

public class PublisherActor extends BaseActor {
    public static final String NAME = "publisher-actor";

    @Override
    public void postStop() throws Exception {
        super.postStop();
        //TODO: clean up connection
    }

    @Override
    public void onReceive(Object message) throws Exception {
        System.out.println(this.getClass().getSimpleName() + " is called");

        if (message instanceof ChannelMessage) {
            final String name = ((ChannelMessage) message).getName();
            System.out.println("Received name: " + name);

            RedisTool redisTool = obtainRedisTool();
            redisTool.publish(ChannelName.CHANNEL.name(), name);

//            redisTool.subscribe(messageListener, CHANNEL);
//            redisTool.persist(name);

        } else {
            unhandled(message);
        }
    }


}
