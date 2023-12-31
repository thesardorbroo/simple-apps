package uz.sardorbroo.musicfinderbot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import uz.sardorbroo.musicfinderbot.config.properties.FluentProperties;
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

    private final FluentProperties properties;
    private final Boolean enable;

    public StartHelpCommandServiceImpl(FluentProperties properties) {
        this.properties = properties;
        this.enable = isCommandServiceEnable();
    }

    @Override
    public boolean supported(Command command) {
        return Objects.nonNull(command) && SUPPORTED_COMMANDS.contains(command);
    }

    @Override
    public boolean isEnable() {
        return this.enable;
    }

    @Override
    public Optional<SendMessage> execute(Update update) {
        log.debug("User send '/start' command!");

        User user = UserUtils.extractUserOrThrow(update);

        String message = buildMessage(user.getLanguageCode());

        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(message);
        sendMessage.setChatId(user.getId());

        return Optional.of(sendMessage);
    }

    private String buildMessage(String languageCode) {

        if (StringUtils.isBlank(languageCode)) {
            log.warn("Invalid argument is passed! LanguageCode must not be empty!");
            throw new IllegalArgumentException("Invalid argument is passed! LanguageCode must not be empty!");
        }

        ResourceBundle bundle = ResourceBundleUtils.getBundle(languageCode);

        StringBuilder builder = new StringBuilder(bundle.getString(KEY_4_MESSAGE) + "\n\n");

        properties.getCommands().stream()
                .map(command -> String.format("%s - %s\n", command.getText(), bundle.getString(command.getKey4Description())))
                .forEach(builder::append);

        return builder.toString();
    }

    private boolean isCommandServiceEnable() {
        return properties.getCommands().stream()
                .anyMatch(SUPPORTED_COMMANDS::contains);
    }
}
