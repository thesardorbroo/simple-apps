package uz.sardorbroo.musicfinderbot.service.utils;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
public class AbsSenderUtils {

    public static void send(AbsSender sender, SendMessage message) {

        try {
            sender.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public static void send(AbsSender sender, SendAudio audio) {

        try {
            sender.execute(audio);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public static void send(AbsSender sender, EditMessageText editMessageText) {

        try {
            sender.execute(editMessageText);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
