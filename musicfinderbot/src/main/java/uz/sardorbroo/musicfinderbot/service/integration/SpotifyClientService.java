package uz.sardorbroo.musicfinderbot.service.integration;

import uz.sardorbroo.musicfinderbot.service.dto.PageDTO;
import uz.sardorbroo.musicfinderbot.service.dto.spotify.SpotifyMusicDTO;
import uz.sardorbroo.musicfinderbot.service.enumeration.MusicSourceType;

import java.util.Optional;

public interface SpotifyClientService {

    Optional<SpotifyMusicDTO> search(String name, MusicSourceType type, PageDTO pagination);

    byte[] download(String musicId);
}
