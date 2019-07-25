package dao1;

import dao1.impl.ContactImplDao;

public class DaoFactory {
    public static ContactDao getContactDao(){
        return ContactImplDao.getInstance();
    }
}
