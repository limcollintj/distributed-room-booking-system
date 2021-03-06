package Server.Entities;

import Server.Entities.Concrete.ObservationSession;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.PriorityQueue;

public abstract class AbstractFacility implements IObservable {
    public final String CLIENT_ADDRESS_SEPARATOR = "&=";
    private String facilityName;
    private PriorityQueue<ObservationSession> observationSessions;

    @Override
    public void addObservationSession(InetAddress clientAddress, int clientPort, long expirationTimeStamp) {
        String client = clientAddress.getHostAddress() + CLIENT_ADDRESS_SEPARATOR + clientPort;
        ObservationSession session = new ObservationSession(expirationTimeStamp, client);
        observationSessions.add(session);
    }

    @Override
    public void sendUpdateToObservingClients(DatagramSocket socket) throws IOException {
        /* Set up variables
        ioExceptCaught: indicates if IOException was thrown
        updateInfoByteBuffer: update message string in byte format
         */
        boolean ioExceptCaught = false;
        String updateMessage = getServerReplyString();
        byte[] updateInfoByteBuffer = updateMessage.getBytes();

        /*
        - Remove expired observations based on current timestamp
        - Iterate through priority queue and send message to clients
        - If IOException caught, rethrow at end of function
         */
        removeExpiredObservationSessions();
        for (ObservationSession clientSession : observationSessions) {
            try {
                sendMessageTo(socket, clientSession.getClient(), updateInfoByteBuffer);
            } catch (IOException e) {
                ioExceptCaught = true;
                System.out.println("Failed to send to: " + clientSession.getClient());
            }
        }
        if (ioExceptCaught) throw new IOException();
    }

    // =====================================
    // Getters and Setters
    // =====================================
    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public PriorityQueue<ObservationSession> getObservationSessions() {
        return observationSessions;
    }

    public void setObservationSessions(PriorityQueue<ObservationSession> observationSessions) {
        this.observationSessions = observationSessions;
    }

    public String getServerReplyString() {
        return "Update from: " + facilityName;
    }

    // =====================================
    // Protected methods
    // =====================================
    protected void removeExpiredObservationSessions() {
        /* Removes expired observation sessions
        - Min heap based on expiry timestamp is used to form queue
        - While queue is not empty and head of queue is expired, remove head of queue
         */
        while(observationSessions.size() > 0 && observationSessions.peek().getExpirationTimeStamp() < System.currentTimeMillis()) {
            observationSessions.poll();
        }
    }

    protected void sendMessageTo(DatagramSocket socket, String client, byte[] updateInfo) throws IOException {
        String[] clientInfo = client.split(CLIENT_ADDRESS_SEPARATOR);
        assert (clientInfo.length == 2);
        InetAddress clientAddress = InetAddress.getByName(clientInfo[0]);
        int clientPort = Integer.parseInt(clientInfo[1]);

        DatagramPacket message = new DatagramPacket(updateInfo, updateInfo.length, clientAddress, clientPort);
        socket.send(message);
    }
}
