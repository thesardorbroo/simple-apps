package uz.sardorbroo.musicfinderbot.service.dto.spotify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpotifyRootTrack {

    private long totalCount;

    private List<SpotifyDataDTO> items;
}
