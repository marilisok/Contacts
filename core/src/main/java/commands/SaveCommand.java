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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SaveCommand implements Command{
    private Logger logger = LogManager.getLogger(SaveCommand.class);
    private ContactService contactService = ServiceFactory.getContactService();
    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) {
        ObjectMapper mapper = new ObjectMapper();
        Long contactId = null;
        if(Long.valueOf(req.getParameter("contactId"))!=0){
             contactId = Long.valueOf(req.getParameter("contactId"));
        }

        Contact c = new Contact();
        Address add = new Address();
        try {
            JsonNode j = mapper.readTree(req.getReader());
            c.setContactId(contactId);
            c.setFirstName(j.get("contact").get("name").asText());
            c.setLastName(j.get("contact").get("surname").asText());
            c.setPatronymic(j.get("contact").get("patronymic").asText());
            String date = j.get("contact").get("birth").asText();
            java.sql.Date d = GeneralUtil.stringToDate(date);
            c.setBirthday(d);
            String gender = j.get("contact").get("gender").asText();
            c.setGender(!(gender.equals("NONE"))? Gender.valueOf(gender.toUpperCase()): null);
            String maritalStatus = j.get("contact").get("marital").asText();
            c.setMaritalStatus(!(maritalStatus.equals("NONE"))?MaritalStatus.valueOf(maritalStatus.toUpperCase()):null);
            c.setCompany(j.get("contact").get("company").asText());
            c.setEmail(j.get("contact").get("email").asText());
            c.setWebsite(j.get("contact").get("website").asText());
            c.setCitizenship(j.get("contact").get("citizenship").asText());

            String a = j.get("contact").get("country").asText();
            add.setCountry(j.get("contact").get("country").asText());
            add.setCity(j.get("contact").get("city").asText());
            add.setHouseNumber(j.get("contact").get("houseNumber").asText());
            add.setStreet(j.get("contact").get("street").asText());
            add.setFlat(j.get("contact").get("apartmentNumber").asText());
            add.setZipcode(j.get("contact").get("zipCode").asText());

            add.setAddressId(j.get("contact").get("addressId").asLong());
            c.setAddress(add);
            contactId=contactService.setContact(c);
            c.setContactId(contactId);
            JsonNode actionsPhones = j.get("actionsPhones");
            if(actionsPhones != null){
                JsonNode delPhone = actionsPhones.get("del");
                JsonNode addPhone = actionsPhones.get("add");
                JsonNode editPhone = actionsPhones.get("edit");
                List<Long> delIndex = new ArrayList<>();
                List<JsonNode> editPh = new ArrayList<>();
                List<JsonNode> addPh = new ArrayList<>();
                if(delPhone.isArray()){
                    for (JsonNode jsonNode: delPhone){
                        delIndex.add(jsonNode.asLong());
                    }
                }
                if(editPhone.isArray()){
                    for (JsonNode jsonNode: editPhone){
                        editPh.add(jsonNode);
                    }
                }
                if(addPhone.isArray()){
                    for (JsonNode jsonNode: addPhone){
                        addPh.add(jsonNode);
                    }
                }
                List<Phone> newPhone = new ArrayList<>();
                newPhone.addAll(setPhones(addPh, contactId));
                newPhone.addAll(setPhones(editPh, contactId));
                if(newPhone.size()!=0){
                    contactService.savePhones(newPhone);
                }
                if(delIndex.size()!=0){
                    contactService.deletePhones(delIndex);
                }

            }
            ObjectMapper objectMapper = new ObjectMapper();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            objectMapper.setDateFormat(df);
            objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            final String json = objectMapper.writeValueAsString(c);
            logger.info("Saving contact with full name:"+c.getFirstName()+" "+c.getLastName());
            resp.getWriter().write(json);
        } catch (IOException e) {
            throw new CommandException("Error while saving contact", e);
        }

    }
    private List<Phone> setPhones(List<JsonNode> addPh, Long contactId){
        List<Phone> newPhones = new ArrayList<>();
        for(JsonNode j: addPh){
            Phone phone = new Phone();
            JsonNode id = j.get("phoneId");
            phone.setPhoneId(id!=null? id.asLong():null);
            String type = j.get("kind").asText();
            phone.setTypePhone(!(type.equals(""))?TypePhone.valueOf(type.toUpperCase()):null);
            phone.setIdContact(contactId);

            phone.setComment(j.get("comment").asText());
            phone.setCountryCode(j.get("countryCode").asInt());
            phone.setOperatorCode(j.get("operatorCode").asInt());
            phone.setNumber(j.get("phone").asText());
            newPhones.add(phone);

        }
        return  newPhones;
    }
}
