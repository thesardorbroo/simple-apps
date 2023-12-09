package uz.sardorbroo.musicfinderbot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import uz.sardorbroo.musicfinderbot.enumeration.Command;
import uz.sardorbroo.musicfinderbot.service.CommandService;
import uz.sardorbroo.musicfinderbot.service.utils.ResourceBundleUtils;
import uz.sardorbroo.musicfinderbot.service.utils.UserUtils;

import java.util.*;

@Slf4j
@Service
public class StartHelpCommandServiceImpl implements CommandService {

    private static final String KEY_4_MESSAGE = "start.text";
    private static final List<Command> SUPPORTED_COMMANDS = List.of(Command.START, Command.HELP);

    @Override
    public boolean supported(Command command) {
        return Objects.nonNull(command) && SUPPORTED_COMMANDS.contains(command);
    }

    @Override
    public Collection<Command> getSupportedCommands() {
        return SUPPORTED_COMMANDS;
    }

    @Override
    public Optional<SendMessage> execute(Update update) {
        log.debug("User send '/start' command!");

        User user = UserUtils.extractUserOrThrow(update);

        // Todo: should fix locale.
        String message = buildMessage(Locale.ENGLISH);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(message);
        sendMessage.setChatId(user.getId());

        return Optional.of(sendMessage);
    }

    private String buildMessage(Locale locale) {

        if (Objects.isNull(locale)) {
            log.warn("Invalid argument is passed! Locale must not be null!");
            throw new IllegalArgumentException("Invalid argument is passed! Locale must not be null!");
        }

        ResourceBundle bundle = ResourceBundleUtils.getBundleOrError(locale);

        StringBuilder builder = new StringBuilder(bundle.getString(KEY_4_MESSAGE) + "\n\n");

        Arrays.stream(Command.values())
                .map(command -> String.format("%s - %s\n", command.getText(), bundle.getString(command.getKey4Description())))
                .forEach(builder::append);

        return builder.toString();
    }

}
