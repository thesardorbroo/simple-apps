package uz.sardorbroo.musicfinderbot.service.utils.service.dto;

public class CallbackDataDTO {
    private final String prefix;

    public CallbackDataDTO(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return this.prefix;
    }
}
