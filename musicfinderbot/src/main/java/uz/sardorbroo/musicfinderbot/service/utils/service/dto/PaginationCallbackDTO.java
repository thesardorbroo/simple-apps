package uz.sardorbroo.musicfinderbot.service.utils.service.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class PaginationCallbackDTO extends CallbackDataDTO {
    private int page;
    private String music;

    public PaginationCallbackDTO(String prefix, String music, int page) {
        super(prefix);
        this.setMusic(music);
        this.setPage(page);
    }
}
