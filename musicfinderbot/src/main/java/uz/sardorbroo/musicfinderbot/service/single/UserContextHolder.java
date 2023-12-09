package uz.sardorbroo.musicfinderbot.service.single;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class UserContextHolder {

    private final HashMap<String, User> context = new HashMap<>();

    private UserContextHolder() {
    }

    public Map<String, User> getContext() {
        return this.context;
    }

    public void save(User user) {

        isValid(user);

        context.put(user.getUserName(), user);
    }

    public Optional<User> get(String username) {

        if (StringUtils.isBlank(username)) {
            log.warn("Invalid argument is passed! Username must not be empty!");
            return Optional.empty();
        }

        User user = context.getOrDefault(username, null);

        return Objects.isNull(user) ? Optional.empty() : Optional.of(user);
    }

    public boolean hasUser(String username) {

        if (StringUtils.isBlank(username)) {
            log.warn("Invalid argument is passed! Username must not be empty!");
            return false;
        }

        return context.containsKey(username);
    }

    private void isValid(User user) { // Common validation

        if (Objects.isNull(user) || StringUtils.isBlank(user.getUserName())) {
            log.warn("Invalid argument is passed! User.Username must not be empty!");
            throw new IllegalArgumentException("Invalid argument is passed! User.Username must not be empty!");
        }
    }

}
