import com.mwtestconsultancy.DataQuery;
import com.mwtestconsultancy.models.Booking;
import com.mwtestconsultancy.models.Room;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataQueryTest {

    private static DataQuery dataQuery;

    @BeforeAll
    public static void setup() throws SQLException {
        dataQuery = new DataQuery();
    }

    @BeforeEach
    public void setUpTest() throws SQLException {
        dataQuery.resetDB();
 
        int roomCount = dataQuery.countTableRows("ROOMS");
        int bookingCount = dataQuery.countTableRows("BOOKINGS");
        assert roomCount == 0 : "ROOMS table not empty after reset";
        assert bookingCount == 0 : "BOOKINGS table not empty after reset";
    }

    @Test
    public void testCreateRoom() {
        Room room = new Room("Single", 1, "A room created by my AI bot", 100);
        int roomid = dataQuery.createRoomAndThenReturnRoomId(room);

        assertEquals(1, roomid);
    }

    @Test
    public void testCreateBooking() {
        Booking booking = new Booking(1, 1, "John", "Doe", true, LocalDate.now(), LocalDate.now().plusDays(1));
        boolean bookingCreated = dataQuery.createBooking(booking);

        assertEquals(true, bookingCreated);
    }

    @Test
    public void testQueryRoomByBeds() {
        Room room = new Room("Single", 10, "A room created by my AI bot", 100);
        int roomid = dataQuery.createRoomAndThenReturnRoomId(room);

        int retrievedRoomId = dataQuery.queryRoomByBeds(10);

        assertEquals(roomid, retrievedRoomId);
    }

    @Test
    public void testShowDatabase() throws SQLException {
        Room room = new Room("Single", 1, "A room created by my AI bot", 100);
        dataQuery.createRoomAndThenReturnRoomId(room);

        String output = dataQuery.outputTables("SELECT * FROM ROOMS");

        assertEquals("1 Single 1 A room created by my AI bot 100 \n", output);
    }

}
