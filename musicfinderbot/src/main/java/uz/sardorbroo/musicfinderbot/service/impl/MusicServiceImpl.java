package uz.sardorbroo.musicfinderbot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import uz.sardorbroo.musicfinderbot.service.MusicService;
import uz.sardorbroo.musicfinderbot.service.SpotifyMusicService;
import uz.sardorbroo.musicfinderbot.service.dto.MusicDTO;
import uz.sardorbroo.musicfinderbot.service.dto.PageDTO;
import uz.sardorbroo.musicfinderbot.service.dto.spotify.SpotifyDataDTO;
import uz.sardorbroo.musicfinderbot.service.dto.spotify.SpotifyMusicDTO;
import uz.sardorbroo.musicfinderbot.service.dto.spotify.SpotifyTrack;
import uz.sardorbroo.musicfinderbot.service.enumeration.MusicSourceType;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "music.service", name = "simulate", havingValue = "false", matchIfMissing = true)
public class MusicServiceImpl implements MusicService {
    private final static byte[] EMPTY_BYTE_ARRAY = new byte[0];
    private final SpotifyMusicService spotifyMusicService;

    public MusicServiceImpl(SpotifyMusicService spotifyMusicService) {
        this.spotifyMusicService = spotifyMusicService;
    }

    @Override
    public List<MusicDTO> find(String musicName, PageDTO pagination) {
        log.debug("Find musics by name. Music name: {}", musicName);

        Optional<SpotifyMusicDTO> musicsOptional = spotifyMusicService.search(musicName, MusicSourceType.TRACK, pagination);
        if (musicsOptional.isEmpty()) {
            log.warn("Music is not found by name! Name: {}", musicName);
            return Collections.emptyList();
        }

        List<MusicDTO> spotifyMusics = convert(musicsOptional.get());
        log.debug("Musics are fetch successfully! Musics: {}", spotifyMusics.size());
        return spotifyMusics;
    }

    @Override
    public byte[] download(String musicId) {
        log.debug("Downloading music by ID. MusicID: {}", musicId);

        if (StringUtils.isBlank(musicId)) {
            log.warn("Invalid argument is passed! MusicID must not be null!");
            return EMPTY_BYTE_ARRAY;
        }

        return spotifyMusicService.download(musicId);
    }


    private List<MusicDTO> convert(SpotifyMusicDTO spotifyMusics) {

        if (Objects.isNull(spotifyMusics)) {
            log.warn("Invalid argument is passed! SpotifyMusic must not be null!");
            return Collections.emptyList();
        }

        return spotifyMusics.getTracks().getItems().stream()
                .map(SpotifyDataDTO::getData)
                .map(this::convert)
                .collect(Collectors.toList());
    }

    private MusicDTO convert(SpotifyTrack track) {

        return new MusicDTO()
                .setId(track.getId())
                .setTitle(track.getName())
                .setArtist(track.joinArtists())
                .setUrl2Download(track.getUri())
                .setDuration(track.getDuration());
    }
}