package uz.sardorbroo.musicfinderbot.controller;

import lombok.extern.slf4j.Slf4j;
import org.khasanof.annotation.UpdateController;
import org.khasanof.annotation.methods.HandleCallback;
import org.khasanof.enums.MatchScope;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import uz.sardorbroo.musicfinderbot.config.constants.CallbackPrefix;
import uz.sardorbroo.musicfinderbot.service.MusicCatalogService;
import uz.sardorbroo.musicfinderbot.service.MusicDownloadService;
import uz.sardorbroo.musicfinderbot.service.utils.AbsSenderUtils;

import java.util.Optional;

@Slf4j
@UpdateController
public class MusicDownloadController {

    private final MusicDownloadService musicDownloadService;
    private final MusicCatalogService musicCatalogService;

    public MusicDownloadController(MusicDownloadService musicDownloadService, MusicCatalogService musicCatalogService) {
        this.musicDownloadService = musicDownloadService;
        this.musicCatalogService = musicCatalogService;
    }

    @HandleCallback(values = CallbackPrefix.MUSIC_ID_PREFIX, scope = MatchScope.START_WITH)
    public void handleMusic(Update update, AbsSender sender) {
        log.debug("Callback message is handled");
        // Todo uncomment when context attributes will work
        // AbsSenderUtils.setContext(sender);
        Optional<SendAudio> audioOptional = musicDownloadService.download(update);
        audioOptional.ifPresent(audio -> AbsSenderUtils.send(sender, audio));
        log.debug("Successfully answered!");
    }

    @HandleCallback(values = CallbackPrefix.PAGE_PREFIX, scope = MatchScope.START_WITH)
    public void handlePagination(Update update, AbsSender sender) {
        log.debug("Callback for controlling pagination");
        // Todo uncomment when context attributes will work
        // AbsSenderUtils.setContext(sender);
        Optional<EditMessageText> audioOptional = musicCatalogService.controlPagination(update);
        audioOptional.ifPresent(audio -> AbsSenderUtils.send(sender, audio));
        log.debug("Successfully answered!");
    }

    @HandleCallback(values = CallbackPrefix.CANCEL_PREFIX, scope = MatchScope.EQUALS)
    public void handCancel(Update update, AbsSender sender) {
        log.debug("Callback for canceling music dashboard");
        // Todo uncomment when context attributes will work
        // AbsSenderUtils.setContext(sender);
        Optional<EditMessageText> messageOptional = musicCatalogService.cancelMusicDashboard(update);
        messageOptional.ifPresent(message -> AbsSenderUtils.send(sender, message));
        log.debug("Successfully answered!");
    }
}
