package jk.querynex

import jk.querynex.notify.*;

class Main {
	static showPlayer(player) {
		"${player.isSpec()? '-': '+'}${player.isBot()? '(bot)': ''}${player.name}"
	}

	static showServer(server) {
		println "\n${new Date()}: ${server?.hostname}:"
		println "\tmap [${server?.map}], max players [${server?.maxPlayers}]"

		println "\tplayer list:"
		server?.playerList.each {
			println "\t\t${showPlayer(it)}"
		}
	}

	static iterations(int n) {
		{ -> n-- > 0 }
	}
		
	static watch(notifier, query, url, playerFilters, loopCondition) {
		def safeVal = {
			defVal, clos ->
			def val = defVal
			try {
				val = clos()
			} catch(e) {
				println e.message
			}
			val
		}

		//The last person left the server
		def transitionToEmpty = { server ->
			java.awt.Toolkit.defaultToolkit.beep()
			def msg = "${new Date()}: ${server?.hostname} is now empty"
			println "\n$msg"
			notifier.send(msg)
		}

		//Someone entered the empty server
		def transitionToPopulated = { server ->
			java.awt.Toolkit.defaultToolkit.beep()
			showServer server
			notifier.send("server $url is populated")
		}

		//Server remains populated
		def populated = { server ->
			println server?.hostname + ": " + server?.map
			server?.playerList?.each { println "\t${showPlayer it}" }
		}

		//Server remains empty
		def empty = { server ->
			println "${server?.hostname}..."
		}

		def EMPTY = "empty"
		def USERS = "users"
		def actions = [
			(EMPTY) : [
				"foundUsers" : {server -> transitionToPopulated(server); USERS},
				"none" : {server -> empty(server); EMPTY}],
			(USERS) : [
				"foundUsers" : {server -> populated(server); USERS},
				"none" : {server -> transitionToEmpty(server); EMPTY}]
		]

		for (def cur = EMPTY; loopCondition(); ) {
			def server = safeVal(null, {query.getStatus(url)})
			
			/*
			 *  apply filters to player list. reduce the list to those elements passing all the filters
			 */
			if (server && playerFilters) {
				server.playerList = server.playerList.findAll { el -> playerFilters.inject(true) { t, f -> t && f(el) } }
			}
			def evt = server?.playerList?.size() > 0 ? "foundUsers" : "none"
			cur = actions[cur][evt](server)
			sleep(60000)
		}
	}

	static main(args) {
		def cli = new CliBuilder(usage:'Possible options')
		cli.a(argName:'active only', 'Only show active players, not specs')
		cli.b(argName:'show Bots', 'Also show bots in the player list')
		cli.h('This screen')
		cli.f(args:1, argName:'filename', 'Read a file of lines with ip:port')
		cli.i(args:1, argName:'iterations', 'Number of times to check the servers')
		cli.s(args:1, argName:'ip:port', 'Specify the server ip:port')
		cli.w(argName:'watch', 'Watch server forever. Has priority over number of iterations (-i)')
		def options = cli.parse(args)
		def playerFilters = []

		if (options.h) {
			cli.usage()
			System.exit(0)
		}

		if (options.a) {
			playerFilters << { !it.isSpec() }
		}
		
		//we filter out bots unless explicitly asked for with '-b'
		if (!options.b) {
			playerFilters << { !it.isBot() }
		}
		
		def waitForever = options.w
		def iters = options.i ? options.i as int : 1
		def urls

		if (options.f) {
			urls = new File(options.f).readLines().findAll {ln -> ln && !ln.startsWith('#')}
		}
		else if (options.s) {
			urls = [options.s]
		}
		else {
			urls = ['127.0.0.1:26000']
		}

		def query = new ServerQuery()
		def threadList = urls.inject([]) {lst, url -> 
			lst << Thread.startDaemon { 
				watch(new FakeNotifier(), query, url, playerFilters, waitForever ? {true} : iterations(iters)) }; 
			lst
		}
		
		if (waitForever) {
			println "hit <enter> key to terminate"
			System.in.read();
		}
		else {
			println "waiting for $iters server quer${iters > 1 ? 'ies' : 'y'}"
			threadList.each { it.join() }
		}
		println "done"
	}
}