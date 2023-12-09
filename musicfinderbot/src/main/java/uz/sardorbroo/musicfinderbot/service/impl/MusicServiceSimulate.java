package uz.sardorbroo.musicfinderbot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import uz.sardorbroo.musicfinderbot.service.MusicService;
import uz.sardorbroo.musicfinderbot.service.dto.MusicDTO;
import uz.sardorbroo.musicfinderbot.service.dto.PageDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "music.service", name = "simulate", havingValue = "true")
// Todo: should move the strings to Java POJO
public class MusicServiceSimulate implements MusicService {

    @Override
    public List<MusicDTO> find(String musicName, PageDTO pagination) {

        if (StringUtils.isBlank(musicName)) {
            log.warn("Invalid argument is passed! Music name must not be null!");
            return Collections.emptyList();
        }

        List<MusicDTO> musics = new ArrayList<>();
        for (int i = 0; i < pagination.getSize(); i++) {
            MusicDTO music = build(musicName, i + 1);
            musics.add(music);
        }

        return musics;
    }

    @Override
    public byte[] download(String musicId) {
        return new byte[0];
    }

    private MusicDTO build(String name, int index) {

        return new MusicDTO()
                .setId(String.valueOf(UUID.randomUUID()))
                .setTitle(name + "_" + index)
                .setArtist("Fake artist_" + index)
                .setUrl2Download("Fake URL");
    }
}
