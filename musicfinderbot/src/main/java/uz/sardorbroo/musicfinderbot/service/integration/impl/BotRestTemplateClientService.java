package uz.sardorbroo.musicfinderbot.service.integration.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import uz.sardorbroo.musicfinderbot.enumeration.Command;
import uz.sardorbroo.musicfinderbot.enumeration.Language;
import uz.sardorbroo.musicfinderbot.service.integration.BotClientService;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service("REST_TEMPLATE")
public class BotRestTemplateClientService implements BotClientService {
    private static final Language DEFAULT_LANGUAGE = Language.RU;
    private static final String BASE_URL = "https://api.telegram.org/bot${TOKEN}/${METHOD}";
    private static final String DELETE_COMMANDS_METHOD = "deleteMyCommands";
    private static final String SET_COMMANDS_METHOD = "setMyCommands";
    private static final String SEND_AUDIO_METHOD = "sendAudio";

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${fluent.bot.token}")
    private String token;

    @Autowired
    private ObjectMapper mapper;

    @Override
    public Optional<Message> sendAudio(SendAudio audio) {
        log.debug("Start sending audio with non-lib code");


        HttpEntity<SendAudio> requestBody = new HttpEntity<>(audio);

        try {
            URI uri = buildUri(SEND_AUDIO_METHOD);
            ResponseEntity<Message> response = restTemplate.exchange(uri, HttpMethod.POST, requestBody, Message.class);

            return Objects.nonNull(response.getBody()) ? Optional.of(response.getBody()) : Optional.empty();
        } catch (Exception e) {
            log.error("Error while sending audio to user! Exception: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public boolean deleteCommands() {
        log.debug("Deleting all commands");

        try {
            URI uri = buildUri(DELETE_COMMANDS_METHOD);
            String responseAsString = restTemplate.getForObject(uri, String.class);

            System.out.println(responseAsString);

            return StringUtils.isNotBlank(responseAsString);
        } catch (Exception e) {
            log.warn("Error while deleting commands! Exception: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean setCommands(List<Command> commands) {
        log.debug("Set selected commands");

        SetMyCommands botCommands = new SetMyCommands();
        botCommands.setCommands(map(commands));

        HttpEntity<SetMyCommands> requestBody = new HttpEntity<>(botCommands);

        try {
            URI uri = buildUri(SET_COMMANDS_METHOD);
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, requestBody, String.class);

            System.out.println(response.getBody());
            return StringUtils.isNotBlank(response.getBody());
        } catch (Exception e) {
            log.warn("Error while setting commands! Exception: {}", e.getMessage());
            return false;
        }
    }

    private URI buildUri(String method) throws URISyntaxException {
        Map<String, String> map = Map.of("TOKEN", token, "METHOD", method);

        String uri = new StringSubstitutor(map).replace(BASE_URL);
        return new URI(uri);
    }

    private List<BotCommand> map(List<Command> commands) {
        return commands.stream()
                .map(command -> command.map(DEFAULT_LANGUAGE))
                .toList();
    }
}
