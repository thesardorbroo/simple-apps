package uz.sardorbroo.musicfinderbot.service.utils;

import lombok.extern.slf4j.Slf4j;
import org.khasanof.context.FluentContextHolder;
import org.khasanof.custom.attributes.UpdateAttributes;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;
import java.util.Optional;
import java.util.UnknownFormatConversionException;

@Slf4j
public class AbsSenderUtils {
    private static final String ABS_SENDER_KEY = "ABS_SENDER";

    public static void setContext(AbsSender sender) {

        UpdateAttributes attributes = FluentContextHolder.getAttributes();

        attributes.setAttribute(ABS_SENDER_KEY, sender);
    }

    public static Optional<AbsSender> getAbsSender() {
        UpdateAttributes attributes = FluentContextHolder.getAttributes();

        try {

            AbsSender sender = (AbsSender) attributes.getAttribute(ABS_SENDER_KEY);
            return Optional.of(sender);
        } catch (Exception e) {
            log.warn("Error while getting abs sender from attributes! Exception: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public static void send(AbsSender sender, Object tgObject) {

        if (tgObject instanceof SendMessage sendMessage) {
            send(sender, sendMessage);
        } else if (tgObject instanceof EditMessageText editMessageText) {
            send(sender, editMessageText);
        } else if (tgObject instanceof AnswerCallbackQuery answerCallbackQuery) {
            send(sender, answerCallbackQuery);
        } else if (tgObject instanceof SendAudio sendAudio) {
            send(sender, sendAudio);
        } else {
            throw new UnknownFormatConversionException("Unknown type of telegram object!");
        }
    }

    public static void send(Object tgObject) {
        Optional<AbsSender> senderOptional = getAbsSender();
        if (senderOptional.isEmpty()) {
            throw new RuntimeException("AbsSender is not found from context!");
        }

        send(senderOptional.get(), tgObject);
    }

    private static <T extends Serializable, R extends BotApiMethod<T>> void send(AbsSender sender, R source) {
        try {
            sender.execute(source);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T extends Serializable, R extends BotApiMethod<T>> void send(AbsSender sender, SendAudio sendAudio) {
        try {
            sender.execute(sendAudio);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
