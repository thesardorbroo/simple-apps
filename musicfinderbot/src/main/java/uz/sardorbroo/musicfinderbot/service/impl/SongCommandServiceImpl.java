package uz.sardorbroo.musicfinderbot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.sardorbroo.musicfinderbot.enumeration.Command;
import uz.sardorbroo.musicfinderbot.service.CommandService;
import uz.sardorbroo.musicfinderbot.service.MusicService;
import uz.sardorbroo.musicfinderbot.service.dto.MusicDTO;
import uz.sardorbroo.musicfinderbot.service.dto.PageDTO;
import uz.sardorbroo.musicfinderbot.service.utils.ResourceBundleUtils;
import uz.sardorbroo.musicfinderbot.service.utils.SendMessageUtils;
import uz.sardorbroo.musicfinderbot.service.utils.UserUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class SongCommandServiceImpl implements CommandService {
    private static final String PREVIOUS_BUTTON_TEXT = "⬅️";
    private static final String CANCEL_BUTTON_TEXT = "❌";
    private static final String CANCEL_BUTTON_CALLBACK = "CANCEL";
    private static final String NEXT_BUTTON_TEXT = "➡️";

    private static final String SPLITERATOR_PREFIX = " ";
    private static final String MUSIC_NOT_FOUND_KEY = "music.not.found";
    private static final Command SUPPORTED_COMMAND = Command.SONG;

    private final MusicService musicService;

    public SongCommandServiceImpl(MusicService musicService) {
        this.musicService = musicService;
    }

    @Override
    public Optional<SendMessage> execute(Update update) {
        log.debug("Song command is send");

        User user = UserUtils.extractUserOrThrow(update);

        String message = update.getMessage().getText();
        if (isMessageInvalid(message)) {
            log.warn("Invalid argument is passed! Text must contain more words!");
            return Optional.of(SendMessageUtils.wrongValue(user));
        }

        StringBuilder musicName = new StringBuilder();
        Arrays.stream(split(message))
                .filter(pieceOfMessage -> !Objects.equals(SUPPORTED_COMMAND.getText(), pieceOfMessage))
                .forEach(pieceOfMessage -> musicName.append(pieceOfMessage).append(" "));

        List<MusicDTO> musics = musicService.find(musicName.toString(), new PageDTO(0, 10));
        if (musics.isEmpty()) {
            log.debug("Musics are not found! Music name: {}", musicName);
            return Optional.of(musicNotFound(user));
        }

        return Optional.of(buildMessage(user, musics));
    }

    @Override
    public boolean supported(Command command) {
        return Objects.nonNull(command) && Objects.equals(SUPPORTED_COMMAND, command);
    }

    private boolean isMessageInvalid(String message) {
        return !isMessageValid(message);
    }

    private String[] split(String message) {
        return message.split(SPLITERATOR_PREFIX);
    }

    private boolean isMessageValid(String message) {

        if (StringUtils.isBlank(message)) {
            log.warn("Invalid argument is passed! Text must not be empty!");
            return false;
        }

        return split(message).length > 1;
    }

    private SendMessage musicNotFound(User user) {

        ResourceBundle bundle = ResourceBundleUtils.getBundle(user.getLanguageCode());

        String text = bundle.getString(MUSIC_NOT_FOUND_KEY);

        SendMessage message = new SendMessage();
        message.setChatId(user.getId());
        message.setText(text);

        return message;
    }

    private SendMessage buildMessage(User user, List<MusicDTO> musics) {

        String text = buildMessageText(musics);

        InlineKeyboardMarkup inlineMarkup = (InlineKeyboardMarkup) buildButtons(musics);

        SendMessage message = new SendMessage();
        message.setChatId(user.getId());
        message.setText(text);
        message.setReplyMarkup(inlineMarkup);

        return message;
    }

    private String buildMessageText(List<MusicDTO> musics) {

        AtomicInteger counter = new AtomicInteger(1);

        StringBuilder text = new StringBuilder();

        musics.forEach(music -> {
            String line = String.format("%d. %s - %s | %s\n", counter.getAndIncrement(), music.getArtist(), music.getTitle(), String.valueOf(music.getDuration()));
            text.append(line);
        });

        return text.toString();
    }


    // Todo: Should optimize this.
    // Todo: Should implement pagination logic
    private ReplyKeyboard buildButtons(List<MusicDTO> musics) {

        AtomicInteger counter = new AtomicInteger(1);

        InlineKeyboardMarkup inline = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> allButtons = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        for (MusicDTO music : musics) {

            if (buttons.size() == 5) {
                allButtons.add(buttons);
                buttons = new ArrayList<>();
            }

            buttons.add(buildButton(String.valueOf(counter.getAndIncrement()), String.valueOf(music.getId())));
        }

        if (buttons.size() != 0) {
            allButtons.add(buttons);
        }


        allButtons.add(buildControllerButtons());
        inline.setKeyboard(allButtons);

        return inline;
    }

    private List<InlineKeyboardButton> buildControllerButtons() {
        return List.of(
                buildButton(PREVIOUS_BUTTON_TEXT, "NULL"),
                buildButton(CANCEL_BUTTON_TEXT, CANCEL_BUTTON_CALLBACK),
                buildButton(NEXT_BUTTON_TEXT, "NULL")
        );
    }

    private InlineKeyboardButton buildButton(String text, String callback) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callback)
                .build();
    }
}
