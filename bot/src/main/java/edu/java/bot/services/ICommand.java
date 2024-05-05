package edu.java.bot.services;

import com.pengrad.telegrambot.model.Update;

public interface ICommand {
    String SCRAPPER_ERROR = "Извините, в данный момент бот не работает";

    String getName();

    String getDescription();

    String processCommand(Update update);
}
