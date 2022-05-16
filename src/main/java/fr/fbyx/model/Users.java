package fr.fbyx.model;

public class Users {
    private String name;
    private String lastname;
    private String role;

    public Users(String name, String lastname, String role) {
        this.name = name;
        this.lastname = lastname;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public static Users ofSplit(String line) {
        String[] parts = line.split(";");
        return new Users(parts[0], parts[1], parts[2]);
    }

}
