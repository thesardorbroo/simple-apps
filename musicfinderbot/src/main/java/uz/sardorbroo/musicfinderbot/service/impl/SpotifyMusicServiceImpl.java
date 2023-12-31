package uz.sardorbroo.musicfinderbot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import uz.sardorbroo.musicfinderbot.service.SpotifyMusicService;
import uz.sardorbroo.musicfinderbot.service.dto.PageDTO;
import uz.sardorbroo.musicfinderbot.service.dto.spotify.SpotifyMusicDTO;
import uz.sardorbroo.musicfinderbot.service.dto.spotify.SpotifyTrackResourceDTO;
import uz.sardorbroo.musicfinderbot.service.enumeration.MusicSourceType;
import uz.sardorbroo.musicfinderbot.service.integration.SpotifyClientService;

import java.io.InputStream;
import java.util.Optional;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "music.service.spotify", name = "simulate", havingValue = "false", matchIfMissing = true)
public class SpotifyMusicServiceImpl implements SpotifyMusicService {
    private static final String TEMP_MUSIC_NAME = "Override the name of music!";
    private final SpotifyClientService clientService;

    public SpotifyMusicServiceImpl(SpotifyClientService clientService) {
        this.clientService = clientService;
    }

    public Optional<SpotifyMusicDTO> search(String name, MusicSourceType type, PageDTO pagination) {
        return clientService.search(name, type, pagination);
    }

    public Optional<SpotifyTrackResourceDTO> download(String musicId) {

        if (StringUtils.isBlank(musicId)) {
            log.warn("Invalid argument is passed! MusicID must not be empty!");
            return Optional.empty();
        }

        Optional<InputStream> inputStreamOptional = clientService.download(musicId);
        if (inputStreamOptional.isEmpty()) {
            log.warn("Music is not downloaded!");
            return Optional.empty();
        }

        SpotifyTrackResourceDTO track = new SpotifyTrackResourceDTO();
        track.setInputStream(inputStreamOptional.get());
        track.setName(TEMP_MUSIC_NAME);
        track.setId(musicId);

        return Optional.of(track);
    }
}
