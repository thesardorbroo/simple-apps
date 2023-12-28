package uz.sardorbroo.musicfinderbot.service.builder.impl;

import org.apache.commons.lang3.NotImplementedException;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import uz.sardorbroo.musicfinderbot.service.builder.ButtonBuilder;
import uz.sardorbroo.musicfinderbot.service.builder.dto.ButtonBuilderDTO;
import uz.sardorbroo.musicfinderbot.service.dto.MusicDTO;

import java.util.List;
import java.util.Optional;

public class ReplyKeyboardBuilder implements ButtonBuilder {

    @Override
    public Optional<ReplyKeyboard> build(ButtonBuilderDTO buttonBuilderDTO) {
        throw new NotImplementedException("Building ReplyKeyboardButtons is not implemented!");
    }

    @Override
    public Optional<ReplyKeyboard> build(List<MusicDTO> musics) {
        throw new NotImplementedException("Building ReplyKeyboardButtons is not implemented!");
    }
}
