package uz.sardorbroo.musicfinderbot.service.integration.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.UriBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.objects.Message;
import uz.sardorbroo.musicfinderbot.service.integration.BotClientService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

@Slf4j
@Service
public class BotClientServiceImpl implements BotClientService {

    // private static final String BOT_TOKEN_ARG = "BOT_TOKEN";
    private static final String SEND_AUDIO_ENDPOINT = "/sendAudio";
    private static final String TELEGRAM_BASE_API = "https://api.telegram.org/bot";

    @Value("${fluent.bot.token}")
    private String token;

    private final ObjectMapper mapper;

    public BotClientServiceImpl(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<Message> sendAudio(SendAudio audio) {
        log.debug("Start sending audio with non-lib code");

        URI uri = buildUri();

        Optional<String> requestBodyOptional = convertRequestBody(audio);
        if (requestBodyOptional.isEmpty()) {
            log.warn("Invalid argument is passed! SendAudio is not converted to String!");
            return Optional.empty();
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyOptional.get()))
                .build();

        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());

            return convert(response.body());
        } catch (Exception e) {
            log.error("Error while sending audio to user! Exception: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private URI buildUri() {

        String uriAsString = String.join("", TELEGRAM_BASE_API, token);

        return UriBuilder.fromUri(uriAsString)
                .path(SEND_AUDIO_ENDPOINT)
                .build();
    }

    private Optional<String> convertRequestBody(SendAudio audio) {
        try {
            String requestBodyAsString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(audio);
            return Optional.of(requestBodyAsString);
        } catch (JsonProcessingException e) {
            log.error("Error while converting SendAudio object to String! Exception: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<Message> convert(String updateAsString) {
        try {
            Message message = mapper.readValue(updateAsString, new TypeReference<Message>() {
            });
            return Optional.of(message);
        } catch (Exception e) {
            log.error("Error while converting String to Update! Exception: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
