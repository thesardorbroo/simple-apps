package uz.sardorbroo.musicfinderbot.service.utils.service;

import uz.sardorbroo.musicfinderbot.service.utils.service.dto.CallbackDataDTO;

import java.util.Optional;

public interface CallbackDataExtractor {

    boolean supported(String prefix);

    Optional<CallbackDataDTO> extract(String text);
}
