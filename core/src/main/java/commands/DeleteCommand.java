package commands;

import Service.ContactService;
import Service.ServiceFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import commands.exception.CommandException;
import commands.util.FilesUtil;
import model.Attachments;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.Properties;

public class DeleteCommand implements Command{
    private Logger logger = LogManager.getLogger(DeleteCommand.class);
    private ContactService contactService = ServiceFactory.getContactService();
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode j = objectMapper.readTree(request.getReader());
            String arr= String.valueOf(j.get("index"));
            String[] chosen = arr.replace("[", "").replace("]", "").replaceAll("\"", "").split(",");
            for(String item: chosen) {
                long idContact = Long.parseLong(item);
                deletePhoto(idContact);
                deleteAttaches(idContact);
                contactService.deleteContact(idContact);
            }
            logger.info("Deleting contact with id="+ arr);
        } catch (IOException e) {
            throw  new CommandException("Error while deleting contact",e);
        }



    }
    private void deletePhoto(long idContact) {
        Properties properties = new Properties();
        try {
            properties.load(AvatarCommand.class.getResourceAsStream("/avatars.properties"));
        } catch (IOException e) {
            throw  new CommandException("Error while getting attachment",e);
        }
        String attachmentsDirectory = properties.getProperty("SAVE_AVATARS_PATH");
        String filePath = attachmentsDirectory + File.separator + idContact + "_"+ "avatar";
        File file = new File(filePath);
        if(file.exists()){
            file.delete();
        }
    }
    private void deleteAttaches(long idContact) {
        List<Attachments> attaches = contactService.getAttaches(idContact);
        for(Attachments attach:attaches) {
            FilesUtil.deleteOnPath(attach);
        }
    }
}
