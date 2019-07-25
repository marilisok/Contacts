package commands;

import commands.exception.CommandException;

import javax.servlet.http.HttpServletRequest;

public class CommandFactory {
    public Command defineCommand(HttpServletRequest request) {
        Command current = null;
        String action = request.getParameter("command");

        try {
            CommandEnum currentEnum = CommandEnum.valueOf(action.toUpperCase());
            current = currentEnum.getCurrentCommand();
        } catch (IllegalArgumentException e) {
            throw new CommandException("Error in CommandFactory",e);
        }
        return current;
    }
}
