package commands;

import Service.ContactService;
import Service.ServiceFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import commands.exception.CommandException;
import commands.util.FilesUtil;
import commands.util.GeneralUtil;
import model.Attachments;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

import java.io.IOException;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.*;


public class SetAttachCommand implements Command{
    private Logger logger = LogManager.getLogger(SetAttachCommand.class);
    private Map<String, String> formFields = new HashMap<>();
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 30;
    private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 50;
    private ContactService contactService = ServiceFactory.getContactService();
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {

        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (!isMultipart) {
            System.out.println("Not multipart type request: {}, {}"+request.getRequestURI()+ request.getContentType());
            return;
        }
        Long contacId = Long.valueOf(request.getParameter("contactId"));

        ServletFileUpload upload = createUploadRestrictions();
        List<FileItem> items = null;
        try {
            items = upload.parseRequest(request);
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
        List<FileItem> fileItems=parseFormFieldValues(items);
        try {
            saveAvatarOnDisk(contacId, fileItems);

            actions(contacId);
            List<Attachments> addAttcahes = createAttach(contacId, fileItems);
            if(addAttcahes.size()!=0)
                contactService.saveAttaches(contacId, addAttcahes);


            saveAttachmentsOnDisk(contacId, fileItems);
            logger.info("File was attached");
        } catch (Exception ex) {
            throw new CommandException("ERROR while getting attachments "+ex.getMessage());
        }

    }
    private List<Attachments> createAttach(Long idContact, List<FileItem> fileItem){
        List<Attachments> newAttach = new ArrayList<>();
        for(FileItem file: fileItem){
            Attachments a = new Attachments();
            String str = file.getFieldName();
            String timeId = str.replaceAll("[^0-9]", "");

            a.setFileName(file.getName());
            a.setComment(formFields.get("comment"+timeId));
            a.setIdContact(idContact);
            String dateStr = formFields.get("date"+timeId);
            Date date = GeneralUtil.stringToDate(dateStr);
            a.setDateOfLoad(date);
            a.setRandomFileName(file.getName()+timeId);

            newAttach.add(a);
        }
        return newAttach;
    }
    private void actions(Long idContact) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode j = mapper.readTree(formFields.get("actions"));
        JsonNode delAttach = j.get("del");
        JsonNode editAtt = j.get("change");
        List<Long> delIndex = new ArrayList<>();
        List<JsonNode> editAttach = new ArrayList<>();
        if(delAttach.isArray()){
            for (JsonNode jsonNode: delAttach){
                delIndex.add(jsonNode.asLong());
            }
        }
        if(editAtt.isArray()){
            for (JsonNode jsonNode: editAtt){
                editAttach.add(jsonNode);
            }
        }
        if(delIndex.size()!=0){
            List<Attachments> attaches = contactService.getAttaches(idContact);
            for(Attachments attach:attaches) {
                Long a = attach.getAttachmentsId();
                System.out.println(a);
                if(delIndex.contains(a)){
                    FilesUtil.deleteOnPath(attach);

                }
            }
            contactService.deleteAttaches(delIndex);
        }
        List<Attachments> newAttaches = new ArrayList<>();
        newAttaches.addAll(setAtt(editAttach));

        contactService.updateAttach(newAttaches);


    }

    private List<Attachments> setAtt(List<JsonNode> addPh){
        List<Attachments> newAtt = new ArrayList<>();
        for(JsonNode j: addPh){
            Attachments attachments= new Attachments();
            attachments.setComment(j.get("attachComment").asText());
            attachments.setFileName(j.get("fileName").asText());
            attachments.setAttachmentsId(j.get("attachId").asLong());
            newAtt.add(attachments);

        }
        return  newAtt;
    }
    private ServletFileUpload createUploadRestrictions() {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setFileSizeMax(MAX_FILE_SIZE);
        upload.setSizeMax(MAX_REQUEST_SIZE);
        upload.setHeaderEncoding("UTF-8");
        return upload;
    }
    private List<FileItem> parseFormFieldValues(List<FileItem> items) {
        try {
            Iterator<FileItem> iterator = items.iterator();
            while (iterator.hasNext()) {
                FileItem item = iterator.next();
                if (item.isFormField()) {
                    String name = item.getFieldName();
                    String value = item.getString("UTF-8");
                    formFields.put(name, value);
                    iterator.remove();
                }
            }
        } catch (Exception ex) {
            throw new CommandException("ERROR while parse form field values "+ex.getMessage() );
        }
        return items;
    }
    private void saveAttachmentsOnDisk(Long contactId, List<FileItem> fileItems) throws Exception {
        Properties properties = new Properties();
        try {
            properties.load(AvatarCommand.class.getResourceAsStream("/attach.properties"));
        } catch (IOException e) {
            throw  new CommandException("Error while getting attachment",e);
        }
        Iterator<FileItem> iterator = fileItems.iterator();
        while (iterator.hasNext()) {
            FileItem item = iterator.next();

            String realFileName = item.getName();
            String fieldName = item.getFieldName();
            String timeId= fieldName.replaceAll("[^0-9]", "");
            System.out.println(timeId);
            String filePath = null;
            String attachmentsDirectory = properties.getProperty("SAVE_ATTACH_PATH");

            filePath = attachmentsDirectory + File.separator + contactId+ "_"+ realFileName+timeId;
            File uploadedFile = new File(filePath);

            item.write(uploadedFile);

        }
    }


    private void saveAvatarOnDisk(Long contactId, List<FileItem> fileItems) throws Exception {
        Properties properties = new Properties();
        try {
            properties.load(AvatarCommand.class.getResourceAsStream("/avatars.properties"));
        } catch (IOException e) {
            throw  new CommandException("Error while getting attachment",e);
        }
        Iterator<FileItem> iterator = fileItems.iterator();
        while (iterator.hasNext()) {
            FileItem item = iterator.next();
            String fieldName = item.getFieldName();
            if(fieldName.equals("avatar")){
                String filePath = null;
                String attachmentsDirectory = properties.getProperty("SAVE_AVATARS_PATH");
                filePath = attachmentsDirectory + File.separator + contactId + "_"+ "avatar";
                File uploadedFile = new File(filePath);
                item.write(uploadedFile);
                iterator.remove();
                break;
            }

        }
    }
}
