package uz.sardorbroo.musicfinderbot.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPooled;
import uz.sardorbroo.musicfinderbot.config.constants.CacheType;
import uz.sardorbroo.musicfinderbot.service.MusicTempStorageService;
import uz.sardorbroo.musicfinderbot.service.dto.MusicDTO;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "cache.storage", name = "type", havingValue = CacheType.REDIS)
public class MusicTempRedisStorageServiceImpl implements MusicTempStorageService {

    private final JedisPooled jedis;
    private final ObjectMapper mapper;

    @Value("${cache.storage.ttl:300}") // 300 second = 5 min
    private long time2LiveInSecond;

    public MusicTempRedisStorageServiceImpl(JedisPooled jedis, ObjectMapper mapper) {
        this.jedis = jedis;
        this.mapper = mapper;
    }

    @Override
    public Optional<String> save(MusicDTO music) {
        log.debug("Caching music with Jedis");

        if (Objects.isNull(music) || StringUtils.isBlank(music.getId())) {
            log.warn("Invalid argument is passed! Music.ID must not be empty!");
            return Optional.empty();
        }

        Optional<String> musicOptional = convert(music);
        if (musicOptional.isEmpty()) {
            log.warn("Music is not converted to String!");
            return Optional.empty();
        }

        String musicId = jedis.setex(music.getId(), time2LiveInSecond, musicOptional.get());

        log.debug("Music is cached successfully. Cache ID: {}", musicId);
        return Optional.of(musicId);
    }

    @Override
    public Optional<MusicDTO> getById(String musicId) {
        log.debug("Get cached object by ID: {}", musicId);

        if (StringUtils.isBlank(musicId)) {
            log.warn("Invalid argument is passed! MusicID must not be empty!");
            return Optional.empty();
        }

        return getCachedMusic(musicId);
    }

    private Optional<MusicDTO> getCachedMusic(String musicId) {

        String musicAsString = jedis.get(musicId);
        return convert(musicAsString);
    }

    private Optional<MusicDTO> convert(String musicAsString) {

        try {
            MusicDTO music = mapper.readValue(musicAsString, new TypeReference<MusicDTO>() {});
            return Optional.of(music);
        } catch (Exception e) {
            log.warn("Cached object type not MusicDTO! Exception: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<String> convert(MusicDTO music) {

        try {
            String musicAsString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(music);
            return Optional.of(musicAsString);
        } catch (Exception e) {
            log.warn("Error while converting MusicDTO to String! Exception: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
