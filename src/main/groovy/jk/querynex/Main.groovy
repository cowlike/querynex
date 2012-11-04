package jk.querynex

class Main {
	class MyAuth extends Authenticator {}
	
	static mailer = new MailSender(
		'to':['myemailaccount@sample.com'],
		'from':'nexbot@sample.com',
		'subject':'Nex update')
	
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
		
		def transitionToEmpty = {
			java.awt.Toolkit.defaultToolkit.beep()
			def msg = "${new Date()}: $url is empty"  
			println "\n$msg"
			//mailer.content = msg
			//mailer.send()
		}
		
		def transitionToPopulated = {
			java.awt.Toolkit.defaultToolkit.beep()
			showServer safeVal(null, {query.getStatus url})
			//mailer.content = "server $url is populated"
			//mailer.send()
		}
		
		def populated = {
			//print '+'
			println safeVal(null, {query.getStatus url})?.playerList?.
				collect {"${it.isSpec()? '-': '+'}${it.name}"}
		}
		
		def EMPTY = "empty"
		def USERS = "users"		
		def actions = [
			(EMPTY) : [
				"foundUsers" : {transitionToPopulated(); USERS},
				"none" : {print "."; EMPTY}],
			(USERS) : [
				"foundUsers" : {populated(); USERS},
				"none" : {transitionToEmpty(); EMPTY}]
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