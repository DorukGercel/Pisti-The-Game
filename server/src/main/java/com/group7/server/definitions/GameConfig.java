package com.group7.server.definitions;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/** Configuration class of the prototype scoped game bean*/
@Configuration
public class GameConfig {

    @Bean(name = "Game")
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Game game(final CardTable cardTable){
        return new Game(cardTable);
    }
}
