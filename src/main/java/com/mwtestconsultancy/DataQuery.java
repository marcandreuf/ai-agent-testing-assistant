package com.mwtestconsultancy;

import com.mwtestconsultancy.models.Booking;
import com.mwtestconsultancy.models.Room;

import java.sql.*;

public class DataQuery {

    private Connection connection;
    PreparedStatement preparedStatement;

    private String queryRoomByBeds = """
        SELECT * FROM ROOMS WHERE beds = ?
    """;

    private String createRoom = """
        INSERT INTO ROOMS (type, beds, description, roomPrice) VALUES (?, ?, ?, ?)
    """;

    public DataQuery() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb");

        Statement st = connection.createStatement();

        st.executeUpdate("""
            CREATE TABLE BOOKINGS (
                bookingid int NOT NULL AUTO_INCREMENT,   // Unique ID for each booking
                roomid int,                              // Foreign key to the ROOMS table
                firstname varchar(255),                  // First name of the person booking
                lastname varchar(255),                   // Last name of the person booking
                depositpaid boolean,                     // Whether a deposit is paid
                checkin date,                            // Check-in date
                checkout date,                           // Check-out date
                primary key (bookingid)                  // Primary key on bookingid
            );
            CREATE TABLE ROOMS (
                roomid int NOT NULL AUTO_INCREMENT,      // Unique ID for each room
                type varchar(255),                       // Type of room (single, double, etc.)
                beds int,                                // Number of beds in the room
                description varchar(2000),               // Description of the room
                roomPrice int,                           // Price of the room
                primary key (roomid)                     // Primary key on roomid
            );
        """);
    }

    public int queryRoomByBeds(int beds) {
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(queryRoomByBeds);
            preparedStatement.setInt(1, beds);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("roomid");
            } else {
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    public int createRoomAndThenReturnRoomId(Room room) {
        ResultSet generatedKeys = null;
        try {
            preparedStatement = connection.prepareStatement(createRoom, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, room.getType());
            preparedStatement.setInt(2, room.getBeds());
            preparedStatement.setString(3, room.getDescription());
            preparedStatement.setInt(4, room.getRoomPrice());
            preparedStatement.executeUpdate();

            generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (preparedStatement != null) preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    public String outputTables(String query) throws SQLException {
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(query);
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        StringBuilder output = new StringBuilder();

        while (rs.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                output.append(rs.getString(i)).append(" ");
            }
            output.append("\n");
        }

        return output.toString();
    }

    public boolean createBooking(Booking booking) {
        try {
            preparedStatement = connection.prepareStatement("""
                INSERT INTO BOOKINGS (roomid, firstname, lastname, depositpaid, checkin, checkout)
                VALUES (?, ?, ?, ?, ?, ?)
            """);
            preparedStatement.setInt(1, booking.getRoomid());
            preparedStatement.setString(2, booking.getFirstname());
            preparedStatement.setString(3, booking.getLastname());
            preparedStatement.setBoolean(4, booking.isDepositpaid());
            preparedStatement.setDate(5, Date.valueOf(booking.getCheckin()));
            preparedStatement.setDate(6, Date.valueOf(booking.getCheckout()));
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public int countTableRows(String tableName) {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            ResultSet rs1 = statement.executeQuery(String.format("SELECT COUNT(*) FROM %s", tableName));
            rs1.next();
            int roomCount = rs1.getInt(1);
            rs1.close();
            return roomCount;
        } catch (SQLException e) {            
            e.printStackTrace();
            return -1;            
        }finally{
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void resetDB() {
        Statement statement = null;
        try {
            // Disable foreign key checks temporarily
            statement = connection.createStatement();
            statement.execute("SET REFERENTIAL_INTEGRITY FALSE");
            
            statement.executeUpdate("TRUNCATE TABLE BOOKINGS");
            statement.executeUpdate("TRUNCATE TABLE ROOMS");
            
            // Reset auto-increment columns
            statement.executeUpdate("ALTER TABLE BOOKINGS ALTER COLUMN bookingid RESTART WITH 1");
            statement.executeUpdate("ALTER TABLE ROOMS ALTER COLUMN roomid RESTART WITH 1");
            
            // Re-enable foreign key checks
            statement.execute("SET REFERENTIAL_INTEGRITY TRUE");
            
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }
}