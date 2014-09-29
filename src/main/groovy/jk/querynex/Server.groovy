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
		"""${hostname}
    map [$map], max players [$maxPlayers]
    players: $playerList"""
	}

	String toShortString() {
		def data = [
			ip.split(':')[0],
			map
		]
		playerList.inject(data) {t,v -> t << v}
		data.join ", "
	}
}
