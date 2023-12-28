package uz.sardorbroo.musicfinderbot.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uz.sardorbroo.musicfinderbot.config.properties.FluentProperties;
import uz.sardorbroo.musicfinderbot.service.integration.BotClientService;

@Slf4j
@Component
public class CommandConfig {

    private final BotClientService client;

    private final FluentProperties properties;

    public CommandConfig(@Qualifier("REST_TEMPLATE") BotClientService client, FluentProperties properties) {
        this.client = client;
        this.properties = properties;
    }

    @PostConstruct
    public void setCommands() {
        log.debug("Start settings selected commands");

        client.deleteCommands();

        client.setCommands(properties.getCommands());
        log.debug("Commands are set successfully");
    }
}
