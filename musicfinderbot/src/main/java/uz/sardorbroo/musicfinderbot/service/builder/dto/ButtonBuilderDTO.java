package uz.sardorbroo.musicfinderbot.service.builder.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import uz.sardorbroo.musicfinderbot.service.dto.MusicDTO;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class ButtonBuilderDTO extends BuilderDTO {
    private List<MusicDTO> musics;
}
