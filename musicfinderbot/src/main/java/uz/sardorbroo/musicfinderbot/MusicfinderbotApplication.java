package uz.sardorbroo.musicfinderbot;

import org.khasanof.state.StateConfigurerAdapter;
import org.khasanof.state.configurer.StateConfigurer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uz.sardorbroo.musicfinderbot.state.BotState;

import java.util.EnumSet;

@SpringBootApplication
public class MusicfinderbotApplication implements StateConfigurerAdapter<BotState> {

    public static void main(String[] args) {
        SpringApplication.run(MusicfinderbotApplication.class, args);
    }

    @Override
    public void configure(StateConfigurer<BotState> state) {
        state.initial(BotState.START)
                .states(EnumSet.allOf(BotState.class));
    }
}
