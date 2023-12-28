package uz.sardorbroo.musicfinderbot.service.builder.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import uz.sardorbroo.musicfinderbot.service.builder.ButtonBuilder;
import uz.sardorbroo.musicfinderbot.service.builder.MessageManager;
import uz.sardorbroo.musicfinderbot.service.builder.TextBuilder;
import uz.sardorbroo.musicfinderbot.service.dto.MusicDTO;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class PlainSendMessageManager implements MessageManager {

    public static Builder builder(List<MusicDTO> musics, String chatId) {

        if (Objects.isNull(musics) || musics.isEmpty()) {
            throw new IllegalArgumentException("Invalid argument is passed! Musics collection must not be empty!");
        }

        return new Builder(musics, chatId);
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Builder {
        private final List<MusicDTO> musics;
        private final String chatId;

        private TextBuilder textBuilder;
        private ButtonBuilder buttonBuilder;

        private Builder(List<MusicDTO> musics, String chatId) {
            this.musics = musics;
            this.chatId = chatId;
        }

        public Optional<SendMessage> build() {

            Optional<String> textOptional = textBuilder.build(musics);
            if (textOptional.isEmpty()) {
                log.warn("Message text is not build!");
                return Optional.empty();
            }

            Optional<ReplyKeyboard> keyboardOptional = buttonBuilder.build(musics);
            if (keyboardOptional.isEmpty()) {
                log.warn("Buttons of keyboard are not build!");
                return Optional.empty();
            }

            SendMessage message = new SendMessage();
            message.setText(textOptional.get());
            message.setReplyMarkup(keyboardOptional.get());
            message.setChatId(chatId);

            return Optional.of(message);
        }
    }
}
