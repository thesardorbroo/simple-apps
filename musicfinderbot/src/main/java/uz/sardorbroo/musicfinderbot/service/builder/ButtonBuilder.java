package uz.sardorbroo.musicfinderbot.service.builder;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import uz.sardorbroo.musicfinderbot.service.builder.dto.ButtonBuilderDTO;
import uz.sardorbroo.musicfinderbot.service.dto.MusicDTO;

import java.util.List;
import java.util.Optional;

public interface ButtonBuilder {

    Optional<ReplyKeyboard> build(ButtonBuilderDTO buttonBuilderDTO);
    Optional<ReplyKeyboard> build(List<MusicDTO> musics);
}
