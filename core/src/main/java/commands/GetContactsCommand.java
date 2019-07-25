package commands;

import Service.ContactService;
import Service.ServiceFactory;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import commands.exception.CommandException;
import model.Contact;
import model.ContactsCount;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class GetContactsCommand implements Command{
    private Logger logger = LogManager.getLogger(GetContactsCommand.class);
    private ContactService contactService = ServiceFactory.getContactService();
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        ContactsCount contacts = null;
        List<Contact> contactList = null;
        long count;
        try {
            int page = Integer.parseInt(request.getParameter("page"));
            int limit = Integer.parseInt(request.getParameter("limit"));
            int begin = (page-1)*limit;


            contactList = contactService.getContacts(begin, limit);
            count = contactService.countContacts();
            long pages = count%limit==0? count/limit: count/limit+1;
            contacts = new ContactsCount(contactList, pages);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            objectMapper.setDateFormat(df);
            String json = objectMapper.writeValueAsString(contacts);
            logger.info("Getting of contacts");
            response.getWriter().write(json);
        } catch (JsonProcessingException e) {
            throw new CommandException("Error while getting contacts", e);
        } catch (IOException e) {
            throw new CommandException("Error while getting contacts", e);
        }

    }
}
