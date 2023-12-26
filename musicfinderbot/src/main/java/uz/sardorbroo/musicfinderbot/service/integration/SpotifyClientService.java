package uz.sardorbroo.musicfinderbot.service.integration;

import uz.sardorbroo.musicfinderbot.service.dto.PageDTO;
import uz.sardorbroo.musicfinderbot.service.dto.spotify.SpotifyMusicDTO;
import uz.sardorbroo.musicfinderbot.service.enumeration.MusicSourceType;

import java.io.InputStream;
import java.util.Optional;

public interface SpotifyClientService {

    Optional<SpotifyMusicDTO> search(String name, MusicSourceType type, PageDTO pagination);

    Optional<InputStream> download(String musicId);
}
