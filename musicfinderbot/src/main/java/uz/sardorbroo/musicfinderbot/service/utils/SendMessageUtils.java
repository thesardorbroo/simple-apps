package uz.sardorbroo.musicfinderbot.service.utils;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.ResourceBundle;

public class SendMessageUtils {
    private static final String WRONG_VALUE_KEY = "wrong.value";

    public static SendMessage wrongValue(User user) {

        ResourceBundle bundle = ResourceBundleUtils.getBundle(user.getLanguageCode());

        String message = bundle.getString(WRONG_VALUE_KEY);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(user.getId());
        sendMessage.setText(message);

        return sendMessage;
    }
}
