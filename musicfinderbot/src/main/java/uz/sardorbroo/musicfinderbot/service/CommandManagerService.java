package uz.sardorbroo.musicfinderbot.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

public interface CommandManagerService {

    Optional<SendMessage> command(Update update);

}
