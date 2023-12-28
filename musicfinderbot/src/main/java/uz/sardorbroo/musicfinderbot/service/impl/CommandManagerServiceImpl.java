package uz.sardorbroo.musicfinderbot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.sardorbroo.musicfinderbot.enumeration.Command;
import uz.sardorbroo.musicfinderbot.service.CommandManagerService;
import uz.sardorbroo.musicfinderbot.service.CommandService;
import uz.sardorbroo.musicfinderbot.service.single.UserContextHolder;
import uz.sardorbroo.musicfinderbot.service.utils.ResourceBundleUtils;

import java.util.*;
import java.util.function.Predicate;

@Slf4j
@Service
public class CommandManagerServiceImpl implements CommandManagerService {
    private static final String UNKNOWN_COMMAND_KEY = "command.unknown.description";
    private static final List<Predicate<Update>> USER_CONSTRAINTS = List.of(
            (Objects::isNull),
            (update -> Objects.isNull(update.getMessage())),
            (update -> Objects.isNull(update.getMessage().getFrom()))
    );

    private final UserContextHolder userContext;
    private final List<CommandService> commandServices;

    public CommandManagerServiceImpl(UserContextHolder userContext,
                                     List<CommandService> commandServices) {
        this.userContext = userContext;
        this.commandServices = commandServices;
    }

    public Optional<SendMessage> command(Update update) {
        log.debug("Start managing command and answering to the user");

        if (isUpdateValid(update)) {
            log.warn("Invalid argument is passed! Update.Message.From must not be null!");
            return Optional.empty();
        }

        Optional<Command> commandOptional = resolve(update.getMessage().getText());
        if (commandOptional.isEmpty()) {
            log.warn("Unknown command is send! Command: {}", update.getMessage().getText());
            return Optional.of(wrongCommand(update.getMessage().getFrom().getId()));
        }

        return commandServices.stream()
                .filter(CommandService::isEnable)
                .filter(commandService -> commandService.supported(commandOptional.get()))
                .map(commandService -> commandService.execute(update))
                .findFirst()
                .orElse(Optional.empty());
    }

    private Optional<Command> resolve(String text) {

        return Arrays.stream(Command.values())
                .filter(command -> Objects.equals(command.getText(), text) || text.startsWith(command.getText()))
                .findAny();
    }

    private SendMessage wrongCommand(Long chatId) {

        if (Objects.isNull(chatId) || chatId <= 0) {
            log.warn("Invalid argument is passed! ChatID must not be null!");
            throw new IllegalArgumentException("Invalid argument is passed! ChatID must not be null!");
        }

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(unknownCommandMessage());

        return sendMessage;
    }

    private String unknownCommandMessage() {

        ResourceBundle bundle = ResourceBundleUtils.getBundleOrError(Locale.ENGLISH);

        return bundle.getString(UNKNOWN_COMMAND_KEY);
    }

    private boolean isUpdateValid(Update update) {

        return !USER_CONSTRAINTS.stream()
                .filter(constraint -> constraint.test(update))
                .toList()
                .isEmpty();
    }
}
