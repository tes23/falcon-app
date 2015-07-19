import actors.*;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import play.libs.akka.AkkaGuiceSupport;
import redis.*;

public class GuiceModule extends AbstractModule implements AkkaGuiceSupport {

    @Override
    protected void configure() {
        bindActor(PubSubParentActor.class, PubSubParentActorProtocol.ActorNamePath.PUB_SUB_PARENT);
        bindActorFactory(PublisherActor.class, PubSubParentActorProtocol.PublisherFactory.class);
        bindActorFactory(SubscriberActor.class, PubSubParentActorProtocol.SubscriberFactory.class);
        bindActorFactory(MessageBroadcasterActor.class, PubSubParentActorProtocol.MessageBroadcasterFactory.class);

        install(new FactoryModuleBuilder().implement(RedisTool.class, RedisToolImpl.class).build(RedisToolFactory.class));

        bind(RedisConnectionPool.class).to(RedisConnectionPoolImpl.class);
    }
}
