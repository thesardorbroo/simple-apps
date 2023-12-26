package uz.sardorbroo.musicfinderbot.service.integration.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.UriBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uz.sardorbroo.musicfinderbot.service.dto.PageDTO;
import uz.sardorbroo.musicfinderbot.service.dto.spotify.SpotifyMusicDTO;
import uz.sardorbroo.musicfinderbot.service.dto.spotify.SpotifyTrackWithDownloadUri;
import uz.sardorbroo.musicfinderbot.service.enumeration.MusicSourceType;
import uz.sardorbroo.musicfinderbot.service.integration.SpotifyClientService;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "music.service.spotify", name = "service", havingValue = "rapid-api")
public class SpotifyRapidApiClientServiceImpl implements SpotifyClientService {
    private static final String SEARCH_API = "/search/";
    private static final String TRACK_API = "/tracks/";
    private static final String RAPID_API_KEY_HEADER_NAME = "X-RapidAPI-Key";
    private static final String RAPID_API_HOST_HEADER_NAME = "X-RapidAPI-Host";

    @Value("${music.service.credentials.secret:no secret}")
    private String secret;

    @Value("${music.service.spotify.url}")
    private URI uri;

    private final ObjectMapper mapper;

    public SpotifyRapidApiClientServiceImpl(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<SpotifyMusicDTO> search(String name, MusicSourceType type, PageDTO page) {
        log.debug("Requesting to get music. Name: {} | Type: {}", name, type);

        URI searchUri = UriBuilder.fromUri(uri)
                .path(SEARCH_API)
                .queryParam("q", name)
                .queryParam("type", type.getName())
                .queryParam("offset", page.getPage())
                .queryParam("limit", page.getSize())
                .build();

        log.debug("URI is build! URI: {}", searchUri.toString());

        HttpRequest request = buildRequest(searchUri, HttpMethod.GET).build();

        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            return convert(response.body());
        } catch (Exception e) {
            log.error("Error while searching musics from Spotify! Exception: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<InputStream> download(String musicId) {

        if (StringUtils.isBlank(musicId)) {
            log.warn("Invalid argument is passed! MusicID must not be empty!");
            return Optional.empty();
        }

        Optional<SpotifyTrackWithDownloadUri> trackDataOptional = getTrackDataById(musicId);
        if (trackDataOptional.isEmpty() || trackDataOptional.get().getTracks().isEmpty()) return Optional.empty();

        Optional<URI> musicDownloadUriOptional = extractDownloadUri(trackDataOptional.get());
        if (musicDownloadUriOptional.isEmpty()) {
            log.warn("Music download URL is not found!");
            return Optional.empty();
        }

        HttpHeaders headers = buildHeaders(musicDownloadUriOptional.get().getHost());
        headers.add("Content-Type", "application/mpeg");

        HttpEntity<?> requestBody = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplateBuilder()
                .setReadTimeout(Duration.ofMinutes(2))
                .setConnectTimeout(Duration.ofMinutes(2))
                .build();
        try {

            ResponseEntity<Resource> response = restTemplate.exchange(musicDownloadUriOptional.get(), HttpMethod.GET, requestBody, Resource.class);
            if (Objects.isNull(response.getBody()) || response.getStatusCode().isError()) {
                log.warn("Error while downloading music from Spotify! Response: {}", response);
                return Optional.empty();
            }

            InputStream inputStream = response.getBody().getInputStream();

            return Optional.of(inputStream);
        } catch (Exception e) {
            log.warn("Error while downloading music from Spotify! Exception: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<SpotifyTrackWithDownloadUri> getTrackDataById(String musicId) {
        log.debug("Get music by ID from Spotify. ID: {}", musicId);

        if (StringUtils.isBlank(musicId)) {
            log.warn("Invalid argument is passed! MusicID must not be blank!");
            return Optional.empty();
        }

        URI musicDataUri = UriBuilder.fromUri(uri)
                .path(TRACK_API)
                .queryParam("ids", musicId)
                .build();

        HttpHeaders headers = buildHeaders(uri.getHost());
        HttpEntity<?> requestBody = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<SpotifyTrackWithDownloadUri> response = restTemplate.exchange(musicDataUri, HttpMethod.GET, requestBody, SpotifyTrackWithDownloadUri.class);
            if (response.getStatusCode().isError()) {
                log.warn("Error while getting music data from Spotify by ID! Response: {}", response);
                return Optional.empty();
            }

            return Optional.ofNullable(response.getBody());
        } catch (Exception e) {
            log.warn("Error while getting music data from Spotify by ID: Exception: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private HttpRequest.Builder buildRequest(URI uri, HttpMethod method) {
        return HttpRequest.newBuilder()
                .uri(uri)
                .header(RAPID_API_KEY_HEADER_NAME, secret)
                .header(RAPID_API_HOST_HEADER_NAME, uri.getHost())
                .method(method.name(), HttpRequest.BodyPublishers.noBody());
    }

    private HttpHeaders buildHeaders(String host) {
        HttpHeaders headers = new HttpHeaders();

        headers.add(RAPID_API_KEY_HEADER_NAME, secret);
        headers.add(RAPID_API_HOST_HEADER_NAME, host);

        return headers;
    }

    private Optional<URI> extractDownloadUri(SpotifyTrackWithDownloadUri spotifyTracks) {

        if (Objects.isNull(spotifyTracks) || Objects.isNull(spotifyTracks.getTracks())) {
            log.warn("Invalid argument is passed! SpotifyTrack to must not be null!");
            return Optional.empty();
        }

        return spotifyTracks.getTracks().stream()
                .map(SpotifyTrackWithDownloadUri.SpotifyTracksWithDownloadUri::getDownloadUri)
                .findFirst();
    }

    private Optional<SpotifyTrackWithDownloadUri> convert(String trackDataAsString, boolean download) {
        try {
            SpotifyTrackWithDownloadUri spotifyMusic = mapper.readValue(trackDataAsString, new TypeReference<SpotifyTrackWithDownloadUri>() {
            });
            return Optional.of(spotifyMusic);

        } catch (JsonProcessingException e) {
            log.warn("Error while converting response to SpotifyMusicDTO! Exception: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<SpotifyMusicDTO> convert(String spotifyMusicsAsString) {

        try {
            SpotifyMusicDTO spotifyMusic = mapper.readValue(spotifyMusicsAsString, new TypeReference<SpotifyMusicDTO>() {
            });
            return Optional.of(spotifyMusic);

        } catch (JsonProcessingException e) {
            log.warn("Error while converting response to SpotifyMusicDTO! Exception: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
