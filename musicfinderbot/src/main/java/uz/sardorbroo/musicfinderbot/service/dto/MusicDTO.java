package uz.sardorbroo.musicfinderbot.service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MusicDTO {

    private String id;

    private String title;

    private String artist;

    private String url2Download;

    private long duration;
}
