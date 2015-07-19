import actors.*;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import play.libs.akka.AkkaGuiceSupport;
import redis.*;

public class GuiceModule extends AbstractModule implements AkkaGuiceSupport {

    @Override
    protected void configure() {
        bindActor(ConsumerActor.class, ConsumerActorProtocol.ActorNamePath.CONSUMER);
        bindActorFactory(PublisherActor.class, ConsumerActorProtocol.PublisherFactory.class);
        bindActorFactory(SubscriberActor.class, ConsumerActorProtocol.SubscriberFactory.class);
        bindActorFactory(MessageBroadcasterActor.class, ConsumerActorProtocol.MessageBroadcasterFactory.class);

//        install(new FactoryModuleBuilder().implement(RedisTool.class, RedisToolImpl.class).build(RedisToolFactory.class));

        bind(RedisTool.class).to(RedisToolImpl.class);
        bind(RedisConnectionPool.class).to(RedisConnectionPoolImpl.class);
    }
}
