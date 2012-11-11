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
		
		//The last person left the server
		def transitionToEmpty = {
			java.awt.Toolkit.defaultToolkit.beep()
			def msg = "${new Date()}: $url is empty"  
			println "\n$msg"
			//mailer.content = msg
			//mailer.send()
		}
		
		//Someone entered the empty server
		def transitionToPopulated = {
			java.awt.Toolkit.defaultToolkit.beep()
			showServer safeVal(null, {query.getStatus url})
			//mailer.content = "server $url is populated"
			//mailer.send()
		}
		
		//Server remains populated
		def populated = {
			println safeVal(null, {query.getStatus url})?.playerList?.
				collect {"${it.isSpec()? '-': '+'}${it.name}"}
		}
		
		//Server remains empty
		def empty = {
			print "."
		}
		
		def EMPTY = "empty"
		def USERS = "users"		
		def actions = [
			(EMPTY) : [
				"foundUsers" : {transitionToPopulated(); USERS},
				"none" : {empty(); EMPTY}],
			(USERS) : [
				"foundUsers" : {populated(); USERS},
				"none" : {transitionToEmpty(); EMPTY}]
		]
			
		for (def cur = EMPTY; true; ) {
			def evt = safeVal(-1, {query.getInfo(url)?.playerCount}) > 0 ? "foundUsers" : "none"
			cur = actions[cur][evt]()
			sleep(60000)
		}
	}
	
	static main(args) {
		def cli = new CliBuilder(usage:'Possible options')
		cli.h('This screen')
		cli.f(args:1, argName:'filename', 'Read a file of lines with ip:port')
		cli.s(args:1, argName:'ip:port', 'Specify the server ip:port')		
		def options = cli.parse(args)
		
		if (options.h) {
			cli.usage()
			System.exit(0)
		}
		
		def urls = ['198.23.132.34:26000']
		
		if (options.f) {
			urls = new File(options.f).readLines()
		}
		else if (options.s) {
			urls = [options.s]
		}
		
		def query = new ServerQuery()
		
		if (urls.size() > 1) {
			urls.each {
				try {showServer query.getStatus(it); println ''} catch(e) {print "${e.message}\n\n"} 
			}
		}
		else {
			println "hit <enter> key to terminate"
			Thread.startDaemon { watchForever(query, urls.first()) }
			System.in.read();
			println "done"
		}
	}
}