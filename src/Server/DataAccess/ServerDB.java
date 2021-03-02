package Server.DataAccess;

import Server.Entities.Concrete.Facility;
import Server.Exceptions.BookingNotFoundException;
import Server.Exceptions.FacilityNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServerDB implements IServerDB {
    private HashMap<String, Facility> facilities;
    private HashMap<String, Integer> bookingsByDay;

    public ServerDB() {
        facilities = createFacilitiesTable();
        bookingsByDay = new HashMap<>();
    }

    public List<String> getFacilityNames() {
        return new ArrayList<>(facilities.keySet());
    }

    @Override
    public String createBooking(int day, String clientId, String facilityName, String startTime, String endTime)
            throws FacilityNotFoundException {
        if (!facilities.containsKey(facilityName)) {
            throw new FacilityNotFoundException("Facility does not exist");
        }
        Facility facility = facilities.get(facilityName);
        String confirmationId = facility.addBooking(day, clientId, startTime, endTime);
        bookingsByDay.put(confirmationId, day);
        return confirmationId;
    }

    @Override
    public void updateBooking(String confirmationId, String facilityName, String newStartTime, String newEndTime)
            throws BookingNotFoundException {
        Facility facility = facilities.get(facilityName);
        int day = bookingsByDay.get(confirmationId);
        facility.updateBooking(day, confirmationId, newStartTime, newEndTime);
    }

    private HashMap<String, Facility> createFacilitiesTable() {
        HashMap<String, Facility> facilities = new HashMap<>();
        List<String[]> allFacilityInfo = getFacilityInfo();
        for (String[] facilityInfo : allFacilityInfo) {
            String facilityName = facilityInfo[0];
            String facilityType = facilityInfo[1];
            Facility facility = new Facility(facilityName, facilityType);
            facilities.put(facilityName, facility);
        }
        return facilities;
    }

    private List<String[]> getFacilityInfo() {
        List<String[]> facilityInfo = new ArrayList<>();
        // Add facilities in the form [facilityName, facilityType]
        facilityInfo.add(new String[]{"LT1", "Lecture Theater"});
        facilityInfo.add(new String[]{"LT2", "Lecture Theater"});
        facilityInfo.add(new String[]{"TC1", "Tennis Court"});
        facilityInfo.add(new String[]{"BTC1", "Badminton Court"});
        facilityInfo.add(new String[]{"BTC2", "Badminton Court"});
        facilityInfo.add(new String[]{"SWLAB1", "Software Lab"});

        return facilityInfo;
    }

}
