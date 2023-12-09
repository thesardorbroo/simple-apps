package uz.sardorbroo.musicfinderbot.controller;

import lombok.extern.slf4j.Slf4j;
import org.khasanof.annotation.UpdateController;
import org.khasanof.annotation.methods.HandleMessage;
import org.khasanof.enums.MatchScope;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import uz.sardorbroo.musicfinderbot.service.CommandManagerService;
import uz.sardorbroo.musicfinderbot.service.utils.AbsSenderUtils;

import java.util.Optional;

@Slf4j
@UpdateController
public class CommandController {

    private final CommandManagerService commandManagerService;

    public CommandController(CommandManagerService commandManagerService) {
        this.commandManagerService = commandManagerService;
    }

    @HandleMessage(value = "/", scope = MatchScope.START_WITH)
    public void commandHandler(Update update, AbsSender sender) {
        log.debug("Command is send");
        Optional<SendMessage> messageOptional = commandManagerService.command(update);
        messageOptional.ifPresent(sendMessage -> AbsSenderUtils.send(sender, sendMessage));
        log.debug("Successfully answered!");
    }
}
