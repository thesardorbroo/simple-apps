package uz.sardorbroo.musicfinderbot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import uz.sardorbroo.musicfinderbot.service.MusicService;
import uz.sardorbroo.musicfinderbot.service.MusicTempStorageService;
import uz.sardorbroo.musicfinderbot.service.SpotifyMusicService;
import uz.sardorbroo.musicfinderbot.service.dto.MusicDTO;
import uz.sardorbroo.musicfinderbot.service.dto.MusicResourceDTO;
import uz.sardorbroo.musicfinderbot.service.dto.PageDTO;
import uz.sardorbroo.musicfinderbot.service.dto.spotify.SpotifyDataDTO;
import uz.sardorbroo.musicfinderbot.service.dto.spotify.SpotifyMusicDTO;
import uz.sardorbroo.musicfinderbot.service.dto.spotify.SpotifyTrack;
import uz.sardorbroo.musicfinderbot.service.dto.spotify.SpotifyTrackResourceDTO;
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
    private final SpotifyMusicService spotifyMusicService;
    private final MusicTempStorageService tempStorageService;

    public MusicServiceImpl(SpotifyMusicService spotifyMusicService, MusicTempStorageService tempStorageService) {
        this.spotifyMusicService = spotifyMusicService;
        this.tempStorageService = tempStorageService;
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

        saveAllMusic2TempStorage(spotifyMusics);

        log.debug("Musics are fetch successfully! Musics: {}", spotifyMusics.size());
        return spotifyMusics;
    }

    @Override
    public Optional<MusicResourceDTO> download(String musicId) {
        log.debug("Downloading music by ID. MusicID: {}", musicId);

        if (StringUtils.isBlank(musicId)) {
            log.warn("Invalid argument is passed! MusicID must not be null!");
            return Optional.empty();
        }

        Optional<SpotifyTrackResourceDTO> trackOptional = spotifyMusicService.download(musicId);
        if (trackOptional.isEmpty()) {
            log.warn("Music is not downloaded!");
            return Optional.empty();
        }

        return convertToResource(trackOptional.get());
    }

    private void saveAllMusic2TempStorage(List<MusicDTO> musics) {
        log.debug("Saving all musics to temporary storage");

        if (Objects.isNull(musics) || musics.isEmpty()) {
            log.warn("Invalid argument is passed! Musics collections must not be empty!");
            return;
        }

        musics.forEach(tempStorageService::save);
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

    private Optional<MusicResourceDTO> convertToResource(SpotifyTrackResourceDTO track) {
        Optional<MusicDTO> musicOptional = tempStorageService.getById(track.getId());

        if (musicOptional.isEmpty()) {
            log.warn("Music is not found from temporary storage! MusicID: {}", track.getId());
            return Optional.empty();
        }

        MusicDTO music = musicOptional.get();

        MusicResourceDTO musicResource = new MusicResourceDTO();
        musicResource.setId(music.getId());
        musicResource.setTitle(music.getTitle());
        musicResource.setDuration(music.getDuration());
        musicResource.setArtist(music.getArtist());
        musicResource.setUrl2Download(music.getUrl2Download());

        musicResource.setInputStream(track.getInputStream());
        return Optional.of(musicResource);
    }
}
