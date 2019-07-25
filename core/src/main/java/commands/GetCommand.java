package commands;

import Service.ContactService;
import Service.ServiceFactory;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import commands.exception.CommandException;
import model.Contact;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class GetCommand implements Command{
    private ContactService contactService = ServiceFactory.getContactService();
    private Logger logger = LogManager.getLogger(GetCommand.class);
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
       Contact c = null;
        try {
            Long id = Long.parseLong(request.getParameter("id"));
            c = contactService.getContact(id);
            c.setPhones(contactService.getPhones(id));
            c.setAttachments(contactService.getAttaches(id));
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            objectMapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS , false);
            final String json = objectMapper.writeValueAsString(c);
            logger.info("Getting of contact with id="+id);
            response.getWriter().write(json);
        } catch (NamingException e) {
            throw new CommandException("Error while getting contact", e);
        } catch (JsonProcessingException e) {
            throw new CommandException("Error while getting contact", e);
        } catch (IOException e) {
            throw new CommandException("Error while getting contact", e);
        }

    }
}
