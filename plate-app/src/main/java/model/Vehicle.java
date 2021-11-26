package model;

import java.util.Date;

public class Vehicle {

    int id;
    String reg_number;
    Date last_arrival;
    Date last_departure;
    Boolean is_in;
    int owner_id;

    public Vehicle() {}

    public Vehicle(int id,String reg_number, Date last_arrival, Date last_departure, Boolean is_in, int owner_id) {
        this.id = id;
        this.reg_number = reg_number;
        this.last_arrival = last_arrival;
        this.last_departure = last_departure;
        this.is_in = is_in;
        this.owner_id = owner_id;
    }
}
