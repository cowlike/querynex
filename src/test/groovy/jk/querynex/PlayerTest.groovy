package jk.querynex;

import groovy.util.GroovyTestCase;

class PlayerTest extends GroovyTestCase {

	List<Player> playerList
	
	protected void setUp() throws Exception {
		super.setUp();
		playerList = [
			new Player('name':'bot1', 'coloredName':'bot1', 'score':25, 'ping':0, 'team':0),
			new Player('name':'bot2', 'coloredName':'bot2', 'score':15, 'ping':0, 'team':0),
			new Player('name':'jackson', 'coloredName':'j', 'score':1, 'ping':50, 'team':0),
			new Player('name':'jake', 'coloredName':'jake', 'score':-666, 'ping':40, 'team':0)
			] 
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testIsBot() {
		assert(playerList[0].isBot())
	}

	public void testIsSpec() {
		assert(playerList[3].isSpec())
	}

	public void testSetName() {
		playerList[0].setName('botto')
		assert(playerList[0].name == 'botto')
	}

	public void testToString() {
		assert(playerList.toString() == '[+(bot)bot1, +(bot)bot2, +jackson, -jake]')
	}

}
