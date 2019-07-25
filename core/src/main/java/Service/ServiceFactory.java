package Service;

public class ServiceFactory {
    public static ContactService getContactService(){
        return new ContactServiceImpl();
    }
}
