package uz.sardorbroo.musicfinderbot.service.utils;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@Slf4j
public class UserUtils {

    private static final List<Predicate<Update>> USER_CONSTRAINTS = List.of(
            (Objects::isNull),
            (update -> Objects.isNull(update.getMessage())),
            (update -> Objects.isNull(update.getMessage().getFrom()))
    );

    public static Optional<User> extractUser(Update update) {

        if (isUpdateInvalid(update)) {
            log.warn("Invalid argument is passed! Cannot extract user!");
            return Optional.empty();
        }

        return Optional.of(update.getMessage().getFrom());
    }

    public static User extractUserOrThrow(Update update) {
        return extractUser(update)
                .orElseThrow(() -> new IllegalArgumentException("Cannot extract user from Update!"));
    }

    private static boolean isUpdateInvalid(Update update) {
        return !isUpdateValid(update);
    }

    private static boolean isUpdateValid(Update update) {

        return USER_CONSTRAINTS.stream()
                .filter(constraint -> constraint.test(update))
                .toList()
                .isEmpty();
    }
}
