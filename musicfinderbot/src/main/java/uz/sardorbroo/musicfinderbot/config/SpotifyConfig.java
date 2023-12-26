package uz.sardorbroo.musicfinderbot.config;

import com.wrapper.spotify.Api;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpotifyConfig {

    @Value("${music.service.spotify.credentials.clientId}")
    private String clientId;

    @Value("${music.service.spotify.credentials.clientSecret}")
    private String clientSecret;

    @Value("${music.service.spotify.redirectUri:http://localhost:8080}")
    private String redirectUri;

    @Bean("REAL")
    public Api initializeSpotifyConfig() {
        return new Api.Builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .redirectURI(redirectUri)
                .build();
    }

    @Bean("DEFAULT")
    public Api initializeDefaultSpotifyConfig() {
        return Api.DEFAULT_API;
    }
}
