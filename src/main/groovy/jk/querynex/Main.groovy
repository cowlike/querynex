package jk.querynex

class Main {
	static showServer(server) {
		println "${server?.hostname}:"
		println "\tmap [${server?.map}], max players [${server?.maxPlayers}]"
		
		println "\tplayer list:"
		server?.playerList.each { player ->
			println "\t\t${player.isSpec()? '-': '+'}${player.isBot()? '(bot)': ''} ${player.name}" }
	}
	
	static watch(query, url) {
		while (!query.getInfo(url)?.playerCount) {
			print "."
			Thread.sleep(60000)
		}
		println ''		
		showServer query.getStatus(url)
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
			watch(query, urls.first())
	}
}