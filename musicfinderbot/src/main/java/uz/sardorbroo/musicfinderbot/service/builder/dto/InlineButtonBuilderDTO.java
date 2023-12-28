package uz.sardorbroo.musicfinderbot.service.builder.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import uz.sardorbroo.musicfinderbot.service.dto.PageDTO;

@Getter
@Setter
@Accessors(chain = true)
public class InlineButtonBuilderDTO extends ButtonBuilderDTO {
    private PageDTO pagination;
    private String musicName;
}
