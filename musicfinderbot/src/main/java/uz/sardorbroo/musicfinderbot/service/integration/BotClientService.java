package uz.sardorbroo.musicfinderbot.service.integration;

import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Optional;

// Todo: Should remove when AbsSender.sendAudio(...) is works!
public interface BotClientService {

    Optional<Message> sendAudio(SendAudio audio);
}
