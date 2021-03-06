package Server.DataAccess;

import Server.Entities.IBooking;
import Server.Exceptions.BookingNotFoundException;
import Server.Exceptions.FacilityNotFoundException;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.ParseException;
import java.util.List;

public interface IServerDB {
    /**
     * Creates a booking for a given day for a given facility between a specified start to end time
     * @param day: the int value of the enumerated days
     * @param clientId: the client id string
     * @param facilityName: the name of the facility to book
     * @param startTime: the start time in HH:mm
     * @param endTime: the end time in HH:mm
     * @return the confirmation id of the booking created
     * @throws FacilityNotFoundException when the facility name provided is not found
     */
    String createBooking(int day, String clientId, String facilityName, String startTime, String endTime) throws FacilityNotFoundException;

    /**
     * An overloaded method to create booking and update observing clients
     * @param day: the int value of the enumerated days
     * @param clientId: the client id string
     * @param facilityName: the name of the facility to book
     * @param startTime: the start time in HH:mm
     * @param endTime: the end time in HH:mm
     * @param serverSocket: the socket of the server to send updates to the observing clients
     * @return the confirmation id of the booking created
     * @throws FacilityNotFoundException when the facility name provided is not found
     */
    String createBooking(int day, String clientId, String facilityName, String startTime, String endTime, DatagramSocket serverSocket) throws FacilityNotFoundException;

    /**
     * Updates a booking given an existing confirmation id and the new start and end times
     * @param confirmationId: confirmation id of an existing, confirmed booking
     * @param facilityName: the name of the facility
     * @param newStartTime: the new start time in HH:mm
     * @param newEndTime: the new end time in HH:mm
     * @throws FacilityNotFoundException if the facility name is not found in the database
     * @throws BookingNotFoundException when the confirmation id is not found in the facility
     */
    void updateBooking(String confirmationId, String facilityName, String newStartTime, String newEndTime) throws FacilityNotFoundException, BookingNotFoundException;

    /**
     * An overloaded method to create booking and update observing clients
     * @param confirmationId: confirmation id of an existing, confirmed booking
     * @param facilityName: the name of the facility
     * @param newStartTime: the new start time in HH:mm
     * @param newEndTime: the new end time in HH:mm
     * @param serverSocket: the socket of the server to send updates to the observing clients
     * @throws FacilityNotFoundException if the facility name is not found in the database
     * @throws BookingNotFoundException when the confirmation id is not found in the facility
     */
    void updateBooking(String confirmationId, String facilityName, String newStartTime, String newEndTime, DatagramSocket serverSocket) throws FacilityNotFoundException, BookingNotFoundException;

    /**
     * Retrieves a booking from a given facility using the confirmation id
     * @param confirmationId: confirmation id of an existing, confirmed booking
     * @param facilityName: the name of the facility
     * @return the IBooking-implemented object that corresponds to the confirmation id
     * @throws FacilityNotFoundException if the facility name given is not found in the database
     * @throws BookingNotFoundException if the confirmation id is not found in the facility
     */
    IBooking getBookingByConfirmationId(String confirmationId, String facilityName) throws FacilityNotFoundException, BookingNotFoundException;

    /**
     * Retrieves all bookings for the given day under the given facility
     * @param facilityName: the name of the facility
     * @param day: the int code of the days enum
     * @return a sorted list of IBooking-implemented objects
     * @throws FacilityNotFoundException if the facility name does not exist
     */
    List<IBooking> getSortedBookingsByDay(String facilityName, int day) throws FacilityNotFoundException;

    /**
     * Retrieves the facility names stored in the database
     * @return a list of facility names
     */
    List<String> getFacilityNames();

    /**
     * Retrieves facility through the given name and gets availability for the days provided for that facility
     * @param facilityName: the name of the facility
     * @param days: a list of days to get the availability
     * @return a string of all the available timeslots in the form "D/HH/mm to D/HH/mm"
     */
    String getAvailability(String facilityName, List<Integer> days) throws FacilityNotFoundException, ParseException;

    /**
     * Adds a client to the facility's update list
     * @param facilityName: the name of the facility
     * @param clientAddress: the internet address of the client
     * @param clientPort: the port number to send the updates to
     * @param expirationTimestamp: the system UNIX timestamp that the observation will expire
     * @throws FacilityNotFoundException if the facility is not found
     */
    void addObservingClient(String facilityName, InetAddress clientAddress, int clientPort, long expirationTimestamp) throws FacilityNotFoundException;
}
