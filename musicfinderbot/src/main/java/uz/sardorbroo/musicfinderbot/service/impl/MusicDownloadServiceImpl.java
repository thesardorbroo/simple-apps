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

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class MusicDownloadServiceImpl implements MusicDownloadService {
    private final MusicService musicService;

    public MusicDownloadServiceImpl(MusicService musicService) {
        this.musicService = musicService;
    }

    // Todo: should optimize
    public Optional<SendAudio> download(Update update) {
        log.debug("Start downloading music by ID");

        User user = update.getCallbackQuery().getFrom();
        String musicIdAsMessage = update.getCallbackQuery().getData();

        Optional<MusicResourceDTO> musicOptional = musicService.download(musicIdAsMessage);
        if (musicOptional.isEmpty()) {
            log.debug("Music is not downloaded!");
            return Optional.empty();
        }

        Optional<InputFile> inputFileOptional = convert(musicOptional.get());
        if (inputFileOptional.isEmpty()) {
            log.warn("Cannot download music! Music is not found! MusicID: {}", musicIdAsMessage);
            return Optional.empty();
        }

        SendAudio audio = new SendAudio();
        audio.setAudio(inputFileOptional.get());
        audio.setChatId(user.getId());

        return Optional.of(audio);
    }

    private Optional<InputFile> convert(MusicResourceDTO music) {

        if (Objects.isNull(music)) {
            log.warn("Invalid argument is passed! MusicArray must not be empty!");
            return Optional.empty();
        }

        try {

            InputFile inputFile = new InputFile();
            inputFile.setMedia(music.getInputStream(), music.getTitle());

            return Optional.of(inputFile);
        } catch (Exception e) {
            log.warn("Error while converting MusicByteArray to File! Exception: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
