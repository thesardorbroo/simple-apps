package uz.sardorbroo.musicfinderbot.service.impl;

import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import uz.sardorbroo.musicfinderbot.service.MusicCatalogService;
import uz.sardorbroo.musicfinderbot.service.MusicService;
import uz.sardorbroo.musicfinderbot.service.builder.ButtonBuilder;
import uz.sardorbroo.musicfinderbot.service.builder.TextBuilder;
import uz.sardorbroo.musicfinderbot.service.builder.impl.InlineKeyboardBuilder;
import uz.sardorbroo.musicfinderbot.service.builder.impl.MessageTextBuilder;
import uz.sardorbroo.musicfinderbot.service.builder.impl.PlainSendMessageManager;
import uz.sardorbroo.musicfinderbot.service.dto.MusicDTO;
import uz.sardorbroo.musicfinderbot.service.dto.PageDTO;
import uz.sardorbroo.musicfinderbot.service.utils.AbsSenderUtils;
import uz.sardorbroo.musicfinderbot.service.utils.CallbackDataExtractorUtils;
import uz.sardorbroo.musicfinderbot.service.utils.ResourceBundleUtils;
import uz.sardorbroo.musicfinderbot.service.utils.UserUtils;
import uz.sardorbroo.musicfinderbot.service.utils.service.dto.PaginationCallbackDTO;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

@Slf4j
@Service
public class MusicCatalogServiceImpl implements MusicCatalogService {
    private static final String NO_MORE_PAGES = "no.more.pages";
    private static final String MUSIC_NOT_FOUND = "music.not.found";
    private static final String SEARCHING_CANCELED = "searching.canceled";

    private final MusicService musicService;

    public MusicCatalogServiceImpl(MusicService musicService) {
        this.musicService = musicService;
    }

    public Optional<EditMessageText> controlPagination(Update update) {

        User user = UserUtils.extractUserOrThrow(update);

        Optional<PaginationCallbackDTO> paginationCallbackOptional = CallbackDataExtractorUtils.extractPaginationCallback(update);
        if (paginationCallbackOptional.isEmpty()) {
            log.warn("Invalid argument is passed! Could not extract callback data!");
            return Optional.empty();
        }

        PaginationCallbackDTO paginationCallback = paginationCallbackOptional.get();
        if (isPageNegative(paginationCallback.getPage())) {
            log.debug("Page is negative! Page: {}", paginationCallback.getPage());
            errorAlert(update, NO_MORE_PAGES);
            return Optional.empty();
        }

        PageDTO pagination = new PageDTO(paginationCallback.getPage(), 10);
        List<MusicDTO> musics = musicService.find(paginationCallback.getMusic(), pagination);
        if (musics.isEmpty()) {
            log.debug("Musics are not found! Music name: {}", paginationCallback.getMusic());
            musicNotFound(user);
            return Optional.empty();
        }

        return Optional.of(buildMessage(user, musics, pagination, paginationCallback.getMusic(), update.getCallbackQuery().getMessage().getMessageId()));
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

    private boolean isPageNegative(int page) {
        return page < 0;
    }

    private void errorAlert(Update update, String messageKey) {

        User user = UserUtils.extractUserOrThrow(update);
        String callbackQueryId = update.getCallbackQuery().getId();

        ResourceBundle bundle = ResourceBundleUtils.getBundle(user.getLanguageCode());

        String text = bundle.getString(messageKey);

        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setText(text);
        answerCallbackQuery.setShowAlert(true);
        answerCallbackQuery.setCacheTime(5);
        answerCallbackQuery.setCallbackQueryId(callbackQueryId);

        AbsSenderUtils.send(answerCallbackQuery);
    }

    private void musicNotFound(User user) {
        ResourceBundle bundle = ResourceBundleUtils.getBundle(user.getLanguageCode());

        String text = bundle.getString(MUSIC_NOT_FOUND);

        SendMessage message = new SendMessage();
        message.setChatId(user.getId());
        message.setText(text);

        AbsSenderUtils.send(message);
    }

    // Todo: minimize arguments
    private EditMessageText buildMessage(User user, List<MusicDTO> musics, PageDTO pagination, String musicName, int messageId) {

        Optional<SendMessage> messageOptional = buildMessage(musics, String.valueOf(user.getId()), pagination, musicName);
        if (messageOptional.isEmpty()) {
            throw new NotFoundException("SendMessage object is not found!");
        }

        EditMessageText message = new EditMessageText();
        message.setChatId(user.getId());
        message.setText(messageOptional.get().getText());
        message.setReplyMarkup((InlineKeyboardMarkup) messageOptional.get().getReplyMarkup());
        message.setMessageId(messageId);

        return message;
    }

    private Optional<SendMessage> buildMessage(List<MusicDTO> musics, String chatId, PageDTO pagination, String musicName) {

        TextBuilder textBuilder = new MessageTextBuilder();
        ButtonBuilder buttonBuilder = new InlineKeyboardBuilder(pagination, musicName);

        return PlainSendMessageManager.builder(musics, chatId)
                .setTextBuilder(textBuilder)
                .setButtonBuilder(buttonBuilder)
                .build();
    }
}
