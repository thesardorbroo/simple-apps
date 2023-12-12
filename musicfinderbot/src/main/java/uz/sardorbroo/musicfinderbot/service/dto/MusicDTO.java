package uz.sardorbroo.musicfinderbot.service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Objects;

@Data
@Accessors(chain = true)
public class MusicDTO {

    private String id;

    private String title;

    private String artist;

    private String url2Download;

    private long duration;

    public MusicDTO setDuration(Long duration) {

        if (Objects.nonNull(duration)) {
            this.duration = duration;
        }

        return this;
    }
}
