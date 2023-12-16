package uz.sardorbroo.musicfinderbot.service.utils.service;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;

public interface UserExtractor {

    boolean supported(Update update);

    Optional<User> extract(Update update);
}
