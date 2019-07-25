package commands.exception;

public class CommandException extends  RuntimeException{
    public CommandException(Exception e) {
        super(e);
    }

    public CommandException(String message, Exception e) {
        super(message, e);
    }

    public CommandException(String message) {
        super(message);
    }
}
