package com.accesadades.jdbc;

public class Pasajero {

    private int id;
    private String name;
    private String passaport;
    private String telefono;




    // Constructor completo
    public Pasajero(String name, String passaport, String telefono) {

        this.id = generateId();
        this.name = name;
        this.passaport = passaport;
        this.telefono = telefono;
        
    }

    public Pasajero(int id, String name, String passaport, String telefono) {

        this.id = id;
        this.name = name;
        this.passaport = passaport;
        this.telefono = telefono;
        
    }

    private static int generateId() {
        return (int) (Math.random() * 10000);
    }

    


    // toString para depuración y visualización
    @Override
    public String toString() {
        return null;

        /* "Person{" +
                "employeeId=" + employeeId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneInt='" + phoneInt + '\'' +
                ", hireDate=" + hireDate +
                ", jobId='" + jobId + '\'' +
                ", salary=" + salary +
                ", commissionPct=" + commissionPct +
                ", managerId=" + managerId +
                ", departmentId=" + departmentId +
                ", bonus='" + bonus + '\'' +
                '}'; */
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

    public String getPassaport() {
        return passaport;
    }

    public void setPassaport(String passaport) {
        this.passaport = passaport;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}