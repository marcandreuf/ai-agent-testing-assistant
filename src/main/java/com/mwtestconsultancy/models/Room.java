package com.mwtestconsultancy.models;


import java.sql.ResultSet;
import java.sql.SQLException;

public class Room {

    private int roomid;

    private String type;

    private int beds;

    private String description;

    private int roomPrice;

    public Room(String type, int beds, String description, int roomPrice) {
        this.type = type;
        this.beds = beds;
        this.description = description;
        this.roomPrice = roomPrice;
    }

    public Room(ResultSet resultSet) throws SQLException {
        this.roomid = resultSet.getInt("roomid");
        this.type = resultSet.getString("type");
        this.beds = resultSet.getInt("beds");
        this.description = resultSet.getString("description");
        this.roomPrice = resultSet.getInt("roomPrice");
    }

    public int getRoomid() {
        return roomid;
    }

    public void setRoomid(int roomid) {
        this.roomid = roomid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getBeds() {
        return beds;
    }

    public void setBeds(int beds) {
        this.beds = beds;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRoomPrice() {
        return roomPrice;
    }

    public void setRoomPrice(int roomPrice) {
        this.roomPrice = roomPrice;
    }

    @Override
    public String toString() {
        return "Room{" +
                "roomid=" + roomid +
                ", type='" + type + '\'' +
                ", beds=" + beds +
                ", description='" + description + '\'' +
                ", roomPrice=" + roomPrice +
                '}';
    }
}
