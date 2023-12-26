package uz.sardorbroo.musicfinderbot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import uz.sardorbroo.musicfinderbot.service.MusicDownloadService;
import uz.sardorbroo.musicfinderbot.service.MusicService;
import uz.sardorbroo.musicfinderbot.service.dto.MusicResourceDTO;
import uz.sardorbroo.musicfinderbot.service.utils.CallbackDataExtractorUtils;
import uz.sardorbroo.musicfinderbot.service.utils.UserUtils;
import uz.sardorbroo.musicfinderbot.service.utils.service.dto.MusicCallbackDTO;

import java.util.Optional;

@Slf4j
@Service
public class MusicDownloadServiceImpl implements MusicDownloadService {
    private final MusicService musicService;

    public MusicDownloadServiceImpl(MusicService musicService) {
        this.musicService = musicService;
    }

    public Optional<SendAudio> download(Update update) {
        log.debug("Start downloading music by ID");

        User user = UserUtils.extractUserOrThrow(update);
        Optional<MusicCallbackDTO> musicCallbackOptional = CallbackDataExtractorUtils.extractMusicCallback(update);
        if (musicCallbackOptional.isEmpty()) {
            log.warn("MusicID is not found!");
            return Optional.empty();
        }
        String musicIdAsMessage = musicCallbackOptional.get().getMusicId();

        Optional<MusicResourceDTO> musicOptional = musicService.download(musicIdAsMessage);
        if (musicOptional.isEmpty()) {
            log.debug("Music is not downloaded!");
            return Optional.empty();
        }

        InputFile inputFile = convert(musicOptional.get());

        SendAudio audio = new SendAudio();
        audio.setAudio(inputFile);
        audio.setChatId(user.getId());

        return Optional.of(audio);
    }

    private InputFile convert(MusicResourceDTO music) {

        InputFile inputFile = new InputFile();
        inputFile.setMedia(music.getInputStream(), music.getTitle());

        return inputFile;
    }
}
