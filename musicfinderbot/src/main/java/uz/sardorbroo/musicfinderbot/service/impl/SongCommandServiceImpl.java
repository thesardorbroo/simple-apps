package uz.sardorbroo.musicfinderbot.service.impl;

import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import uz.sardorbroo.musicfinderbot.enumeration.Command;
import uz.sardorbroo.musicfinderbot.service.CommandService;
import uz.sardorbroo.musicfinderbot.service.MusicCatalogButtonService;
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
    private static final PageDTO DEFAULT_PAGEABLE = new PageDTO(0, 10);

    private static final String SPLITERATOR_PREFIX = " ";
    private static final String MUSIC_NOT_FOUND_KEY = "music.not.found";
    private static final Command SUPPORTED_COMMAND = Command.SONG;

    private final MusicService musicService;
    private final MusicCatalogButtonService musicCatalogButtonService;

    public SongCommandServiceImpl(MusicService musicService, MusicCatalogButtonService musicCatalogButtonService) {
        this.musicService = musicService;
        this.musicCatalogButtonService = musicCatalogButtonService;
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

        List<MusicDTO> musics = musicService.find(musicName.toString(), DEFAULT_PAGEABLE);
        if (musics.isEmpty()) {
            log.debug("Musics are not found! Music name: {}", musicName);
            return Optional.of(musicNotFound(user));
        }

        return Optional.of(buildMessage(user, musics, DEFAULT_PAGEABLE, musicName.toString()));
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

    private SendMessage buildMessage(User user, List<MusicDTO> musics, PageDTO pagination, String musicName) {

        String text = buildMessageText(musics);

        InlineKeyboardMarkup inlineMarkup = (InlineKeyboardMarkup) buildButtons(musics, pagination, musicName)
                .orElseThrow(() -> new NotFoundException("Inline buttons are not found!"));

        SendMessage message = new SendMessage();
        message.setChatId(user.getId());
        message.setText(text);
        message.setReplyMarkup(inlineMarkup);

        return message;
    }

    private Optional<ReplyKeyboard> buildButtons(List<MusicDTO> musics, PageDTO pagination, String musicName) {
        return musicCatalogButtonService.buildButtons(musics, pagination, musicName);
    }

    private String buildMessageText(List<MusicDTO> musics) {

        AtomicInteger counter = new AtomicInteger(1);

        StringBuilder text = new StringBuilder();

        musics.forEach(music -> {
            String line = String.format("%d. %s - %s | %s\n", counter.getAndIncrement(), music.getArtist(), music.getTitle(), music.getDuration());
            text.append(line);
        });

        return text.toString();
    }
}
