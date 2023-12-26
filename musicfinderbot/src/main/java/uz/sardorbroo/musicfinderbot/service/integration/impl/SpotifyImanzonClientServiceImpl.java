package uz.sardorbroo.musicfinderbot.service.integration.impl;

import com.wrapper.spotify.Api;
import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.methods.TrackSearchRequest;
import com.wrapper.spotify.methods.authentication.ClientCredentialsGrantRequest;
import com.wrapper.spotify.models.Track;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uz.sardorbroo.musicfinderbot.service.dto.PageDTO;
import uz.sardorbroo.musicfinderbot.service.dto.spotify.SpotifyDataDTO;
import uz.sardorbroo.musicfinderbot.service.dto.spotify.SpotifyMusicDTO;
import uz.sardorbroo.musicfinderbot.service.dto.spotify.SpotifyRootTrack;
import uz.sardorbroo.musicfinderbot.service.dto.spotify.SpotifyTrack;
import uz.sardorbroo.musicfinderbot.service.enumeration.MusicSourceType;
import uz.sardorbroo.musicfinderbot.service.integration.SpotifyClientService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "music.service.spotify", name = "service", havingValue = "imanzano", matchIfMissing = true)
public class SpotifyImanzonClientServiceImpl implements SpotifyClientService {

    private final Api api;
    private final RestTemplate restTemplate;

    public SpotifyImanzonClientServiceImpl(@Qualifier("REAL") Api api) {
        this.api = api;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public Optional<SpotifyMusicDTO> search(String name, MusicSourceType type, PageDTO pagination) {
        log.debug("Start searching music by name with Imanzano library");

        Optional<TrackSearchRequest> trackSearchRequestOptional = searchMusic(name, pagination);
        if (trackSearchRequestOptional.isEmpty()) {
            log.warn("Music are not found! Music name: {}", name);
            return Optional.empty();
        }

        TrackSearchRequest trackSearchRequest = trackSearchRequestOptional.get();
        return mapTracks(trackSearchRequest);
    }

    @Override
    public Optional<InputStream> download(String musicId) {
        log.debug("Start downloading track by ID from Spotify");

        if (StringUtils.isBlank(musicId)) {
            log.warn("Invalid argument is passed! MusicID must not be blank!");
            return Optional.empty();
        }

        Optional<Track> trackOptional = getTrack(musicId);
        if (trackOptional.isEmpty()) {
            log.warn("Spotify track is not found! MusicID: {}", musicId);
            return Optional.empty();
        }

        Optional<String> accessTokenOptional = getAccessToken();
        if (accessTokenOptional.isEmpty()) {
            log.warn("Spotify access token is not found!");
            return Optional.empty();
        }

        try {
            return try2DownloadMusic(accessTokenOptional.get(), trackOptional.get());
        } catch (Exception e) {
            log.error("Error while download music from Spotify! Exception: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private void authorize() {

        Optional<String> accessTokenOptional = getAccessToken();
        if (accessTokenOptional.isEmpty()) {
            log.warn("Spotify access token is not found!");
            return;
        }

        api.setAccessToken(accessTokenOptional.get());
        log.debug("Access token is set to Api");
    }

    private Optional<String> getAccessToken() {
        try {

            final ClientCredentialsGrantRequest request = api.clientCredentialsGrant().build();
            String accessToken = request.get().getAccessToken();

            return Optional.of(accessToken);
        } catch (IOException | WebApiException e) {
            log.error("Error while getting access token from Spotify! Exception: {}", e.getMessage());
            throw new RuntimeException("Error while getting access token from Spotify! Exception: " + e.getMessage());
        }
    }

    private Optional<TrackSearchRequest> searchMusic(String musicName, PageDTO pagination) {
        try {
            authorize();

            TrackSearchRequest trackSearchRequest = api.searchTracks(musicName).offset(pagination.getPage()).limit(pagination.getSize()).build();
            return Optional.of(trackSearchRequest);

        } catch (Exception e) {
            log.error("Error while searching music by name from Spotify! Exception: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<Track> getTrack(String id) {
        try {
            authorize();

            Track track = api.getTrack(id).build().get();
            return Optional.of(track);

        } catch (Exception e) {
            log.warn("Error while getting track by ID! Exception: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<SpotifyMusicDTO> mapTracks(TrackSearchRequest request) {

        try {

            List<SpotifyDataDTO> tracks = request.get().getItems().stream()
                    .map(this::map)
                    .toList();

            SpotifyRootTrack spotifyRootTrack = new SpotifyRootTrack();
            spotifyRootTrack.setItems(tracks);
            spotifyRootTrack.setTotalCount(tracks.size());

            SpotifyMusicDTO spotifyMusicDTO = new SpotifyMusicDTO();
            spotifyMusicDTO.setTracks(spotifyRootTrack);

            return Optional.of(spotifyMusicDTO);

        } catch (IOException | WebApiException e) {
            log.warn("Error while mapping tracks to SpotifyMusicDTO! Exception: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private SpotifyDataDTO map(Track track) {
        SpotifyTrack spotifyTrack = new SpotifyTrack();

        spotifyTrack.setId(track.getId());
        spotifyTrack.setName(track.getName());
        spotifyTrack.setUri(track.getUri());
        spotifyTrack.setDuration((long) track.getDuration());
        spotifyTrack.setDownloadUri(buildUri(track.getHref()));

        return new SpotifyDataDTO(spotifyTrack);
    }

    private URI buildUri(String uriAsString) {
        try {
            return new URI(uriAsString);
        } catch (URISyntaxException e) {
            log.warn("Invalid String URI! Exception: {}", e.getMessage());
            return null;
        }
    }

    private Optional<InputStream> try2DownloadMusic(String token, Track track) throws IOException {

        if (StringUtils.isBlank(token) || Objects.isNull(track)) {
            log.warn("Invalid argument is passed! Token or Track must not be null!");
            return Optional.empty();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<?> requestBody = new HttpEntity<>(headers);

        ResponseEntity<Resource> response = restTemplate.exchange(track.getPreviewUrl(), HttpMethod.GET, requestBody, Resource.class);
        if (response.getStatusCode().isError() || Objects.isNull(response.getBody())) {
            log.warn("Error while downloading music from Spotify! Response: {}", response);
            return Optional.empty();
        }

        return Optional.of(response.getBody().getInputStream());
    }
}
