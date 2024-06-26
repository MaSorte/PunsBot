package io.proj3ct.telegramjokebot;

import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@AutoConfigurationPackage
public class TelegramBotApplication extends SpringBootServletInitializer {


    public static void main(String[] args) {
        ConfigurableApplicationContext run = new SpringApplicationBuilder(TelegramBotApplication.class).run(args);
    }
}

