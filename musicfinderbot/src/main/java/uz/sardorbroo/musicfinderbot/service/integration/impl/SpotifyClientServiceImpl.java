package uz.sardorbroo.musicfinderbot.service.integration.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.UriBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import uz.sardorbroo.musicfinderbot.service.dto.PageDTO;
import uz.sardorbroo.musicfinderbot.service.dto.spotify.SpotifyMusicDTO;
import uz.sardorbroo.musicfinderbot.service.dto.spotify.SpotifyTrackWithDownloadUri;
import uz.sardorbroo.musicfinderbot.service.enumeration.MusicSourceType;
import uz.sardorbroo.musicfinderbot.service.integration.SpotifyClientService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "music.service.spotify", name = "simulate", havingValue = "false", matchIfMissing = true)
public class SpotifyClientServiceImpl implements SpotifyClientService {
    private static final String SEARCH_API = "/search/";
    private static final String TRACK_API = "/tracks/";
    private static final String RAPID_API_KEY_HEADER_NAME = "X-RapidAPI-Key";
    private static final String RAPID_API_HOST_HEADER_NAME = "X-RapidAPI-Host";

    @Value("${music.service.credentials.secret:no secret}")
    private String secret;

    @Value("${music.service.spotify.url}")
    private URI uri;

    private final ObjectMapper mapper;

    public SpotifyClientServiceImpl(ObjectMapper mapper) {
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
    public byte[] download(String musicId) {

        if (StringUtils.isBlank(musicId)) {
            log.warn("Invalid argument is passed! MusicID must not be empty!");
            return new byte[0];
        }

        URI getDownloadUri = UriBuilder.fromUri(uri)
                .path(TRACK_API)
                .queryParam("ids", musicId)
                .build();

        HttpRequest request = buildRequest(getDownloadUri, HttpMethod.GET).build();

        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            Optional<SpotifyTrackWithDownloadUri> trackDataOptional = convert(response.body(), true);
            if (trackDataOptional.isEmpty() || trackDataOptional.get().getTracks().isEmpty()) return new byte[0];

            HttpRequest.Builder downloadRequestBuilder = buildRequest(trackDataOptional.get().getTracks().stream().findFirst().get().getDownloadUri(), HttpMethod.GET);
            downloadRequestBuilder.setHeader("Content-Type", "audio/mpeg");

            HttpRequest downloadRequest = downloadRequestBuilder.build();
            return HttpClient.newHttpClient().send(downloadRequest, HttpResponse.BodyHandlers.ofByteArray()).body();
        } catch (Exception e) {
            log.warn("Error while downloading music from Spotify! Exception: {}", e.getMessage());
            return new byte[0];
        }
    }

    private HttpRequest.Builder buildRequest(URI uri, HttpMethod method) {
        return HttpRequest.newBuilder()
                .uri(uri)
                .header(RAPID_API_KEY_HEADER_NAME, secret)
                .header(RAPID_API_HOST_HEADER_NAME, uri.getHost())
                .method(method.name(), HttpRequest.BodyPublishers.noBody());
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
