package uz.sardorbroo.musicfinderbot.service.utils;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.sardorbroo.musicfinderbot.service.utils.service.CallbackDataExtractor;
import uz.sardorbroo.musicfinderbot.service.utils.service.dto.CallbackDataDTO;
import uz.sardorbroo.musicfinderbot.service.utils.service.dto.MusicCallbackDTO;
import uz.sardorbroo.musicfinderbot.service.utils.service.dto.PaginationCallbackDTO;
import uz.sardorbroo.musicfinderbot.service.utils.service.impl.MusicCallbackDataExtractorImpl;
import uz.sardorbroo.musicfinderbot.service.utils.service.impl.PaginationCallbackExtractorImpl;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@Slf4j
public class CallbackDataExtractorUtils {

    private static final List<CallbackDataExtractor> EXTRACTORS =
            List.of(new MusicCallbackDataExtractorImpl(), new PaginationCallbackExtractorImpl());

    private static final List<Predicate<Update>> CONSTRAINTS = getConstraints();

    public static Optional<CallbackDataDTO> extract(Update update) {
        log.debug("Extract callback data from Update");

        if (isUpdateInvalid(update)) {
            log.warn("Invalid argument is passed! Update.Callback must not be null!");
            return Optional.empty();
        }

        return extractWithSpecificExtractors(update);
    }

    public static Optional<MusicCallbackDTO> extractMusicCallback(Update update) {

        Optional<CallbackDataDTO> callbackDataOptional = extract(update);

        if (callbackDataOptional.isEmpty()) return Optional.empty();

        if (!(callbackDataOptional.get() instanceof MusicCallbackDTO)) return Optional.empty();

        return Optional.of((MusicCallbackDTO) callbackDataOptional.get());
    }

    public static Optional<PaginationCallbackDTO> extractPaginationCallback(Update update) {

        Optional<CallbackDataDTO> callbackDataOptional = extract(update);

        if (callbackDataOptional.isEmpty()) return Optional.empty();

        if (!(callbackDataOptional.get() instanceof PaginationCallbackDTO)) return Optional.empty();

        return Optional.of((PaginationCallbackDTO) callbackDataOptional.get());
    }

    private static Optional<CallbackDataDTO> extractWithSpecificExtractors(Update update) {
        String callbackData = update.getCallbackQuery().getData();

        return EXTRACTORS.stream()
                .filter(extractor -> extractor.supported(callbackData))
                .map(extractor -> extractor.extract(callbackData))
                .map(Optional::orElseThrow)
                .findAny();
    }

    private static boolean isUpdateInvalid(Update update) {

        return CONSTRAINTS.stream().anyMatch(constraint -> constraint.test(update));
    }

    private static List<Predicate<Update>> getConstraints() {
        List<Predicate<Update>> constraints = new LinkedList<>();

        constraints.add(isUpdateNull());
        constraints.add(isCallbackNull());
        constraints.add(isMessageNull());

        return constraints;
    }

    private static Predicate<Update> isUpdateNull() {
        return (Objects::isNull);
    }

    private static Predicate<Update> isCallbackNull() {
        return update -> Objects.isNull(update.getCallbackQuery());
    }

    private static Predicate<Update> isMessageNull() {
        return update -> Objects.isNull(update.getCallbackQuery().getMessage());
    }
}
