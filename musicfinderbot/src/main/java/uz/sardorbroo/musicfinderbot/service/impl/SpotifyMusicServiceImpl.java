package uz.sardorbroo.musicfinderbot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import uz.sardorbroo.musicfinderbot.service.SpotifyMusicService;
import uz.sardorbroo.musicfinderbot.service.dto.PageDTO;
import uz.sardorbroo.musicfinderbot.service.dto.spotify.SpotifyMusicDTO;
import uz.sardorbroo.musicfinderbot.service.enumeration.MusicSourceType;
import uz.sardorbroo.musicfinderbot.service.integration.SpotifyClientService;

import java.util.Optional;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "music.service.spotify", name = "simulate", havingValue = "false", matchIfMissing = true)
public class SpotifyMusicServiceImpl implements SpotifyMusicService {

    private final SpotifyClientService clientService;

    public SpotifyMusicServiceImpl(SpotifyClientService clientService) {
        this.clientService = clientService;
    }

    public Optional<SpotifyMusicDTO> search(String name, MusicSourceType type, PageDTO pagination) {
        return clientService.search(name, type, pagination);
    }

    public byte[] download(String musicId) {
        return clientService.download(musicId);
    }
}
