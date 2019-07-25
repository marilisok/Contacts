package commands.util;

import commands.AvatarCommand;
import commands.exception.CommandException;
import model.Attachments;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class FilesUtil {
    public static void deleteOnPath(Attachments a){
        Properties properties = new Properties();
        try {
            properties.load(AvatarCommand.class.getResourceAsStream("/attach.properties"));
        } catch (IOException e) {
            throw  new CommandException("Error while getting attachment",e);
        }
        String attachmentsDirectory = properties.getProperty("SAVE_ATTACH_PATH");
        String filePath = attachmentsDirectory + File.separator + a.getIdContact() + "_"+ a.getRandomFileName();
        File file = new File(filePath);
        if(file.exists()){
            file.delete();
        }
    }
}
