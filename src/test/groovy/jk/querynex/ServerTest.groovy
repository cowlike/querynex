package jk.querynex;

import java.util.List;

import groovy.util.GroovyTestCase;

class ServerTest extends GroovyTestCase {

	List<Player> playerList, playerList2

	protected void setUp() throws Exception {
		super.setUp();
		playerList = [
			new Player('name':'bot1', 'coloredName':'bot1', 'score':25, 'ping':0, 'team':0),
			new Player('name':'bot2', 'coloredName':'bot2', 'score':15, 'ping':0, 'team':0),
			new Player('name':'jackson', 'coloredName':'j', 'score':1, 'ping':50, 'team':0),
			new Player('name':'jake', 'coloredName':'jake', 'score':-666, 'ping':40, 'team':0)
		]
		playerList2 = [
			new Player('name':'bot1', 'coloredName':'bot1', 'score':25, 'ping':0, 'team':0),
			new Player('name':'bot2', 'coloredName':'bot2', 'score':15, 'ping':0, 'team':0),
			new Player('name':'jackson', 'coloredName':'j', 'score':1, 'ping':50, 'team':0),
			new Player('name':'jake', 'coloredName':'jake', 'score':-666, 'ping':40, 'team':0)
		]
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testSetQcstatus() {
		//fail("Not yet implemented");
	}

	public void testToLongString() {
		def s = new Server('hostname':'mojo', 'nick':'S', 'map':'q3dm17ish', 'maxPlayers':20, 'playerList':playerList)
		assert(s.toLongString() == """mojo
    map [q3dm17ish], max players [20]
    players: [(bot)bot1, (bot)bot2, jackson, -jake]""")
	}

	public void testToString() {
		def s = new Server('hostname':'mojo', 'nick':'S', 'map':'q3dm17ish', 'maxPlayers':20, 'playerList':playerList, 'ip':'S:10.0.0.5:24000')
		assert(s.toString() == 'S:q3dm17ish, (bot)bot1, (bot)bot2, jackson, -jake')
	}

	public void testEquals() {
		def s1 = new Server('hostname':'mojo', 'map':'q3dm17ish', 'maxPlayers':20, 'playerList':playerList, 'ip':'S:10.0.0.5:24000')
		def s2 = new Server('hostname':'mojo', 'map':'q3dm17ish', 'maxPlayers':20, 'playerList':playerList2, 'ip':'S:10.0.0.5:24000')
		def s3 = new Server('hostname':'mojo', 'map':'q3dm18ish', 'maxPlayers':20, 'playerList':playerList, 'ip':'S:10.0.0.5:24000')
		assert s1 == s2
		assert s1 != s3
	}
}
