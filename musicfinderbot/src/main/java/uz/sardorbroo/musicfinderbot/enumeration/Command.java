package uz.sardorbroo.musicfinderbot.enumeration;

import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import uz.sardorbroo.musicfinderbot.service.utils.ResourceBundleUtils;

import java.util.ResourceBundle;

@Getter
public enum Command {
    START("/start", "command.start.description"),
    SONG("/song", "command.song.description"),
    ARTIST("/artist", "command.artist.description"),
    TOP_HIT("/top_hit", "command.top.hit.description"),
    UZ("/uz", "command.uz.description"),
    RU("/ru", "command.ru.description"),
    EN("/en", "command.en.description"),
    HELP("/help", "command.help.description");

    private final String text;
    private final String key4Description;

    Command(String text, String key4Description) {
        this.text = text;
        this.key4Description = key4Description;
    }

    // Discuss: if we need move it to another mapper class
    public BotCommand map(Language language) {

        ResourceBundle bundle = ResourceBundleUtils.getBundle(String.valueOf(language));
        String description = bundle.getString(this.getKey4Description());

        BotCommand botCommand = new BotCommand();

        botCommand.setCommand(this.getText());
        botCommand.setDescription(description);

        return botCommand;
    }
}
