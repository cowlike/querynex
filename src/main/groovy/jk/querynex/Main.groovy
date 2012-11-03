package jk.querynex

class Main {
	static showServer(server) {
		println "\n${new Date()}: ${server?.hostname}:"
		println "\tmap [${server?.map}], max players [${server?.maxPlayers}]"
		
		println "\tplayer list:"
		server?.playerList.each { player ->
			println "\t\t${player.isSpec()? '-': '+'}${player.isBot()? '(bot)': ''} ${player.name}" }
	}
	
	static watchForever(query, url) {
		def safeVal = { defVal, clos ->
			def val = defVal
			try {val = clos()} catch(e) {println e.message}
			val
		}
		
		def EMPTY = "empty"
		def USERS = "users"		
		def actions = [
			(EMPTY) : [
				"foundUsers" : {showServer safeVal(null, {query.getStatus url}); USERS},
				"none" : {print "."; EMPTY}],
			(USERS) : [
				"foundUsers" : {print "+"; USERS},
				"none" : {println "\n${new Date()}: $url is empty"; EMPTY}]
		]
			
		for (def cur = EMPTY; true; ) {
			if (System.in.available() > 0)
				break;
			def evt = safeVal(-1, {query.getInfo(url)?.playerCount}) > 0 ? "foundUsers" : "none"
			cur = actions[cur][evt]()
			sleep(60000)
		}
	}
	
	static main(args) {
		def urls = args ? new File(args[0]).readLines() : ['198.23.132.34:26000']
		def query = new ServerQuery()
		
		if (urls.size() > 1) {
			urls.each {
				try {showServer query.getStatus(it); println ''} catch(e) {print "${e.message}\n\n"} 
			}
		}
		else
			watchForever(query, urls.first())
	}
}