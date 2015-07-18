package redis;

public interface RedisToolFactory {

    RedisTool obtainRedisTool(String host, int port);
}
