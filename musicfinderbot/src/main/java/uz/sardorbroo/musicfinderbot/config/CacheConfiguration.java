package uz.sardorbroo.musicfinderbot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPooled;
import uz.sardorbroo.musicfinderbot.config.constants.CacheType;
import uz.sardorbroo.musicfinderbot.util.StringMaskUtils;

@Slf4j
@Configuration
public class CacheConfiguration {

    @Value("${cache.server.host:127.0.0.1}")
    private String host;

    @Value("${cache.server.port:6379}")
    private Integer port;

    @Bean
    @ConditionalOnProperty(prefix = "cache.storage", name = "type", havingValue = CacheType.REDIS)
    public JedisPooled jedisCacheClientInstance() {
        log.info("Initializing jedis pooled... Host & Port: {}", getMaskedHostAndPort());

        JedisPooled pool = new JedisPooled(host, port);

        log.info("Jedis pooled is initialized successfully!");
        return pool;
    }

    private String getMaskedHostAndPort() {

        return String.join(" ", StringMaskUtils.mask(host), StringMaskUtils.mask(String.valueOf(port)));
    }
}
