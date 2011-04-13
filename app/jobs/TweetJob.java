/** 
 * Copyright 2011 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * @author Felipe Oliveira (http://mashup.fm)
 * 
 */
package jobs;

import java.util.List;

import models.Tweet;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * The Class TweetJob.
 */
@Every("30s")
public class TweetJob extends Job {

	/**
	 * Aggregate Tweets
	 * 
	 * @see play.jobs.Job#doJob()
	 */
	@Override
	public void doJob() {
		try {
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setDebugEnabled(true).setOAuthConsumerKey("5ZlLNKbeY6s04bsmUfQLiw").setOAuthConsumerSecret("DfbbpK0Qoj1lXlhaRWYsXbWFtxK6m7BpMSPAdI").setOAuthAccessToken("121620541-jALAu8Arg7PyaNEINDrjrDLqlIUTnD6BXEthSi21").setOAuthAccessTokenSecret("VahfsSz2kBvCvl9zPq2rUX77VI389SUoijGcvqR7mLE");
			TwitterFactory tf = new TwitterFactory(cb.build());
			Twitter twitter = tf.getInstance();

			List<Status> statuses = twitter.getUserTimeline();
			for (Status status : statuses) {
				Tweet checktweet = Tweet.find("tweetId", status.getId()).first();
				if (checktweet == null) {
					Tweet tweet = new Tweet(status.getId(), status.getText(), status.getCreatedAt(), status.getUser().getName());
					Logger.info("Tweet: %s", tweet.text + " -- " + tweet.tweetId + " --- " + tweet.createdAt + " --- " + tweet.user);
					tweet.save();
				}
			}
		} catch (TwitterException e) {
			Logger.error(e, "TwitterException: %s", e.getLocalizedMessage());
		}

	}
}
