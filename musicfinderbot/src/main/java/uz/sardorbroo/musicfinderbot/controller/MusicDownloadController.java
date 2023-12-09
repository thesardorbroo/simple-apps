package uz.sardorbroo.musicfinderbot.controller;

import lombok.extern.slf4j.Slf4j;
import org.khasanof.annotation.UpdateController;
import org.khasanof.annotation.methods.HandleAny;
import org.khasanof.enums.HandleType;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import uz.sardorbroo.musicfinderbot.service.MusicDownloadService;
import uz.sardorbroo.musicfinderbot.service.utils.AbsSenderUtils;

import java.util.Optional;

@Slf4j
@UpdateController
public class MusicDownloadController {

    private final MusicDownloadService musicDownloadService;

    public MusicDownloadController(MusicDownloadService musicDownloadService) {
        this.musicDownloadService = musicDownloadService;
    }

    @HandleAny(type = HandleType.CALLBACK)
    public void handleMusic(Update update, AbsSender sender) {
        log.debug("Callback message is handled!");
        Optional<SendAudio> audioOptional = musicDownloadService.download(update);
        audioOptional.ifPresent(audio -> AbsSenderUtils.send(sender, audio));
        log.debug("Successfully answered!");
    }
}
