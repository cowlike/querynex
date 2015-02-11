package jk.querynex;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


/**
 * AbstractQuery directly communicates with the master and
 * individual game servers. <br/>
 * The darkplaces dpmaster server is based on the Quake 3 Arena server
 * and therefore very similar. <br/>
 * Based on
 * <a href="ftp://ftp.idsoftware.com/idstuff/quake3/docs/server.txt"> id's quake
 * 3 server commands howto</a>
 * @author dmaz (original)
 */
class AbstractQuery {

    private final int TIMEOUT = 2000;          // Timeout used for the sockets
    private final int PACKET_SIZE = 2048;      // Receive packet size
    private final int TYPE_OF_SERVICE = 0x04;  // Type of Service octet: reliability
    protected final int ATTEMPTS = 2;
    protected int ping;

    /**
     * Sends a request to a game server and returns the output.
     * @param address of the server to query
     * @param port of the server to query
     * @param request message to send to server
     * @return  String of the server's reply
     */
    protected byte[] getInfo(final String address, final int port, final String request) {
        byte[] response = null;
        long sendTime;
        byte[] requestBytes = request.getBytes();
        requestBytes[0] = 0xff;  // Change first 4 chars to 0xff
        requestBytes[1] = 0xff;
        requestBytes[2] = 0xff;
        requestBytes[3] = 0xff;

		def socket = null
		try {
			def outPacket = new DatagramPacket(requestBytes, requestBytes.length, InetAddress.getByName(address), port)
			def inPacket = new DatagramPacket(new byte[PACKET_SIZE], PACKET_SIZE)
			socket = new DatagramSocket()
            socket.setSoTimeout(TIMEOUT);
            socket.setTrafficClass(TYPE_OF_SERVICE);
			socket.send(outPacket)
			sendTime = System.currentTimeMillis();  // ping timer
			socket.receive inPacket
			ping = (int) (System.currentTimeMillis() - sendTime);
			response = inPacket.data
		}
		catch (IOException e) {
			throw new Exception("Could not query ${address}:${port}, ${e.message}", e)
		}
		finally {
			socket?.close()
		}
		
        return response;
    }
}

