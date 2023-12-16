package uz.sardorbroo.musicfinderbot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uz.sardorbroo.musicfinderbot.service.MusicTempStorageService;
import uz.sardorbroo.musicfinderbot.service.dto.MusicDTO;

import java.util.*;

@Slf4j
@Service
public class MusicTempMapStorageServiceImpl implements MusicTempStorageService {

    private static final Map<String, MusicDTO> MUSICS_STORAGE = new HashMap<>();

    @Override
    public Optional<String> save(MusicDTO music) {
        log.debug("Save musics to temp Map storage");

        if (Objects.isNull(music) || StringUtils.isBlank(music.getId())) {
            log.warn("Invalid argument is passed! MusicDTO.ID must not be empty!");
            return Optional.empty();
        }

        MUSICS_STORAGE.put(music.getId(), music);

        return Optional.of(music.getId());
    }

    @Override
    public Optional<MusicDTO> getById(String musicId) {
        log.debug("Get stored music by ID! ID: {}", musicId);

        if (StringUtils.isBlank(musicId)) {
            log.warn("Invalid argument is passed! MusicID must not be empty!");
            return Optional.empty();
        }

        return MUSICS_STORAGE.containsKey(musicId) ? Optional.of(MUSICS_STORAGE.get(musicId)) : Optional.empty();
    }
}
