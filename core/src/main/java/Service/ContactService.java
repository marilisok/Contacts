package Service;

import model.*;

import javax.naming.NamingException;
import java.io.IOException;
import java.util.List;

public interface ContactService {
    long countContacts();
    List<Contact> getContacts(int begin, int end);
    List<Contact> getShowContacts(SearchCriteria criteria, Settings settings);
    void deleteAttaches(List<Long> delAttaches);
    void deleteContact(Long id);
    void updateAttach(List<Attachments> attachments);

    long setContact(Contact contact);

    void savePhones(List<Phone> phones);

    Contact getContact(long id) throws NamingException;

    List<Phone> getPhones(long id);

    long countContacts(SearchCriteria criteria);


    List<Attachments> getAttaches(long idContact);

    void saveAttaches(long idContact, List<Attachments> attachesList) throws IOException;

    void deletePhones(List<Long> delPhones);

    List<Contact> getBirthdayContacts();
}
