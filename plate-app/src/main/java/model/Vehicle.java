package model;

import java.util.Date;

public class Vehicle {

    String reg_number;
    Date last_arrival;
    Date last_departure;
    Boolean is_in;

    public Vehicle() {}

    public Vehicle(String reg_number, Date last_arrival, Date last_departure, Boolean is_in) {
        this.reg_number = reg_number;
        this.last_arrival = last_arrival;
        this.last_departure = last_departure;
        this.is_in = is_in;
    }
}
