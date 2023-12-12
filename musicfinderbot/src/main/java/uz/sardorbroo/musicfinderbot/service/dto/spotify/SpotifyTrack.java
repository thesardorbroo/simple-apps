package uz.sardorbroo.musicfinderbot.service.dto.spotify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpotifyTrack {

    @JsonProperty("id")
    private String id;

    @JsonProperty("uri")
    private String uri;

    @JsonProperty("name")
    private String name;

    private Long duration;

    private List<SpotifyArtistDTO> artists;

    @JsonIgnoreProperties(value = "preview_url", ignoreUnknown = true)
    private URI downloadUri;

    @JsonProperty("artists")
    private void resolveArtists(Map<String, List<SpotifyArtistDTO>> artists) {
        this.artists = artists.getOrDefault("items", Collections.emptyList());
    }

    @JsonProperty("duration")
    private void resolveDuration(Map<String, Long> duration) {
        this.duration = duration.getOrDefault("totalMilliseconds", 0L);
    }

    public String joinArtists() {
        StringBuilder artistsNames = new StringBuilder();

        if (Objects.isNull(artists)) return "";

        artists.stream()
                .map(SpotifyArtistDTO::getName)
                .forEach(name -> artistsNames.append(name).append(" "));

        return artistsNames.toString();
    }
}