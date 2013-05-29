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
		twitter.updateStatus(status)
	}
	
	void send(String msg) {
		update(msg)
	}
}