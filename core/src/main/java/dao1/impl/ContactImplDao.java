package dao1.impl;

import dao1.ContactDao;
import dao1.DaoException;
import model.*;
import transaction.TransactionManager;

import javax.naming.NamingException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContactImplDao implements ContactDao {
    private final static ContactImplDao instance = new ContactImplDao();
    private ContactImplDao() {}
    public static ContactImplDao getInstance(){
        return instance;
    }

    @Override
    public void updateAttach(List<Attachments> att) {
        try (Connection connection = TransactionManager.createConnection()){
            for (Attachments attachments: att){
                PreparedStatement statement = connection.prepareStatement("UPDATE DBCont.attachments  SET filename=?," +
                        "  comment=? where attachmentsId=?");
                statement.setLong(3, attachments.getAttachmentsId());
                statement.setString(2, attachments.getComment());
                statement.setString(1, attachments.getFileName());
                statement.execute();
            }
        } catch (SQLException | NamingException e) {
            throw new DaoException("Error while inserting phone of contact", e);
        }
    }

    @Override
    public List<Contact> getContacts(int begin, int end) {
        List<Contact> contacts = new ArrayList<>();
        try (Connection connection = TransactionManager.createConnection();
             PreparedStatement statement = connection.prepareStatement("select * from DBCont.contact limit ?, ?")){
            statement.setInt(1, begin);
            statement.setInt(2, end);
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    String s = set.getString("contactId");
                    Contact c = getById(set.getLong("contactId"));

                    contacts.add(c);
                }
            }
        }catch (SQLException e) {
            throw new DaoException("Error while getting contacts", e);
        } catch (NamingException e) {
            throw new DaoException("Error while getting contacts", e);
        }
        return contacts;
    }


    @Override
    public Contact getById(Long contactId) throws NamingException {
        Contact contact = new Contact();

        try (Connection connection = TransactionManager.createConnection();
             PreparedStatement statement = connection.prepareStatement("select * from DBCont.contact  as c" +
                     " left join DBCont.address as a ON c.addressId=a.addressId WHERE contactId = ?")) {
            statement.setString(1, Long.toString(contactId));
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {

                    contact.setContactId(contactId);
                    contact.setLastName(set.getString("lastName"));
                    contact.setFirstName(set.getString("firstName"));
                    contact.setPatronymic(set.getString("patronymic"));
                    Date d = set.getDate("birthday");
                    contact.setBirthday(d);
                    String gender = set.getString("gender");
                    Gender genderVal = (gender != null) ? Gender.valueOf(gender.toUpperCase()) : null;
                    contact.setGender(genderVal);

                    contact.setCitizenship(set.getString("citizenship"));
                    String maritalStatus = set.getString("maritalStatus");
                    MaritalStatus value = (maritalStatus != null) ? MaritalStatus.valueOf(maritalStatus) : null;
                    contact.setMaritalStatus(value);
                    contact.setWebsite(set.getString("website"));
                    contact.setEmail(set.getString("email"));
                    contact.setCompany(set.getString("company"));
                    contact.setPhoto(set.getString("photo"));

                    //Getting address
                    Address address = new Address();

                    address.setAddressId(set.getLong("addressId"));

                    address.setCountry(set.getString("country"));
                    address.setCity(set.getString("city"));
                    address.setStreet(set.getString("street"));
                    address.setHouseNumber(set.getString("houseNumber"));
                    address.setFlat(set.getString("flat"));
                    address.setZipcode(set.getString("zipcode"));
                    contact.setAddress(address);


                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error while getting contacts by ID", e);
        }
        return contact;
    }

    @Override
    public long setContact(Contact contact) {
        Long contactId = contact.getContactId();
        if(contactId != null){
            updateContact(contact);
            return contactId;
        }
        return insertContact(contact);
    }

    @Override
    public void deleteContact(Long contactId){
        deleteOnStatement("delete from contact where contactId = ?",contactId);
    }

    @Override
    public void insertPhone(Phone phone) {
        try (Connection connection = TransactionManager.createConnection()){
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO DBCont.phone(countryCode," +
                    "operatorCode,number , typePhone, comment,contactId ) VALUES (?,?,?,?,?,?)")) {
                statement.setInt(1,phone.getCountryCode());
                statement.setInt(2,phone.getOperatorCode());
                statement.setString(3,phone.getNumber());
                statement.setString(4, phone.getTypePhone()!=null? phone.getTypePhone().name(): null);
                statement.setString(5,phone.getComment());
                statement.setLong(6,phone.getIdContact());

                statement.executeUpdate();
            }
        } catch (SQLException | NamingException e) {
            throw new DaoException("Error while inserting phone of contact", e);
        }
    }
    @Override
    public void updatePhone(Phone phone) {
        try (Connection connection = TransactionManager.createConnection()){
            try (PreparedStatement statement = connection.prepareStatement("UPDATE DBCont.phone  SET countryCode=?, " +
                    "operatorCode=?,number=? , typePhone=?, comment=?,contactId=? where phoneId=?")) {

                statement.setInt(1,phone.getCountryCode());
                statement.setInt(2,phone.getOperatorCode());
                statement.setString(3,phone.getNumber());
                statement.setString(4,phone.getTypePhone()!=null? phone.getTypePhone().name(): null);
                statement.setString(5,phone.getComment());
                statement.setLong(6,phone.getIdContact());
                statement.setLong(7, phone.getPhoneId());

                statement.executeUpdate();
            }
        } catch (SQLException | NamingException e) {
            throw new DaoException("Error while inserting phone of contact", e);
        }
    }
    @Override
    public void setPhones(List<Phone> phones) {
        for(Phone phone : phones) {
            if(phone.getPhoneId()==null)
                insertPhone(phone);
            else
                updatePhone(phone);
        }
    }

    @Override
    public List<Phone> getPhones(Long idContact) {
        List<Phone> list = new ArrayList<>();
        try (Connection connection = TransactionManager.createConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM DBCont.phone " +
                     "WHERE contactId = ?")) {
            statement.setString(1, Long.toString(idContact));
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    Phone phone = new Phone();
                    phone.setComment(set.getString("comment"));
                    phone.setNumber(set.getString("number"));
                    phone.setCountryCode(set.getInt("countryCode"));
                    phone.setOperatorCode(set.getInt("operatorCode"));
                    String type = set.getString("typePhone");
                    TypePhone typePhone = (type!=null)?TypePhone.valueOf(type):null;
                    phone.setTypePhone(typePhone);
                    phone.setIdContact(idContact);
                    phone.setPhoneId(set.getLong("phoneId"));
                    list.add(phone);
                }
            }
        } catch (SQLException | NamingException e) {
            throw new DaoException("Error while getting phones of contact", e);
        }
        return list;
    }

    @Override
    public List<Attachments> getAttaches(Long idContact) {
        List<Attachments> attachments = new ArrayList<>();
        try(Connection connection = TransactionManager.createConnection();
            PreparedStatement statement=connection.prepareStatement("SELECT * FROM  DBCont.attachments " +
                    "WHERE contactId = ?")){
            statement.setLong(1, idContact);
            try(ResultSet set = statement.executeQuery()){
                while(set.next()){
                    Attachments attach = new Attachments();
                    attach.setAttachmentsId(set.getLong("attachmentsId"));
                    attach.setComment(set.getString("comment"));
                    attach.setDateOfLoad(set.getDate("dateOfLoad"));
                    attach.setIdContact(idContact);
                    attach.setFileName(set.getString("filename"));
                    attach.setRandomFileName(set.getString("randomFileName"));
                    attachments.add(attach);
                }
            }

        }catch(SQLException | NamingException e){
            throw new DaoException("Error while getting attachment of contact", e);
        }
        return attachments;

    }

    @Override
    public void insertAttach(Attachments attach) {
        try(Connection connection = TransactionManager.createConnection();
            PreparedStatement statement = connection.prepareStatement("insert into DBCont.attachments" +
                    "(filename, dateOfLoad, comment, contactId, randomFileName) values (?, ?, ?, ?, ?)")){
            statement.setString(1, attach.getFileName());
            statement.setDate(2, (Date) attach.getDateOfLoad());
            statement.setString(3, attach.getComment());
            statement.setLong(4, attach.getIdContact());
            statement.setString(5, attach.getRandomFileName());
            statement.executeUpdate();

        }catch (SQLException | NamingException e){
            throw new DaoException("Error while inserting contact's attachment", e);
        }
    }



    @Override
    public void setAttaches(long idContact, List<Attachments> attaches) {
        for (Attachments attachments: attaches){
            insertAttach(attachments);
        }
    }

    @Override
    public List<Contact> forBirthdayContacts() {
        List<Contact> list = new ArrayList<>();
        try (Connection connection = TransactionManager.createConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT firstName, lastName,  birthday" +
                     " FROM DBCont.contact")) {
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    Contact contact = new Contact();
                    contact.setFirstName(set.getString("firstName"));
                    contact.setLastName(set.getString("lastName"));
                    contact.setBirthday(set.getDate("birthday"));

                    list.add(contact);
                }
            }
        } catch (SQLException | NamingException e) {
            throw new DaoException("Error while getting birthday contacts", e);
        }
        return list;

    }

    @Override
    public List<Contact> getToShowContacts(SearchCriteria criteria, Settings settings) {
        List<String> paramStrList = new ArrayList<>();
        List<Date> paramDateList = new ArrayList<>();
        List<Contact> list = new ArrayList<>();
        String query = "select * from DBCont.contact  as c left join DBCont.address as a ON c.addressId=a.addressId";
        query = makeSelectQuery(query,criteria, paramStrList, paramDateList);
        query +=" LIMIT ? , ?";
        try (Connection connection = TransactionManager.createConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            int index = 1;
            for(String param: paramStrList){
                statement.setString(index, param);
                index ++;
            }
            for(Date date: paramDateList){
                statement.setDate(index, date);
                index ++;
            }

            statement.setLong(index,(settings.getPageNum()-1)*settings.getCount());
            statement.setLong(index + 1,settings.getCount());
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    Contact contact = new Contact();
                    contact.setContactId(set.getLong("contactId"));
                    contact.setLastName(set.getString("firstName"));
                    contact.setFirstName(set.getString("lastName"));
                    contact.setPatronymic(set.getString("patronymic"));
                    contact.setCompany(set.getString("company"));
                    contact.setBirthday(set.getDate("birthday"));
                    contact.setEmail(set.getString("email"));

                    Address address = new Address();
                    address.setCountry(set.getString("country"));
                    address.setCity(set.getString("city"));
                    address.setAddressId(set.getLong("addressId"));
                    address.setZipcode(set.getString("zipcode"));
                    address.setFlat(set.getString("flat"));
                    address.setHouseNumber(set.getString("houseNumber"));
                    address.setStreet(set.getString("street"));
                    contact.setAddress(address);

                    list.add(contact);
                }
            }
        } catch (SQLException | NamingException e) {
            throw new DaoException("Error while getting contacts for showing", e);
        }
        return list;
    }


    @Override
    public long countContacts(SearchCriteria criteria){
        long total = 0;
        List<String> paramStrList = new ArrayList<>();
        List<Date> paramDateList = new ArrayList<>();
        String query = "SELECT COUNT(*) AS total from Contact AS c LEFT JOIN Address AS a ON c.addressId = a.addressId";
        query = makeSelectQuery(query,criteria, paramStrList, paramDateList);
        try (Connection connection = TransactionManager.createConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            int index = 1;
            for(String param: paramStrList){
                statement.setString(index, param);
                index ++;
            }
            for(Date date: paramDateList){
                statement.setDate(index, date);
                index ++;
            }
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    total = set.getLong("total");
                }
            }
        } catch (SQLException | NamingException e) {
            throw new DaoException("Error while counting contacts", e);
        }
        return total;
    }

    @Override
    public void deletePhones(List<Long> delPhones) {
        try (Connection connection = TransactionManager.createConnection()){
            for (int i=0; i<delPhones.size(); i++){
                PreparedStatement statement = connection.prepareStatement("delete from DBCont.phone where phoneId = ?");
                statement.setLong(1, delPhones.get(i));
                statement.execute();
            }
        } catch (SQLException | NamingException e) {
            throw new DaoException("Error while deleting of phones or attaches", e);
        }
    }

    @Override
    public void deleteAttaches(List<Long> delAttaches) {
        try (Connection connection = TransactionManager.createConnection()){
            for (int i=0; i<delAttaches.size(); i++){
                PreparedStatement statement = connection.prepareStatement("delete from DBCont.attachments where attachmentsId = ?");
                statement.setLong(1, delAttaches.get(i));
                statement.execute();
            }
        } catch (SQLException | NamingException e) {
            throw new DaoException("Error while deleting of phones or attaches", e);
        }
    }

    @Override
    public long countContacts() {
        try (Connection connection = TransactionManager.createConnection()){
            PreparedStatement statement = connection.prepareStatement("select count(*) from DBCont.contact");
            ResultSet rs = statement.executeQuery();
            rs.next();
            return rs.getLong(1);
        } catch (SQLException | NamingException e) {
            throw new DaoException("Error while deleting of phones or attaches", e);
        }
    }

    private void deleteOnStatement(String string, long idContact){
        try (Connection connection = TransactionManager.createConnection()){
            PreparedStatement statement = connection.prepareStatement(string);
            statement.setLong(1, idContact);
            statement.execute();
        } catch (SQLException | NamingException e) {
            throw new DaoException("Error while deleting of phones or attaches", e);
        }
    }
    private long insertContact(Contact contact){
        try(Connection connection = TransactionManager.createConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "insert into DBCont.contact (firstName, lastName, patronymic,  gender, birthday, citizenship, maritalStatus, website, email, company, addressId ) " +
                            "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)){
            statement.setString(1,contact.getFirstName());
            statement.setString(2,contact.getLastName());
            statement.setString(3,contact.getPatronymic());

            statement.setString(4,contact.getGender()!=null? contact.getGender().name(): null);
            statement.setDate(5,   new Date(contact.getBirthday().getTime()));
            statement.setString(6,contact.getCitizenship());
            statement.setString(7, contact.getMaritalStatus()!=null? contact.getMaritalStatus().name(): null);
            statement.setString(8, contact.getWebsite());
            statement.setString(9, contact.getEmail());
            statement.setString(10, contact.getCompany());

            long id = setAdds(contact);
            statement.setLong(11, id);

            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();

            generatedKeys.next();
            return generatedKeys.getLong(1);
        }catch (SQLException | NamingException e){
            System.out.println(e);
            throw new DaoException("Error while inserting contact", e);

        }
    }
    private long setAdds(Contact contact){
        Address address = contact.getAddress();
        Long idAddress = address.getAddressId();
        String query;
        if(idAddress == 0){
            query ="";
            try (Connection connection = TransactionManager.createConnection();
                 PreparedStatement statement = connection.prepareStatement("insert into DBCont.address" +
                         " (country, city, street, houseNumber, flat, zipcode)" +
                         " VALUES (?, ?, ?, ?, ?, ?)", +
                         Statement.RETURN_GENERATED_KEYS)){
                statement.setString(1, address.getCountry());
                statement.setString(2, address.getCity());
                statement.setString(3, address.getStreet());
                statement.setString(4, address.getHouseNumber());
                statement.setString(5, address.getFlat());
                statement.setString(6, address.getZipcode());

                statement.executeUpdate();

                ResultSet generatedKeys = statement.getGeneratedKeys();
                generatedKeys.next();
                return generatedKeys.getLong(1);
            } catch (SQLException | NamingException e) {
                throw new DaoException("Error while inserting address of contact", e);
            }
        }else{
            query = "update  DBCont.address set country=?, city=?, street=?, houseNumber=?, flat=?, zipcode=? where addressId=?";
            try (Connection connection = TransactionManager.createConnection();
                 PreparedStatement statement = connection.prepareStatement(query)){
                statement.setString(1, address.getCountry());
                statement.setString(2, address.getCity());
                statement.setString(3, address.getStreet());
                statement.setString(4, address.getHouseNumber());
                statement.setString(5, address.getFlat());
                statement.setString(6, address.getZipcode());
                statement.setLong(7, idAddress);
                statement.executeUpdate();

                return idAddress;
            } catch (SQLException | NamingException e) {
                throw new DaoException("Error while inserting address of contact", e);
            }
        }




    }
    private void updateContact(Contact contact){
        try (Connection connection = TransactionManager.createConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE DBCont.contact  SET  " +
                    "firstName = ?, lastName=?, patronymic=?, birthday=?, gender=?, citizenship=?, maritalStatus=?, " +
                    "website=?, email=?, company=?, addressId=? WHERE contactId = ?")) {
                statement.setString(1,contact.getFirstName());
                statement.setString(2,contact.getLastName());
                statement.setString(3,contact.getPatronymic());
                statement.setDate(4,   contact.getBirthday());
                statement.setString(5,contact.getGender()!=null? contact.getGender().name(): null);
                statement.setString(6,contact.getCitizenship());
                statement.setString(7, contact.getMaritalStatus()!=null? contact.getMaritalStatus().name(): null);
                statement.setString(8, contact.getWebsite());
                statement.setString(9, contact.getEmail());
                statement.setString(10, contact.getCompany());
                long addressId = setAdds(contact);
                statement.setLong(11, addressId);

                statement.setLong(12, contact.getContactId());

                statement.executeUpdate();
            }
        } catch (SQLException | NamingException e) {
            throw new DaoException("Error while updating contact", e);
        }
    }
    private String makeSelectQuery(String query,SearchCriteria criteria,
                                   List<String> paramStrList, List<Date> paramDateList) {
        String word = " WHERE ";
        if (criteria.getFirstName() != null && ! "".equals(criteria.getFirstName())) {
            query += word +"INSTR (firstName , ?)";
            paramStrList.add(criteria.getFirstName());
            word = " AND ";
        }
        if (criteria.getLastName() != null && ! "".equals(criteria.getLastName())) {
            query += word +"INSTR (lastName, ?)";
            paramStrList.add(criteria.getLastName());
            word = " AND ";
        }
        if (criteria.getPatronymic() != null && ! "".equals(criteria.getPatronymic())) {
            query += word +"INSTR (patronymic, ?)";
            paramStrList.add(criteria.getPatronymic());
            word = " AND ";
        }
        if (criteria.getGender() != null && ! "".equals(criteria.getGender())) {
            query += word +"gender = ?";
            paramStrList.add(criteria.getGender().name());
            word = " AND ";
        }
        if (criteria.getCitizenship() != null && ! "".equals(criteria.getCitizenship())) {
            query += word +"INSTR (citizenship, ?)";
            paramStrList.add(criteria.getCitizenship());
            word = " AND ";
        }
        if (criteria.getMaritalStatus() != null && ! "".equals(criteria.getMaritalStatus())) {
            query += word+ "maritalStatus = ?";
            paramStrList.add(criteria.getMaritalStatus().name());
            word = " AND ";
        }
        if (criteria.getCountry() != null && ! "".equals(criteria.getCountry())) {
            query += word+ "INSTR (country, ?)";
            paramStrList.add(criteria.getCountry());
            word = " AND ";
        }
        if (criteria.getCity() != null && ! "".equals(criteria.getCity())) {
            query += word+ "INSTR(city, ?)";
            paramStrList.add(criteria.getCity());
            word = " AND ";
        }
        if (criteria.getFlat() != null && ! "".equals(criteria.getFlat())) {
            query += word+ "flat = ?";
            paramStrList.add(criteria.getFlat());
            word = " AND ";
        }
        if (criteria.getStreet() != null && ! "".equals(criteria.getStreet())) {
            query += word+ "INSTR (street, ?)";
            paramStrList.add(criteria.getStreet());
            word = " AND ";
        }
        if (criteria.getHouseNumber() != null && ! "".equals(criteria.getHouseNumber())) {
            query += word+ "flat = ?";
            paramStrList.add(criteria.getHouseNumber());
            word = " AND ";
        }
        if (criteria.getZipcode() != null && ! "".equals(criteria.getZipcode())) {
            query += word+ "`index` = ?";
            paramStrList.add(criteria.getZipcode());
            word = " AND ";
        }
        if (criteria.getBirthdayFrom() != null && criteria.getBirthdayTO() != null) {
            query += word + "birthday BETWEEN ? AND ?";
            paramDateList.add(new Date( criteria.getBirthdayFrom().getTime()));
            paramDateList.add(new Date( criteria.getBirthdayTO().getTime()));
        } else {
            if (criteria.getBirthdayFrom() != null) {
                query += word + "birthday >= ?";
                paramDateList.add(new Date(criteria.getBirthdayFrom().getTime()));
            }
            if (criteria.getBirthdayTO() != null) {
                query += word + "birthday <= ?";
                paramDateList.add(new Date( criteria.getBirthdayTO().getTime()));
            }
        }
        return query;
    }

}

