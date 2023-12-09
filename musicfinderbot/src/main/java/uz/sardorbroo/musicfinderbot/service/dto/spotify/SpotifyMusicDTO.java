package uz.sardorbroo.musicfinderbot.service.dto.spotify;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpotifyMusicDTO {

    private String query;
    private SpotifyRootTrack tracks;
}
