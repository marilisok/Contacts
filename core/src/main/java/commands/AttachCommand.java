package commands;

import Service.ContactService;
import Service.ServiceFactory;
import commands.exception.CommandException;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Properties;


public class AttachCommand implements Command{
    private Logger logger = LogManager.getLogger(AttachCommand.class);
    private ContactService contactService = ServiceFactory.getContactService();
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {

        Properties properties = new Properties();
        try {
            properties.load(AvatarCommand.class.getResourceAsStream("/attach.properties"));

        } catch (IOException e) {
            throw  new CommandException("Error while getting attachment",e);
        }
        String s = request.getParameter("id");
        Long idContact = Long.parseLong(s);
        String savePath = properties.getProperty("SAVE_ATTACH_PATH");
        String fileName = request.getParameter("filename");
        String realFileName = request.getParameter("realFileName");
        savePath += File.separator + idContact + "_" + fileName;
        File file = new File(savePath);


        if(! file.exists()) {

            return;
        }
        int buffSize = Integer.parseInt(properties.getProperty("BUFFER_SIZE"));

        response.reset();
        response.setCharacterEncoding("UTF-8");
        response.setBufferSize(buffSize);
        response.setContentType(properties.getProperty("CONTENT_TYPE"));
        response.setHeader("Content-Length", String.valueOf(file.length()));

        response.setHeader("Content-Disposition", "attachment; filename=\"" + realFileName + "\"");
        try (OutputStream out = response.getOutputStream();
             FileInputStream in = new FileInputStream(file)) {
            byte[] buffer = new byte[buffSize];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);

            }
        } catch (IOException e) {
            throw  new CommandException("Error while getting attachment",e);
        }
        logger.info("Downloading attachment");
    }
}
