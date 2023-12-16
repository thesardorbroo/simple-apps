package uz.sardorbroo.musicfinderbot.service.utils.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import uz.sardorbroo.musicfinderbot.service.utils.service.UserExtractor;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@Slf4j
public class MessageUserExtractor implements UserExtractor {
    private final Predicate<Update> constraint = this::isMessageUserNull;

    @Override
    public boolean supported(Update update) {
        return constraint.test(update);
    }

    @Override
    public Optional<User> extract(Update update) {
        return Optional.of(update.getMessage().getFrom());
    }

    private boolean isMessageUserNull(Update update) {
        return Objects.nonNull(update) && Objects.nonNull(update.getMessage()) && Objects.nonNull(update.getMessage().getFrom());
    }
}
