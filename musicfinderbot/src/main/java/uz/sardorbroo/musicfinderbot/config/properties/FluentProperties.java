package uz.sardorbroo.musicfinderbot.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import uz.sardorbroo.musicfinderbot.enumeration.Command;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "fluent.bot")
public class FluentProperties {
    private String token;
    private String username;
    private String processType;
    private List<Command> commands = new ArrayList<>();
}
