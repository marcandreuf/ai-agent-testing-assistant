package com.mwtestconsultancy;

import com.mwtestconsultancy.models.Booking;
import com.mwtestconsultancy.models.Room;
import dev.langchain4j.agent.tool.*;

import java.sql.SQLException;
import java.time.LocalDate;

public class DataAssistantTools {

    DataQuery dataQuery = new DataQuery();

    public DataAssistantTools() throws SQLException {
    }

    @Tool("Check for rooms that match the amount of beds required before creating a new room")
    public int checkForExistingRooms(@P("Amount of beds required for an existing room") int beds){
        System.out.println("Agent: You want me to lookup a room with a minimum of " + beds + " beds");

        return dataQuery.queryRoomByBeds(beds);
    }

    @Tool("If there is no room that can be used, record with the necessary amount of beds required")
    public int createRooms(@P("Amount of beds required to create a room") int count) {
        System.out.println("Agent: You want me to create a room with " + count + " beds.");

        Room room = new Room("Single", count, "A room created by my AI bot", 100);
        int roomid = dataQuery.createRoomAndThenReturnRoomId(room);

        System.out.println("Agent: Room created with roomid " + roomid);

        return roomid;
    }

    @Tool("Create booking records")
    public void createBookings(@P("Room id that is used to create a booking") int roomId) throws SQLException {
        System.out.println("Agent: You want be to create a booking using roomid " + roomId);

        Booking booking = new Booking(roomId, 1, "John", "Doe", true, LocalDate.now(), LocalDate.now().plusDays(1));
        dataQuery.createBooking(booking);
    }

    @Tool("Show results of database. Show the overall state of tables of the database")
    public void displayDatabase() throws SQLException {
        printRooms();
        printBookings();
    }

    @Tool("Show the current rooms state of the database") 
    public void displayRooms() throws SQLException {        
        printRooms();
    }

    @Tool("Show the current bookings state of the database")
    public void displayBookings() throws SQLException {
        printBookings();
    }

    private void printRooms() throws SQLException {        
        String roomResult = dataQuery.outputTables("SELECT * FROM ROOMS");
        System.out.println("Agent: Current ROOM database state:");
        System.out.println(roomResult);
    }

    private void printBookings() throws SQLException {
        String bookingResult = dataQuery.outputTables("SELECT * FROM BOOKINGS");
        System.out.println("Agent: Current BOOKING database state:");
        System.out.println(bookingResult);
    }

}
