package model;

public enum MaritalStatus {
    MARRIED("женат/замужем"), WIDOWED("вдова/вдовец"), DIVORCED("разведён/разведена"), SINGLE("холост/не замужем");

    private String status;

    private MaritalStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
