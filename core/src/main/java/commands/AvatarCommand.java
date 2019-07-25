package commands;

import Service.ContactService;

import Service.ServiceFactory;
import commands.exception.CommandException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Properties;


public class AvatarCommand implements Command{
    private Logger logger = LogManager.getLogger(AvatarCommand.class);
    private ContactService contactService = ServiceFactory.getContactService();
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response){

        response.setCharacterEncoding("UTF-8");
        Properties properties = new Properties();
        try {
            properties.load(AvatarCommand.class.getResourceAsStream("/avatars.properties"));
        } catch (IOException e) {
            throw  new CommandException("Error while getting attachment",e);
        }
        String s = request.getParameter("id");
        Long idContact = null;

        if(!s.isEmpty())
           idContact = Long.parseLong(s);

        String savePath = properties.getProperty("SAVE_AVATARS_PATH");
        File file = null;

        if(idContact==null){

            file = new File(properties.getProperty("DEFAULT_AVATAR"));
        }else{
            savePath += File.separator + idContact + "_" + "avatar";
            file=new File(savePath);
            if(! file.exists()) {
                file = new File(properties.getProperty("DEFAULT_AVATAR"));
            }
        }



        int buffSize = Integer.parseInt(properties.getProperty("BUFFER_SIZE"));

        response.reset();
        response.setBufferSize(buffSize);
        response.setContentType(properties.getProperty("CONTENT_TYPE"));
        response.setHeader("Content-Length", String.valueOf(file.length()));
        response.setHeader("Content-Disposition", "avatar; filename=\"" + file.getName() + "\"");
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
        logger.info("Rendoring avatar");
    }
}
