package jk.querynex

import Player;

import java.util.List;

class Server {
	String ip;
	String hostname;
	String map;
	String game;
	String gameVersion;
	String modname;
	String gameType;
	int playerCount;
	int maxPlayers;
	int ping;
	int botCount;
	List<Player> playerList;
	boolean favorite = false;

	public void setHostname(final String newHostName) {
		hostname = PlayerUtils.sanitizeName(newHostName);
	}

	public void setQcstatus(final String string) {
		gameType = string.split(':')[0];
	}

	String toString() {
		def data = [map]
		playerList.inject(data) { t,v ->
			t << v
		}
		data.join ", "
	}

	String toLongString() {
		"""${hostname}
    map [$map], max players [$maxPlayers]
    players: $playerList"""
	}

	public boolean isEmpty() { playerList.isEmpty() }
	
	public boolean equals(Object other) {
		other && this.class == other.class &&
				this.map == ((Server) other).map &&
				this.playerList == ((Server) other).playerList
	}
}
