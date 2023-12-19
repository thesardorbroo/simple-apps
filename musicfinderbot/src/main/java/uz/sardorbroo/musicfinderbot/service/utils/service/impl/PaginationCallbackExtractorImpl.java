package uz.sardorbroo.musicfinderbot.service.utils.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import uz.sardorbroo.musicfinderbot.config.constants.CallbackPrefix;
import uz.sardorbroo.musicfinderbot.service.utils.service.CallbackDataExtractor;
import uz.sardorbroo.musicfinderbot.service.utils.service.dto.CallbackDataDTO;
import uz.sardorbroo.musicfinderbot.service.utils.service.dto.PaginationCallbackDTO;

import java.util.Objects;
import java.util.Optional;

@Slf4j
public class PaginationCallbackExtractorImpl implements CallbackDataExtractor {
    private static final String DELIMITER = CallbackPrefix.DELIMITER;
    private static final String SUPPORTED_PREFIX = CallbackPrefix.PAGE_PREFIX;
    private static final Integer SPLINTED_STRINGS_LENGTH = 3;

    @Override
    public boolean supported(String text) {
        return StringUtils.isNotBlank(text) && text.contains(SUPPORTED_PREFIX);
    }

    @Override
    public Optional<CallbackDataDTO> extract(String text) {
        log.debug("Extract pagination callback data from text");

        if (StringUtils.isBlank(text)) {
            log.warn("Invalid argument is passed! Text must not be null!");
            return Optional.empty();
        }

        String[] data = text.split(DELIMITER);
        if (!Objects.equals(SPLINTED_STRINGS_LENGTH, data.length)) {
            log.warn("Invalid callback data!");
            return Optional.empty();
        }

        Integer page = convert(data[1]);
        String musicName = data[2];
        PaginationCallbackDTO paginationCallback = new PaginationCallbackDTO(SUPPORTED_PREFIX, musicName, page);
        return Optional.of(paginationCallback);
    }

    private Integer convert(String number) {

        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
