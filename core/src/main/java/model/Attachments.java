package model;


import java.util.Date;

public class Attachments{
    private Long attachmentsId;
    private String fileName;
    private String randomFileName;

    private Date dateOfLoad;
    private String comment;
    private Long idContact;


    public String getRandomFileName() {
        return randomFileName;
    }

    public void setRandomFileName(String randomFileName) {
        this.randomFileName = randomFileName;
    }

    public Long getIdContact() {
        return idContact;
    }

    public void setIdContact(Long idContact) {
        this.idContact = idContact;
    }

    public Long getAttachmentsId() {
        return attachmentsId;
    }

    public void setAttachmentsId(Long attachmentsId) {
        this.attachmentsId = attachmentsId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Date getDateOfLoad() {
        return dateOfLoad;
    }

    public void setDateOfLoad(Date dateOfLoad) {
        this.dateOfLoad = dateOfLoad;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
