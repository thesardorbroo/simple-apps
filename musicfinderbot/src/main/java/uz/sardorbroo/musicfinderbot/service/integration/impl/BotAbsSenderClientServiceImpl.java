package uz.sardorbroo.musicfinderbot.service.integration.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.commands.DeleteMyCommands;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import uz.sardorbroo.musicfinderbot.enumeration.Command;
import uz.sardorbroo.musicfinderbot.enumeration.Language;
import uz.sardorbroo.musicfinderbot.service.integration.BotClientService;
import uz.sardorbroo.musicfinderbot.service.utils.AbsSenderUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class BotAbsSenderClientServiceImpl implements BotClientService {
    private static final Language DEFAULT_LANGUAGE = Language.RU;

    @Override
    public Optional<Message> sendAudio(SendAudio audio) {
        log.debug("Start sending audio with AbsSender");

        if (Objects.isNull(audio)) {
            log.warn("Invalid argument is passed! SendAudio must not be null!");
            return Optional.empty();
        }

        AbsSenderUtils.send(audio);
        log.debug("Audio is send successfully");
        return Optional.empty();
    }

    @Override
    public boolean deleteCommands() {
        log.debug("Start deleting commands with AbsSender");

        DeleteMyCommands deleteMyCommands = new DeleteMyCommands();

        AbsSenderUtils.send(deleteMyCommands);

        log.debug("All commands are removed successfully");
        return true;
    }

    @Override
    public boolean setCommands(List<Command> commands) {
        log.debug("Start setting selected commands with AbsSender");

        if (Objects.isNull(commands) || commands.isEmpty()) {
            log.warn("Invalid argument is passed! Commands list must not be empty!");
            return false;
        }

        SetMyCommands botCommands = new SetMyCommands();
        botCommands.setCommands(resolve(commands));

        AbsSenderUtils.send(botCommands);
        log.debug("Selected commands are set successfully");
        return true;
    }

    private List<BotCommand> resolve(List<Command> commands) {
        return commands.stream()
                .map(command -> command.map(DEFAULT_LANGUAGE))
                .toList();
    }
}
