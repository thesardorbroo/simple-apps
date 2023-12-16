package uz.sardorbroo.musicfinderbot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.sardorbroo.musicfinderbot.service.MusicTempStorageService;
import uz.sardorbroo.musicfinderbot.service.dto.MusicDTO;

import java.util.*;

@Slf4j
@Service
public class MusicTempMapStorageServiceImpl implements MusicTempStorageService {

    private static final Map<UUID, MusicDTO> MUSICS_STORAGE = new HashMap<>();

    @Override
    public Optional<UUID> save(MusicDTO music) {
        log.debug("Save musics to temp Map storage");

        if (Objects.isNull(music)) {
            log.warn("Invalid argument is passed! MusicDTO must not be null!");
            return Optional.empty();
        }

        UUID key4Music = UUID.randomUUID();

        MUSICS_STORAGE.put(key4Music, music);

        return Optional.of(key4Music);
    }

    @Override
    public Optional<MusicDTO> getById(UUID musicId) {
        log.debug("Get stored music by ID! ID: {}", musicId);

        if (Objects.isNull(musicId)) {
            log.warn("Invalid argument is passed! MusicID must not be null!");
            return Optional.empty();
        }

        return MUSICS_STORAGE.containsKey(musicId) ? Optional.of(MUSICS_STORAGE.get(musicId)) : Optional.empty();
    }
}
