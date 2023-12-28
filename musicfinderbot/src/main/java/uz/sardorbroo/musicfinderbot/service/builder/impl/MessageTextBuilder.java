package uz.sardorbroo.musicfinderbot.service.builder.impl;

import lombok.extern.slf4j.Slf4j;
import uz.sardorbroo.musicfinderbot.service.builder.TextBuilder;
import uz.sardorbroo.musicfinderbot.service.dto.MusicDTO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class MessageTextBuilder implements TextBuilder {

    @Override
    public Optional<String> build(List<MusicDTO> musics) {
        log.debug("Building text of message");

        AtomicInteger counter = new AtomicInteger(1);

        StringBuilder text = new StringBuilder();

        musics.forEach(music -> {
            String duration = resolveDuration(music.getDuration());
            String line = String.format("%d. %s - %s | %s\n", counter.getAndIncrement(), music.getArtist(), music.getTitle(), duration);
            text.append(line);
        });

        return Optional.of(text.toString());
    }

    private String resolveDuration(long durationAsLong) {

        Date date = new Date(durationAsLong);

        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");

        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        return formatter.format(date);
    }
}
