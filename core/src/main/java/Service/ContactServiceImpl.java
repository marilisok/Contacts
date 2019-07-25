package Service;

import dao1.ContactDao;
import dao1.DaoFactory;
import model.*;
import transaction.TransactionManager;

import javax.naming.NamingException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ContactServiceImpl implements ContactService{
    private ContactDao contactDao;
    public ContactServiceImpl() {
        this.contactDao = DaoFactory.getContactDao();
    }

    @Override
    public long countContacts() {
        return contactDao.countContacts();
    }

    @Override
    public List<Contact> getContacts(int begin, int end) {
        return contactDao.getContacts(begin, end);
    }

    @Override
    public List<Contact> getShowContacts(SearchCriteria criteria, Settings settings) {
        return contactDao.getToShowContacts(criteria,settings);
    }

    @Override
    public void deleteAttaches(List<Long> delAttaches) {
        contactDao.deleteAttaches(delAttaches);
    }

    @Override
    public void deleteContact(Long id) {
        contactDao.deleteContact(id);
    }

    @Override
    public void updateAttach(List<Attachments> attachments) {
        contactDao.updateAttach(attachments);
    }

    @Override
    public long setContact(Contact contact) {
        long id = contactDao.setContact(contact);
        return id;
    }

    @Override
    public void savePhones(List<Phone> phones) {
        contactDao.setPhones(phones);
    }

    @Override
    public Contact getContact(long id) throws NamingException {
        return contactDao.getById(id);
    }

    @Override
    public List<Phone> getPhones(long id) {
        return contactDao.getPhones(id);
    }

    @Override
    public long countContacts(SearchCriteria criteria) {
        return contactDao.countContacts(criteria);
    }


    @Override
    public List<Attachments> getAttaches(long idContact) {
        return contactDao.getAttaches(idContact);
    }


    @Override
    public void saveAttaches(long idContact, List<Attachments> attachesList) throws IOException {
         contactDao.setAttaches(idContact, attachesList);



    }

    @Override
    public void deletePhones(List<Long> delPhones) {
        contactDao.deletePhones(delPhones);
    }

    @Override
    public List<Contact> getBirthdayContacts() {
        return contactDao.forBirthdayContacts();
    }

}
