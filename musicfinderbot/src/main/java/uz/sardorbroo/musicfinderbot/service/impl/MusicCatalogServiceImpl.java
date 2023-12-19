package uz.sardorbroo.musicfinderbot.service.impl;

import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import uz.sardorbroo.musicfinderbot.service.MusicCatalogButtonService;
import uz.sardorbroo.musicfinderbot.service.MusicCatalogService;
import uz.sardorbroo.musicfinderbot.service.MusicService;
import uz.sardorbroo.musicfinderbot.service.dto.MusicDTO;
import uz.sardorbroo.musicfinderbot.service.dto.PageDTO;
import uz.sardorbroo.musicfinderbot.service.utils.CallbackDataExtractorUtils;
import uz.sardorbroo.musicfinderbot.service.utils.ResourceBundleUtils;
import uz.sardorbroo.musicfinderbot.service.utils.UserUtils;
import uz.sardorbroo.musicfinderbot.service.utils.service.dto.PaginationCallbackDTO;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class MusicCatalogServiceImpl implements MusicCatalogService {
    private static final String MUSIC_NOT_FOUND_KEY = "no.more.pages";
    private static final String SEARCHING_CANCELED = "searching.canceled";

    private final MusicService musicService;
    private final MusicCatalogButtonService musicCatalogButtonService;

    public MusicCatalogServiceImpl(MusicService musicService, MusicCatalogButtonService musicCatalogButtonService) {
        this.musicService = musicService;
        this.musicCatalogButtonService = musicCatalogButtonService;
    }

    public Optional<SendMessage> resolveCallback(Update update) {

        User user = UserUtils.extractUserOrThrow(update);

        Optional<PaginationCallbackDTO> paginationCallbackOptional = CallbackDataExtractorUtils.extractPaginationCallback(update);
        if (paginationCallbackOptional.isEmpty()) {
            log.warn("Invalid argument is passed! Could not extract callback data!");
            return Optional.empty();
        }

        PaginationCallbackDTO paginationCallback = paginationCallbackOptional.get();
        if (isPageNegative(paginationCallback.getPage())) {
            log.debug("Page is negative! Page: {}", paginationCallback.getPage());
            // noMorePages(user) Todo: application should answer to callback query when music are not found! but method returns SendMessage
            return Optional.empty();
        }

        PageDTO pagination = new PageDTO(paginationCallback.getPage(), 10);
        List<MusicDTO> musics = musicService.find(paginationCallback.getMusic(), pagination);
        if (musics.isEmpty()) {
            log.debug("Musics are not found! Music name: {}", paginationCallback.getMusic());
            // noMorePages(user) Todo: application should answer to callback query when music are not found! but method returns SendMessage
            return Optional.empty();
        }

        return Optional.of(buildMessage(user, musics, pagination, paginationCallback.getMusic()));
    }

    @Override
    public Optional<EditMessageText> cancelMusicDashboard(Update update) {
        log.debug("Cancel searching the music");

        User user = UserUtils.extractUserOrThrow(update);

        String inlineMessageId = update.getCallbackQuery().getInlineMessageId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();

        InlineKeyboardMarkup keyboard = emptyKeyboardMarkup();

        ResourceBundle bundle = ResourceBundleUtils.getBundle(user.getLanguageCode());
        String text = bundle.getString(SEARCHING_CANCELED);

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(user.getId());
        editMessageText.setReplyMarkup(keyboard);
        editMessageText.setText(text);
        editMessageText.setMessageId(messageId);
        editMessageText.setInlineMessageId(inlineMessageId);

        return Optional.of(editMessageText);
    }

    private InlineKeyboardMarkup emptyKeyboardMarkup() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(Collections.emptyList());

        return markup;
    }

    private ReplyKeyboard buildButtons(List<MusicDTO> musics, PageDTO pagination, String musicName) {
        return musicCatalogButtonService.buildButtons(musics, pagination, musicName)
                .orElseThrow(() -> new NotFoundException("Inline buttons are not found!"));
    }

    private boolean isPageNegative(int page) {
        return page < 0;
    }

    private SendMessage noMorePages(User user) {

        ResourceBundle bundle = ResourceBundleUtils.getBundle(user.getLanguageCode());

        String text = bundle.getString(MUSIC_NOT_FOUND_KEY);

        SendMessage message = new SendMessage();
        message.setChatId(user.getId());
        message.setText(text);

        return message;
    }

    // Todo: move it to another service like button builders
    private SendMessage buildMessage(User user, List<MusicDTO> musics, PageDTO pagination, String musicName) {

        String text = buildMessageText(musics);

        InlineKeyboardMarkup inlineMarkup = (InlineKeyboardMarkup) buildButtons(musics, pagination, musicName);

        SendMessage message = new SendMessage();
        message.setChatId(user.getId());
        message.setText(text);
        message.setReplyMarkup(inlineMarkup);

        return message;
    }

    // Todo: move it to another service like button builders
    private String buildMessageText(List<MusicDTO> musics) {

        AtomicInteger counter = new AtomicInteger(1);

        StringBuilder text = new StringBuilder();

        musics.forEach(music -> {
            String line = String.format("%d. %s - %s | %s\n", counter.getAndIncrement(), music.getArtist(), music.getTitle(), String.valueOf(music.getDuration()));
            text.append(line);
        });

        return text.toString();
    }
}
