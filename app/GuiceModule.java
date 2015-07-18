import com.google.inject.AbstractModule;
import play.libs.akka.AkkaGuiceSupport;
import redis.RedisConnectionPool;
import redis.RedisConnectionPoolImpl;

public class GuiceModule extends AbstractModule implements AkkaGuiceSupport {

    @Override
    protected void configure() {
        bind(RedisConnectionPool.class).to(RedisConnectionPoolImpl.class);
    }
}
