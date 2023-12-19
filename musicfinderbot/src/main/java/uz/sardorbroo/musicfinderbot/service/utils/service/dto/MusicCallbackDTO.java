package uz.sardorbroo.musicfinderbot.service.utils.service.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class MusicCallbackDTO extends CallbackDataDTO {
    private String musicId;

    public MusicCallbackDTO(String prefix, String musicId) {
        super(prefix);
        this.setMusicId(musicId);
    }
}
