package model;

import java.util.List;

public class ContactsCount {
    private List<Contact> contacts;
    long count;

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public ContactsCount(List<Contact> contacts, long count) {
        this.contacts = contacts;
        this.count = count;
    }
}
