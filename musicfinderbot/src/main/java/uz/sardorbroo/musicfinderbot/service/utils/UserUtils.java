package uz.sardorbroo.musicfinderbot.service.utils;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import uz.sardorbroo.musicfinderbot.service.utils.service.UserExtractor;
import uz.sardorbroo.musicfinderbot.service.utils.service.impl.CallbackQueryUserExtractor;
import uz.sardorbroo.musicfinderbot.service.utils.service.impl.MessageUserExtractor;

import java.util.List;
import java.util.Optional;

@Slf4j
public class UserUtils {
    private static final List<UserExtractor> EXTRACTORS = List.of(new MessageUserExtractor(), new CallbackQueryUserExtractor());

    public static Optional<User> extractUser(Update update) {

        return EXTRACTORS.stream()
                .filter(extractor -> extractor.supported(update))
                .map(extractor -> extractor.extract(update))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    public static User extractUserOrThrow(Update update) {
        return extractUser(update).orElseThrow(() ->
                new IllegalArgumentException("Cannot extract user from Update!"));
    }
}
