package uz.sardorbroo.musicfinderbot.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.sardorbroo.musicfinderbot.config.constants.CallbackPrefix;
import uz.sardorbroo.musicfinderbot.service.MusicCatalogButtonService;
import uz.sardorbroo.musicfinderbot.service.dto.MusicDTO;
import uz.sardorbroo.musicfinderbot.service.dto.PageDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class MusicCatalogButtonServiceImpl implements MusicCatalogButtonService {

    private static final String INLINE_BUTTONS_DELIMITER = CallbackPrefix.DELIMITER;
    private static final String MUSIC_BUTTONS_CALLBACK_DATA_PREFIX = CallbackPrefix.MUSIC_ID_PREFIX;
    private static final String PAGINATION_BUTTONS_CALLBACK_DATA_PREFIX = CallbackPrefix.PAGE_PREFIX;
    private static final String CANCEL_BUTTON_CALLBACK_DATA_PREFIX = CallbackPrefix.CANCEL_PREFIX;

    private static final String PREVIOUS_BUTTON_TEXT = "⬅️";
    private static final String CANCEL_BUTTON_TEXT = "❌";
    private static final String NEXT_BUTTON_TEXT = "➡️";

    // Todo: Should optimize this.
    // Todo: Minimize arguments
    @Override
    public Optional<ReplyKeyboard> buildButtons(List<MusicDTO> musics, PageDTO pagination, String musicName) {

        AtomicInteger counter = new AtomicInteger(1);

        InlineKeyboardMarkup inline = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> allButtons = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        for (MusicDTO music : musics) {

            if (buttons.size() == 5) {
                allButtons.add(buttons);
                buttons = clear();
            }

            String callbackData = buildMusicCallbackData(String.valueOf(music.getId()));
            buttons.add(buildButton(String.valueOf(counter.getAndIncrement()), callbackData));
        }

        if (buttons.size() != 0) {
            allButtons.add(buttons);
        }


        allButtons.add(buildControllerButtons(pagination.getPage() - 1, pagination.getPage() + 1, musicName));
        inline.setKeyboard(allButtons);

        return Optional.of(inline);
    }

    private InlineKeyboardButton buildButton(String text, String callback) {

        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callback)
                .build();
    }

    private List<InlineKeyboardButton> buildControllerButtons(int previous, int next, String musicName) {

        // DISCUSS: If need add conditions for previous and next;

        return List.of(
                buildButton(PREVIOUS_BUTTON_TEXT, buildPaginationCallbackData(previous, musicName)),
                buildButton(CANCEL_BUTTON_TEXT, buildCancelCallbackData()),
                buildButton(NEXT_BUTTON_TEXT, buildPaginationCallbackData(next, musicName))
        );
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

    private <T> List<T> clear() {
        return new ArrayList<>();
    }
}
