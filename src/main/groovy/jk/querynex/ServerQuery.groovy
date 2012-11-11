package jk.querynex;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Query a particular game server using <code>getstatus</code>.
 * @author dmaz (original)
 */
public class ServerQuery extends AbstractQuery {

	protected String encoding = 'ISO-8859-1'
	
    /**
     * Creates a Server containing all of the server info,
     * excluding player names, scores, and pings.
     *
     * @param ipStr     String of IP address of this server
     * @return  Server containing the info of this server.
     */
    public Server getInfo(final String ipStr) {
        final String[] ip = ipStr.split(":");
        final int port = Integer.parseInt(ip[1]);
        String response = null;
        Server server = null;

        for (int attempts = 0; attempts < ATTEMPTS; attempts++) {
            try {
                response = new String(getInfo(ip[0], port, "xxxxgetinfo"), encoding);
                if (response != null) {
                    server = getServerFromResponse(response, ipStr);
                    break;
                }
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(this.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }
        }
        return server;
    }

    /**
     * Gets a server including its list of players.
     *
     * @param ipStr     String of IP address of server
     * @return  Server including ArrayList of Players
     */
    public Server getStatus(final String ipStr) {
        final String[] ip = ipStr.split(":");
        final int port = Integer.parseInt(ip[1]);
        byte[] response = null;
        Server server = null;

        for (int attempts = 0; attempts < ATTEMPTS; attempts++) {
			response = getInfo(ip[0], port, "xxxxgetstatus");

            if (response != null) {
                try {
					String responseLines = new String(response, encoding); 
                    responseLines = responseLines.substring(responseLines.indexOf('\\'));
                    String[] lines = responseLines.split("\n");
                    server = getServerFromResponse(lines[0], ipStr);
                    ArrayList<Player> players = new ArrayList<Player>(server.getPlayerCount());
                    for (int i = 1; i < lines.length - 1; i++) {
                        players.add(getPlayerFromResponse(lines[i]));
                    }
                    server.setPlayerList(players);
                } catch (IOException ex) {
                    Logger.getLogger(ServerQuery.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
        }
        return server;
    }

    /**
     * From a line in the queryResult, retrieve player info.
     * TODO: Make this less wasteful.
     * @param queryResult String of the line to process.
     * @return Player which is created.
     */
    private Player getPlayerFromResponse(final String queryResult) {
        final String[] playerData = queryResult.split(" ");
        
        final Player player = new Player();
        player.setScore(playerData[0] as int);
        player.setPing(playerData[1] as int);
		/*
		 * This was in the original, commented out. needs research (I added the "as int" -jk)
		 */
//        if (playerData.length == 3) {
//            player.setTeam(playerData[2] as int);
//        }
        try {
            player.setName(queryResult.substring(queryResult.indexOf('\"') + 1, queryResult.length() - 1));
        } catch (ArrayIndexOutOfBoundsException e) {
            player.setName("Player");
        }
        return player;
    }

    /**
     * Take the server info from the queryResult and build a Server.
     * Response will look like this: <code>\gamename\xonotic\modname\data\gameversion\20000\sv_maxclients\24\clients\4\bots\2\mapname\dance\hostname\[o8] Nexican v2.5.1\protocol\3\qcstatus\ctf:2.5.1::score!!:score!!,caps!:5:22,1:14:73,3\challenge\tortilla</code>
     * @param queryResult String of the server info.
     * @param ipStr The address of the server.
     * @return Server which is created.
     */
    private Server getServerFromResponse(final String queryResult, final String ipStr) {
        final String[] serverData = queryResult.split("\\\\");
        final Server server = new Server();
        server.setGame(serverData[2]);
        server.setModname(serverData[4]);
        server.setGameVersion(serverData[6]);
        server.setMaxPlayers(serverData[8] as int);
        server.setPlayerCount(serverData[10] as int);
        if (serverData[11].equals("bots")) {
            server.setBotCount(Integer.parseInt(serverData[12]));
            server.setMap(serverData[14]);
            server.setHostname(serverData[16]);
            server.setQcstatus(serverData[20]);
        } else {
            // queryResult does not contain bot count
            server.setMap(serverData[12]);
            server.setHostname(serverData[14]);
        }
        server.setPing(ping);
        server.setIp(ipStr);
        return server;
    }
}
