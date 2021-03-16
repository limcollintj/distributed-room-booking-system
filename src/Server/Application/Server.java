package Server.Application;

import Server.BusinessLogic.FacilitiesBookingSystem;
import Server.DataAccess.IServerDB;
import Server.DataAccess.ServerDB;
import Server.Exceptions.*;

import java.io.IOException;
import java.net.*;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

public class Server {
    private DatagramSocket socket;
    private IServerDB serverDB;
    private FacilitiesBookingSystem facilitiesBookingSystem;

    public Server(int port) {
        try {
            System.out.println("Starting a service at port " + port);
            socket = new DatagramSocket(port);
            serverDB = new ServerDB();
            facilitiesBookingSystem = new FacilitiesBookingSystem(serverDB);
            printIp();
        } catch (Exception e){
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        try {
            int port = 17;
            Server server = new Server(port);
            server.service();
        } catch (SocketException ex) {
            System.out.println("Socket error: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }

    /**
     * Starts udp service
     * @throws IOException
     */
    private void service() throws IOException {
        while (true) {

            byte[] buffer = new byte[256];

            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            socket.receive(request);

            // TODO: Unmarshall the client request
            int functionCode = 0; // "unmarshallFunction(request.getData())"
            // TODO: explore using enums
            String responseMessage;
            switch (functionCode) {
                case 0:
                    responseMessage = handleGetAvailability(request);
                    break;
                case 1:
                    responseMessage = handleCreateBooking(request);
                    break;
                case 2:
                    responseMessage = handleUpdateBooking(request);
                    break;
                case 3:
                    responseMessage = handleAddObservingClient(request);
                    break;
                default:
                    responseMessage = handleHeartbeat();
                    break;
            }

            InetAddress clientAddress = request.getAddress();
            int clientPort = request.getPort();

            // Pseudo server response
            buffer = responseMessage.getBytes();

            DatagramPacket response = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
            socket.send(response);
        }
    }

    // ===================================
    // Handler functions
    // ===================================
    private String handleGetAvailability(DatagramPacket request) {
        try {
            String facilityName = "unmarshallString(request.getData())";
            List<Integer> days = Arrays.asList(0, 1); // unmarshallList(request.getData());
            String availability = facilitiesBookingSystem.getAvailability(facilityName, days);
            return "Facility availability: " + availability;
        } catch (FacilityNotFoundException e) {
            return "404: Facility not found";
        } catch (ParseException e) {
            return "500: Error occurred retrieving availability";
        }
    }

    private String handleCreateBooking(DatagramPacket request) {
        try {
            String requestId = "unmarshallString(request.getData())";
            if (requestId == "exists in cache") {
                return "retrieve response from cache";
            }
            String facilityName = "unmarshallString(request.getData())";
            String startDateTime = "unmarshallString(request.getData())";
            String endDateTime = "unmarshallString(request.getData())";
            String clientId = generateClientIdFromOrigin(request);
            String confirmationId = facilitiesBookingSystem.createBooking(facilityName, startDateTime, endDateTime, clientId, socket);
            return "Booking confirmation ID: " + confirmationId;
        } catch (InvalidDatetimeException | ParseException e) {
            return "400: Invalid datetime provided";
        } catch (TimingUnavailableException e) {
            return "409: Booking time not available";
        } catch (FacilityNotFoundException e) {
            return"404: Facility not found";
        }
    }

    private String handleUpdateBooking(DatagramPacket request) {
        try {
            String requestId = "unmarshallString(request.getData())";
            if (requestId == "exists in cache") {
                return "retrieve response from cache";
            }
            String confirmationId = "unmarshallString(request.getData())";
            String clientId = generateClientIdFromOrigin(request);
            int offset = 0; // unmarshallInt(request.getData())
            facilitiesBookingSystem.updateBooking(confirmationId, clientId, offset, socket);
            return "Booking updated successfully";
        } catch (WrongClientIdException | BookingNotFoundException e) {
            return "404: Invalid confirmation ID";
        } catch (InvalidDatetimeException e) {
            return "400: Invalid offset provided";
        } catch (TimingUnavailableException e) {
            return "409: New booking time not available";
        } catch (ParseException e) {
            return "500: Error occurred updating booking";
        }
    }

    private String handleAddObservingClient(DatagramPacket request) {
        try {
            String requestId = "unmarshallString(request.getData())";
            if (requestId == "exists in cache") {
                return "retrieve response from cache";
            }
            String facilityName = "unmarshallString(request.getData())";
            InetAddress clientAddress = request.getAddress();
            int clientPort = request.getPort();
            int durationInMin = 30; // unmarshallInt(request.getData());
            facilitiesBookingSystem.addObservingClient(facilityName, clientAddress, clientPort, durationInMin);
            return "Successfully added to observing list";
        } catch (FacilityNotFoundException e) {
            return "404: Facility not found";
        }
    }

    private String handleHeartbeat() {
        return "Request received by server";
    }

    private String generateClientIdFromOrigin(DatagramPacket request) {
        return request.getAddress().getHostAddress() + ":" + request.getPort();
    }

    /**
     * Prints private IP address
     * @throws IOException
     */
    private void printIp () throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("google.com", 80)); // Creates a pseudo connection to return the private IP address. Reference: https://stackoverflow.com/questions/9481865/getting-the-ip-address-of-the-current-machine-using-java
        System.out.println("Server started on: " + socket.getLocalAddress());
    }
}