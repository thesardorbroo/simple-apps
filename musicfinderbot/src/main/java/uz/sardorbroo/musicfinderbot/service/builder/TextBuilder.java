package uz.sardorbroo.musicfinderbot.service.builder;

import uz.sardorbroo.musicfinderbot.service.dto.MusicDTO;

import java.util.List;
import java.util.Optional;

public interface TextBuilder extends Builder {

    Optional<String> build(List<MusicDTO> musics);
}
