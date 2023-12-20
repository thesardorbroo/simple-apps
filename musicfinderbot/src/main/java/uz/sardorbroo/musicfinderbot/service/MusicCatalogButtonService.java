package uz.sardorbroo.musicfinderbot.service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import uz.sardorbroo.musicfinderbot.service.dto.MusicDTO;
import uz.sardorbroo.musicfinderbot.service.dto.PageDTO;

import java.util.List;
import java.util.Optional;

public interface MusicCatalogButtonService {

    Optional<ReplyKeyboard> buildButtons(List<MusicDTO> musics, PageDTO pagination, String musicName);
}
