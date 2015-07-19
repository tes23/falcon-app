package actors;

import akka.actor.UntypedActor;
import play.Configuration;
import redis.RedisTool;
import redis.RedisToolFactory;

import javax.inject.Inject;

public abstract class BaseActor extends UntypedActor {

    @Inject
    private Configuration configuration;
    @Inject
    private RedisToolFactory redisToolFactory;

    protected RedisTool obtainRedisTool() {
        return redisToolFactory.obtainRedisTool(getHost(), getPort());
    }

    protected String getHost() {
        return configuration.getString("redis.host");
    }

    protected int getPort() {
        return configuration.getInt("redis.port");
    }

}
