package uz.sardorbroo.musicfinderbot.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.sardorbroo.musicfinderbot.enumeration.Command;

import java.util.Collection;
import java.util.Optional;

public interface CommandService {

    Optional<SendMessage> execute(Update update);

    boolean supported(Command command);

    Collection<Command> getSupportedCommands();
}
