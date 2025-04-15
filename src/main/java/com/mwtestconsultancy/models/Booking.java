package com.mwtestconsultancy.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class Booking {

    private int bookingid;

    private int roomid;

    private String firstname;

    private String lastname;

    private boolean depositpaid;

    private LocalDate checkin;

    private LocalDate checkout;

    public Booking(int bookingid, int roomid, String firstname, String lastname, boolean depositpaid, LocalDate checkin, LocalDate checkout) {
        this.bookingid = bookingid;
        this.roomid = roomid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.depositpaid = depositpaid;
        this.checkin = checkin;
        this.checkout = checkout;
    }

    public Booking(ResultSet resultSet) throws SQLException {
        this.bookingid = resultSet.getInt("bookingid");
        this.roomid = resultSet.getInt("roomid");
        this.firstname = resultSet.getString("firstname");
        this.lastname = resultSet.getString("lastname");
        this.depositpaid = resultSet.getBoolean("depositpaid");
        this.checkin = resultSet.getDate("checkin").toLocalDate();
        this.checkout = resultSet.getDate("checkout").toLocalDate();
    }

    public int getBookingid() {
        return bookingid;
    }

    public void setBookingid(int bookingid) {
        this.bookingid = bookingid;
    }

    public int getRoomid() {
        return roomid;
    }

    public void setRoomid(int roomid) {
        this.roomid = roomid;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public boolean isDepositpaid() {
        return depositpaid;
    }

    public void setDepositpaid(boolean depositpaid) {
        this.depositpaid = depositpaid;
    }

    public LocalDate getCheckin() {
        return checkin;
    }

    public void setCheckin(LocalDate checkin) {
        this.checkin = checkin;
    }

    public LocalDate getCheckout() {
        return checkout;
    }

    public void setCheckout(LocalDate checkout) {
        this.checkout = checkout;
    }
}
