package uz.sardorbroo.musicfinderbot.service;

import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

public interface MusicDownloadService {

    Optional<SendAudio> download(Update update);
}
