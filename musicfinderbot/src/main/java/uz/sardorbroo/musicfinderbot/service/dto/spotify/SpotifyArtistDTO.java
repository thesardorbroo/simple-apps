package uz.sardorbroo.musicfinderbot.service.dto.spotify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpotifyArtistDTO {

    private String uri;

    private String name;

    @JsonProperty("profile")
    private void resolveName(Map<String, String> profile) {
        this.name = profile.getOrDefault("name", "");
    }
}
