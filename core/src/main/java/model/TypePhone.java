package model;

public enum TypePhone {
    MOBILE("мобильный"), HOME("Домашний");
    private String type;

    public String getType() {
        return type;
    }

    private TypePhone(String type) {
        this.type = type;
    }
}
