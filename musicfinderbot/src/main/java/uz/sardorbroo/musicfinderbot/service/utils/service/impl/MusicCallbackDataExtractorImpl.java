package uz.sardorbroo.musicfinderbot.service.utils.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import uz.sardorbroo.musicfinderbot.config.constants.CallbackPrefix;
import uz.sardorbroo.musicfinderbot.service.utils.service.CallbackDataExtractor;
import uz.sardorbroo.musicfinderbot.service.utils.service.dto.CallbackDataDTO;
import uz.sardorbroo.musicfinderbot.service.utils.service.dto.MusicCallbackDTO;

import java.util.Objects;
import java.util.Optional;

@Slf4j
public class MusicCallbackDataExtractorImpl implements CallbackDataExtractor {
    private static final String DELIMITER = CallbackPrefix.DELIMITER;
    private static final Integer SPLINTED_STRINGS_LENGTH = 2;
    private static final String SUPPORTED_PREFIX = CallbackPrefix.MUSIC_ID_PREFIX;

    @Override
    public boolean supported(String text) {
        return StringUtils.isNotBlank(text) && text.contains(SUPPORTED_PREFIX);
    }

    @Override
    public Optional<CallbackDataDTO> extract(String text) {
        log.debug("Extracting music callback data from text");

        if (StringUtils.isBlank(text)) {
            log.warn("Invalid argument is passed! Text must not be null!");
            return Optional.empty();
        }

        String[] data = text.split(DELIMITER);
        if (Objects.equals(SPLINTED_STRINGS_LENGTH, data.length)) {
            log.warn("Invalid callback data!");
            return Optional.empty();
        }

        String musicName = data[1];
        MusicCallbackDTO musicCallback = new MusicCallbackDTO(SUPPORTED_PREFIX, musicName);
        return Optional.of(musicCallback);
    }
}
