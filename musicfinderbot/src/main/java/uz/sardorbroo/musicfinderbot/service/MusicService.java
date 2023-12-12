package uz.sardorbroo.musicfinderbot.service;

import uz.sardorbroo.musicfinderbot.service.dto.MusicDTO;
import uz.sardorbroo.musicfinderbot.service.dto.MusicResourceDTO;
import uz.sardorbroo.musicfinderbot.service.dto.PageDTO;

import java.util.List;
import java.util.Optional;

public interface MusicService {

    List<MusicDTO> find(String musicName, PageDTO pagination);

    Optional<MusicResourceDTO> download(String musicId);
}
