package uz.sardorbroo.musicfinderbot.service;

import uz.sardorbroo.musicfinderbot.service.dto.PageDTO;
import uz.sardorbroo.musicfinderbot.service.dto.spotify.SpotifyMusicDTO;
import uz.sardorbroo.musicfinderbot.service.enumeration.MusicSourceType;

import java.util.Optional;

public interface SpotifyMusicService {

    Optional<SpotifyMusicDTO> search(String name, MusicSourceType type, PageDTO pagination);

    byte[] download(String musicId);
}
