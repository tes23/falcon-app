package actors;

import akka.actor.UntypedActor;
import redis.RedisTool;

import javax.inject.Inject;

public abstract class BaseActor extends UntypedActor {

    @Inject
    private RedisTool redisTool;

    protected RedisTool obtainRedisTool() {
        return redisTool;
    }
}
