package jk.querynex.notify

import twitter4j.*;
import twitter4j.auth.*;
import twitter4j.conf.*;

/*
 * relies on twitter4j property file configuration with these entries:
 * 
 * oauth.consumerKey=*********************
 * oauth.consumerSecret=******************************************
 * oauth.accessToken=**************************************************
 * oauth.accessTokenSecret=******************************************
 */
class TwitterSender implements INotifier {
	private Twitter twitter;

	TwitterSender() {
		def builder = new ConfigurationBuilder()
		def factory = new TwitterFactory(builder.build())
		twitter = factory.getInstance()
	}

	Status update(status) {
		try {
			twitter.updateStatus(status.take(135)) //140 is max chars per tweet
		}
		catch (Exception e) {
			println ("failed to update: (${status.take(139)}) -> $e.message")
			e.printStackTrace()
		}
	}
	
	void send(String msg) {
		update(msg)
	}
}