package model;

public enum Gender {
    MALE("мужской"), FEMALE("женский");

    private String gender;

    private Gender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }
}
