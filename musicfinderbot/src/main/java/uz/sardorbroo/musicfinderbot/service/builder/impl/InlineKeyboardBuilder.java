package uz.sardorbroo.musicfinderbot.service.builder.impl;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.sardorbroo.musicfinderbot.config.constants.CallbackPrefix;
import uz.sardorbroo.musicfinderbot.service.builder.ButtonBuilder;
import uz.sardorbroo.musicfinderbot.service.builder.dto.ButtonBuilderDTO;
import uz.sardorbroo.musicfinderbot.service.builder.dto.InlineButtonBuilderDTO;
import uz.sardorbroo.musicfinderbot.service.dto.MusicDTO;
import uz.sardorbroo.musicfinderbot.service.dto.PageDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

@Slf4j
public class InlineKeyboardBuilder implements ButtonBuilder {
    private static final Integer INLINE_BUTTONS_MAX_LENGTH_PER_LINE = 5;
    private static final String INLINE_BUTTONS_DELIMITER = CallbackPrefix.DELIMITER;
    private static final String MUSIC_BUTTONS_CALLBACK_DATA_PREFIX = CallbackPrefix.MUSIC_ID_PREFIX;
    private static final String PAGINATION_BUTTONS_CALLBACK_DATA_PREFIX = CallbackPrefix.PAGE_PREFIX;
    private static final String CANCEL_BUTTON_CALLBACK_DATA_PREFIX = CallbackPrefix.CANCEL_PREFIX;

    private static final String PREVIOUS_BUTTON_TEXT = "⬅️";
    private static final String CANCEL_BUTTON_TEXT = "❌";
    private static final String NEXT_BUTTON_TEXT = "➡️";

    private final PageDTO pagination;
    private final String musicName;

    public InlineKeyboardBuilder(PageDTO pagination, String musicName) {
        this.pagination = pagination;
        this.musicName = musicName;
    }

    @Override
    public Optional<ReplyKeyboard> build(ButtonBuilderDTO buttonBuilderDTO) {
        log.warn("Start building inline buttons");

        Optional<InlineButtonBuilderDTO> inlineButtonOptional = cast(buttonBuilderDTO);
        if (inlineButtonOptional.isEmpty()) {
            log.warn("ButtonBuilderDTO cannot cast to InlineButtonBuilderDTO");
            return Optional.empty();
        }
        InlineButtonBuilderDTO inlineButtonDTO = inlineButtonOptional.get();

        return buildButtons(inlineButtonDTO);
    }

    @Override
    public Optional<ReplyKeyboard> build(List<MusicDTO> musics) {

        if (Objects.isNull(musics) || musics.isEmpty()) {
            log.warn("Invalid argument is passed! MusicDTO collection must not be empty!");
            return Optional.empty();
        }

        InlineButtonBuilderDTO inlineButtonDTO = new InlineButtonBuilderDTO();
        inlineButtonDTO.setMusics(musics);
        inlineButtonDTO.setMusicName(musicName);
        inlineButtonDTO.setPagination(pagination);

        return build(inlineButtonDTO);
    }

    private Optional<ReplyKeyboard> buildButtons(InlineButtonBuilderDTO inlineButtonDTO) {
        AtomicInteger counter = new AtomicInteger(1);

        InlineKeyboardMarkup inline = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> allButtons = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        for (MusicDTO music : inlineButtonDTO.getMusics()) {

            String callbackData = buildMusicCallbackData(String.valueOf(music.getId()));
            InlineKeyboardButton button = buildButton(counter.getAndIncrement(), callbackData);
            buttons.add(button);

            addButtons2Keyboard(allButtons, buttons, this::isFull);
        }

        addButtons2Keyboard(allButtons, buttons, buttonsLine -> !isFull(buttonsLine));

        int page = inlineButtonDTO.getPagination().getPage();

        allButtons.add(buildControllerButtons(page - 1, page + 1, inlineButtonDTO.getMusicName()));
        inline.setKeyboard(allButtons);

        return Optional.of(inline);
    }

    private Optional<InlineButtonBuilderDTO> cast(ButtonBuilderDTO buttonBuilderDTO) {

        if (Objects.isNull(buttonBuilderDTO)) {
            log.warn("Invalid argument is passed! ButtonBuilderDTO must not be null!");
            return Optional.empty();
        }

        if (!(buttonBuilderDTO instanceof InlineButtonBuilderDTO)) {
            log.warn("ButtonBuilderDTO type is not InlineButtonBuilderDTO");
            return Optional.empty();
        }

        return Optional.of((InlineButtonBuilderDTO) buttonBuilderDTO);
    }

    private boolean isFull(List<InlineKeyboardButton> buttons) {

        if (Objects.isNull(buttons)) {
            throw new RuntimeException("Buttons list must not be null!");
        }

        return Objects.equals(INLINE_BUTTONS_MAX_LENGTH_PER_LINE, buttons.size());
    }

    private void addButtons2Keyboard(List<List<InlineKeyboardButton>> keyboard, List<InlineKeyboardButton> buttonsLine, Predicate<List<InlineKeyboardButton>> condition) {

        if (Objects.isNull(keyboard)) {
            throw new RuntimeException("Main keyboard of inline buttons must not be null!");
        }

        if (condition.test(buttonsLine)) {
            keyboard.add(new ArrayList<>(buttonsLine));
            buttonsLine.clear();
        }
    }

    private List<InlineKeyboardButton> buildControllerButtons(int previous, int next, String musicName) {

        return List.of(
                buildButton(PREVIOUS_BUTTON_TEXT, buildPaginationCallbackData(previous, musicName)),
                buildButton(CANCEL_BUTTON_TEXT, buildCancelCallbackData()),
                buildButton(NEXT_BUTTON_TEXT, buildPaginationCallbackData(next, musicName))
        );
    }

    private InlineKeyboardButton buildButton(String text, String callback) {

        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callback)
                .build();
    }

    private InlineKeyboardButton buildButton(Integer text, String callback) {
        return buildButton(String.valueOf(text), callback);
    }

    private String buildPaginationCallbackData(int page, String musicName) {
        return String.join(INLINE_BUTTONS_DELIMITER, PAGINATION_BUTTONS_CALLBACK_DATA_PREFIX, String.valueOf(page), musicName);
    }

    private String buildMusicCallbackData(String callback) {
        return String.join(INLINE_BUTTONS_DELIMITER, MUSIC_BUTTONS_CALLBACK_DATA_PREFIX, callback);
    }

    private String buildCancelCallbackData() {
        return CANCEL_BUTTON_CALLBACK_DATA_PREFIX;
    }
}
