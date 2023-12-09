package uz.sardorbroo.musicfinderbot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import uz.sardorbroo.musicfinderbot.service.MusicDownloadService;
import uz.sardorbroo.musicfinderbot.service.MusicService;
import uz.sardorbroo.musicfinderbot.service.integration.BotClientService;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class MusicDownloadServiceImpl implements MusicDownloadService {
    private static final String TEMP_FILE_NAME = "temp-music-file.mp3";
    private final MusicService musicService;
    private final BotClientService botClientService;

    private final Boolean ready;

    public MusicDownloadServiceImpl(MusicService musicService,
                                    BotClientService botClientService,
                                    @Value("${bot.send-audio.ready}") Boolean ready) {
        this.musicService = musicService;
        this.botClientService = botClientService;
        this.ready = ready;
    }

    public Optional<SendAudio> download(Update update) {
        log.debug("Start downloading music by ID");

        User user = update.getCallbackQuery().getFrom();
        String musicIdAsMessage = update.getCallbackQuery().getData();

        Optional<InputFile> inputFileOptional = convert(musicService.download(musicIdAsMessage));
        if (inputFileOptional.isEmpty()) {
            log.warn("Cannot download music! Music is not found! MusicID: {}", musicIdAsMessage);
            return Optional.empty();
        }

        SendAudio audio = new SendAudio();
        audio.setAudio(inputFileOptional.get());
        audio.setChatId(user.getId());
        audio.setCaption("Fake caption");

        if (!ready) {
            botClientService.sendAudio(audio);
            return Optional.empty();
        }

        return Optional.of(audio);
    }

    private Optional<InputFile> convert(byte[] musicAsArray) {

        if (Objects.isNull(musicAsArray) || musicAsArray.length == 0) {
            log.warn("Invalid argument is passed! MusicArray must not be empty!");
            return Optional.empty();
        }

        try {
            File tempFile = new File(TEMP_FILE_NAME);

            FileUtils.writeByteArrayToFile(tempFile, musicAsArray);

            InputFile inputFile = new InputFile();
            inputFile.setMedia(tempFile, TEMP_FILE_NAME);

            tempFile.delete();
            return Optional.of(inputFile);
        } catch (IOException e) {
            log.warn("Error while converting MusicByteArray to File! Exception: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
