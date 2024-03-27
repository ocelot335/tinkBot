package edu.java.bot.services.commands;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.services.ICommand;
import org.springframework.stereotype.Component;

@Component
public class HelpCommand implements ICommand {
    private ICommand[] commands;

    public HelpCommand(ICommand[] commands) {
        this.commands = commands;
    }

    @Override
    public String getName() {
        return "/help";
    }

    @Override
    public String getDescription() {
        return "Вывести окно с командами";
    }

    @Override
    public String processCommand(Update update) {
        StringBuilder response = new StringBuilder();
        for (ICommand command : commands) {
            response.append(command.getName()).append(" - ").append(command.getDescription()).append("\n");
        }
        response.trimToSize();
        return response.toString();
    }
}
