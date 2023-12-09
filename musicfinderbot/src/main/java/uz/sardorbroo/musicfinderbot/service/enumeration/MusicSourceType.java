package uz.sardorbroo.musicfinderbot.service.enumeration;


import lombok.Getter;

@Getter
public enum MusicSourceType {
    TRACK("tracks"),
    ARTIST("artists"),
    TOP_HITS("top_hits");

    private final String name;

    MusicSourceType(String name) {
        this.name = name;
    }
}
