package uz.sardorbroo.musicfinderbot.service.integration;

import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.objects.Message;
import uz.sardorbroo.musicfinderbot.enumeration.Command;

import java.util.List;
import java.util.Optional;

public interface BotClientService {

    Optional<Message> sendAudio(SendAudio audio);

    boolean deleteCommands();

    boolean setCommands(List<Command> commands);
}
