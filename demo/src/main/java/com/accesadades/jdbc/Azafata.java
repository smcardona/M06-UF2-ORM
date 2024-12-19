package com.accesadades.jdbc;

public class Azafata {

    private final int id;
    private String name;
    private String phone;
    private String passport;
    private String ig;


    public enum Filter {
        NOMBRE, PASAPORTE;
    }




    // Constructor completo
    public Azafata(String name, String passaport, String telefono, String ig) {

        this.id = generateId();
        this.name = name;
        this.phone = passaport;
        this.passport = telefono;
        this.ig = ig;
        
    }

    public Azafata(int id, String name, String passaport, String telefono, String ig) {

        this.id = id;
        this.name = name;
        this.phone = telefono;
        this.passport = passaport;
        this.ig = ig;
        
    }

    private static int generateId() {
        return (int) (Math.random() * 10000);
    }

    


    // toString para depuración y visualización
    @Override
    public String toString() {
        return "Azafata{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", passport='" + passport + '\'' +
                ", phone='" + phone + '\'' +
                ", ig='" + ig + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String passaport) {
        this.phone = passaport;
    }

    public String getPassport() {
        return passport;
    }

    public void setPassport(String passport) {
        this.passport = passport;
    }

    public String getIg() {
        return ig;
    }

    public void setIg(String ig) {
        this.ig = ig;
    }

    
}