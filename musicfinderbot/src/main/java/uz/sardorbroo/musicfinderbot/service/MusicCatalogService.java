package uz.sardorbroo.musicfinderbot.service;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

public interface MusicCatalogService {

    Optional<EditMessageText> controlPagination(Update update);

    Optional<EditMessageText> cancelMusicDashboard(Update update);
}
