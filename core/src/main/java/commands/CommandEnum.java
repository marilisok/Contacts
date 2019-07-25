package commands;

public enum CommandEnum {
    SAVE{
        {
            this.command = new SaveCommand();
        }
    },

    AVATAR{
        {
            this.command = new AvatarCommand();
        }
    },

    DELETE{
        {
            this.command = new DeleteCommand();
        }
    },

    SETATTACH{
        {
            this.command = new SetAttachCommand();
        }
    },

    SEARCH{
        {
            this.command = new SearchCommand();
        }
    },

    ATTACH{
        {
            this.command = new AttachCommand();
        }
    },
    EMAIL{
        {
            this.command = new EmailCommand();
        }
    },

    GET{
        {
            this.command = new GetCommand();
        }
    },
    GETCONTACTS{
            {
            this.command = new GetContactsCommand();
            }
    },
    SETTEMPLATE{
        {
            this.command = new SetTemplate();
        }
    };

    Command command;
    public Command getCurrentCommand() {
        return command;
    }
}
