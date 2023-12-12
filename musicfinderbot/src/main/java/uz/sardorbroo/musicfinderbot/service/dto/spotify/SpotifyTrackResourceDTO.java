package uz.sardorbroo.musicfinderbot.service.dto.spotify;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.InputStream;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SpotifyTrackResourceDTO extends SpotifyTrack {

    private InputStream inputStream;

}
