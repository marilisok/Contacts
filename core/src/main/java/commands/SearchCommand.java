package commands;

import Service.ContactService;
import Service.ServiceFactory;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import commands.exception.CommandException;
import commands.util.GeneralUtil;
import model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SearchCommand implements Command{
    private Logger logger = LogManager.getLogger(SearchCommand.class);
    private ContactService contactService = ServiceFactory.getContactService();
    private SearchCriteria criteria = new SearchCriteria();
    private Settings settings;
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        criteria =  getSearchCriteria(request);
        long total = contactService.countContacts(criteria);
        int page = Integer.parseInt(request.getParameter("page"));
        int limit = Integer.parseInt(request.getParameter("limit"));
        settings =  getViewSettings(page, limit);
        List<Contact> contacts= contactService.getShowContacts(criteria,settings);
        long pages = settings.countPages(total);
        ContactsCount searchContacts = new ContactsCount(contacts, pages);
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        objectMapper.setDateFormat(df);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        String json = null;
        try {
            json = objectMapper.writeValueAsString(searchContacts);
            logger.info("Contacts were finding");
            response.getWriter().write(json);
        } catch (IOException e) {
            throw new CommandException("ERROR while finding contacts"+e.getMessage());
        }

    }
    private SearchCriteria getSearchCriteria(HttpServletRequest request) {
        SearchCriteria c = new SearchCriteria();
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode j = mapper.readTree(request.getReader());
            c.setFirstName(j.get("contact").get("name").asText());
            c.setLastName(j.get("contact").get("surname").asText());
            c.setPatronymic(j.get("contact").get("patronymic").asText());

            String gender = j.get("contact").get("gender").asText();
            c.setGender(!(gender.equals("NONE"))? Gender.valueOf(gender.toUpperCase()): null);
            String maritalStatus = j.get("contact").get("marital").asText();
            c.setMaritalStatus(!(maritalStatus.equals("NONE"))? MaritalStatus.valueOf(maritalStatus.toUpperCase()):null);
            c.setCitizenship(j.get("contact").get("citizenship").asText());
            String date = j.get("contact").get("birthdayFrom").asText();
            Date d =  GeneralUtil.stringToDate(date);
            c.setBirthdayFrom(d);
            c.setBirthdayTO(GeneralUtil.stringToDate(j.get("contact").get("birthdayTo").asText()));
            c.setCountry(j.get("contact").get("country").asText());
            c.setCity(j.get("contact").get("city").asText());
            c.setHouseNumber(j.get("contact").get("houseNumber").asText());
            c.setStreet(j.get("contact").get("street").asText());
            c.setFlat(j.get("contact").get("apartmentNumber").asText());
            c.setZipcode(j.get("contact").get("zipCode").asText());

        } catch (IOException e) {
            throw new CommandException("ERROR while parsing aearch criteria "+e.getMessage());
        }
        return  c;
    }
    private Settings getViewSettings(int page, int limit) {
        Settings settings = new Settings();
        settings.setPageNum(page);
        settings.setCount(limit);
        return  settings;
    }
}
