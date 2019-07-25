package dao1;

import model.*;

import javax.naming.NamingException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public interface ContactDao {
    void updateAttach(List<Attachments> att);
    List<Contact> getContacts(int begin, int end);
    Contact getById(Long contactId) throws NamingException;
    long setContact(Contact contact);
    void deleteContact(Long contactId);
    void insertPhone(Phone phone);
    void setPhones(List<Phone> phones);
    List<Phone> getPhones(Long idContact);
    List<Attachments> getAttaches(Long idContact);
    void insertAttach(Attachments attach);
    void updatePhone(Phone phone);
    void setAttaches(long idContact, List<Attachments> attaches);
    List<Contact> forBirthdayContacts();
    List<Contact> getToShowContacts(SearchCriteria criteria, Settings settings);
    long countContacts(SearchCriteria criteria);
    void deletePhones(List<Long> delPhones);
    void deleteAttaches(List<Long> delAttaches);
    long countContacts();
}
