package jk.querynex

import jk.querynex.notify.*;
import org.apache.commons.cli.Option

class Main {
	static String timeStamp(s) {
		new Date().format('MM/dd HHmm') + (s ? ": $s" : '')
	}

	static beep() {
		java.awt.Toolkit.defaultToolkit.beep()
	}

	static iterations(int n) {
		{ -> --n > 0 }
	}

	/*
	 * Safely get a value from calling a closure. On exception we
	 * will return a default value passed as the first argument
	 */
	static safeVal(defVal, clos) {
		def val = defVal
		try {
			val = clos()
		} catch(e) {
			e.printStackTrace()
		}
		val
	}

	/*
	 * The loopCondition() test is done at the end of the loop
	 * and should return true if we want to iterate again. Returning
	 * false exits the loop.
	 */
	static watch(List<INotifier> notifiers,
			ServerQuery query,
			String url,
			List<Closure>playerFilters,
			Closure loopCondition) {

		def curServer = null

		/*
		 * State transition closures
		 * 
		 * Notifications are sent on 3 conditions:
		 * 1. someone joins the empty server
		 * 2. server becomes empty
		 * 3. map or player list changes while server is populated.
		 */
		//The last person left the server
		def transitionToEmpty = { Server server ->
			beep()
			notifiers.each { n -> n.send(timeStamp(server)) }
		}

		//Someone entered the empty server
		def transitionToPopulated = { Server server ->
			curServer = server
			beep()
			notifiers.each { n -> n.send(timeStamp(server)) }
		}

		//Server remains populated
		def populated = { Server server ->
			if (server != curServer) {
				curServer = server
				notifiers.each { n -> n.send(timeStamp(server)) }
			}
		}

		//Server remains empty
		def empty = { Server server -> }

		def actions = [
			(Population.EMPTY) : [
				(Population.USERS) : {server -> transitionToPopulated(server); Population.USERS},
				(Population.EMPTY) : {server -> empty(server); Population.EMPTY}],
			(Population.USERS) : [
				(Population.USERS) : {server -> populated(server); Population.USERS},
				(Population.EMPTY) : {server -> transitionToEmpty(server); Population.EMPTY}]
		]

		/*
		 * We'll break out of this inside the loop so we don't sleep unnecessarily
		 */
		for (def cur = Population.EMPTY; ; ) {
			Server server = safeVal(null, {query.getStatus(url)})

			/*
			 *  apply filters to player list. reduce the list to those elements passing all the filters
			 */
			if (server && playerFilters) {
				server.playerList = server.playerList.findAll { el -> playerFilters.inject(true) { t, f -> t && f(el) } }
			}
			def evt = server?.isEmpty() ? Population.EMPTY : Population.USERS
			cur = safeVal(cur, {actions[cur][evt](server)})
			
			//keep looping?
			if (! loopCondition()) {
				break
			}
			sleep(60000)
		}
	}

	static main(args) {
		List<INotifier> notifiers = []
		
		println "querynex ${Main.package.implementationVersion}"

		def cli = new CliBuilder(usage:'Possible options')
		cli.a(argName:'active only', 'Only show active players, not specs')
		cli.b(argName:'show Bots', 'Also show bots in the player list')
		cli.h('This screen')
		cli.f(args:1, argName:'filename', 'Read a file of lines with ip:port')
		cli.i(args:1, argName:'iterations', 'Number of times to check the servers')
		cli.n(args:Option.UNLIMITED_VALUES, argName:'notifier', 'use external notifier (twitter or console)')
		cli.s(args:1, argName:'ip:port', 'Specify the server ip:port')
		cli.t('publish a test notification and exit')
		cli.w(argName:'watch', 'Watch server forever. Has priority over number of iterations (-i)')

		def options = cli.parse(args)
		def playerFilters = []

		if (options.h) {
			cli.usage()
			System.exit(0)
		}

		options.ns.each { n ->
			switch (n) {
				case 'twitter':
					notifiers << new TwitterSender()
					break
				case 'console':
					notifiers << ['send':{ msg -> println msg }] as INotifier
					break
				case 'test':
					notifiers << ['send':{ msg -> println "test: $msg" }] as INotifier
					break
				default:
					notifiers << ['send':{ msg -> println "console default: $msg"}] as INotifier
					break
			}
		}

		if (options.t) {
			notifiers.each { it.send(timeStamp("test msg")) }
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
			urls = ['S:' + options.s]
		}
		else {
			urls = ['S:127.0.0.1:26000']
		}

		def query = new ServerQuery()
		def threadList = urls.inject([]) {lst, url ->
			lst << Thread.startDaemon {
				watch(notifiers, query, url, playerFilters, waitForever ? {true} : iterations(iters)) };
			lst
		}

		if (waitForever) {
			println "hit <ctl>-c key to terminate"
		}
		else {
			println "waiting for $iters server quer${iters > 1 ? 'ies' : 'y'}"
		}
		threadList.each { it.join() }
		println "done"
	}
}