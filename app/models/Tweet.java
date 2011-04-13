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
package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Lob;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;
import play.modules.elasticsearch.annotations.ElasticSearchable;

/**
 * The Class Tweet.
 */
@ElasticSearchable
@Entity
public class Tweet extends Model {

	/** The tweet id. */
	@Required
	public long tweetId;

	/** The text. */
	@Lob
	@Required
	@MaxSize(2000)
	public String text;

	/** The created at. */
	@Required
	public Date createdAt;

	/** The user. */
	@Required
	public String user;

	/**
	 * Instantiates a new tweet.
	 * 
	 * @param tweetId
	 *            the tweet id
	 * @param text
	 *            the text
	 * @param createdAt
	 *            the created at
	 * @param user
	 *            the user
	 */
	public Tweet(long tweetId, String text, Date createdAt, String user) {
		this.tweetId = tweetId;
		this.createdAt = createdAt;
		this.user = user;
		this.text = this.parseTweed(text);
	}

	/**
	 * Parses the tweed.
	 * 
	 * @param text
	 *            the text
	 * @return the string
	 */
	private String parseTweed(String text) {
		String[] chunks = text.split(" ");
		String newText = "";
		for (int j = 0; j < chunks.length; j++) {
			String string = chunks[j];
			if (string.startsWith("@")) {

				String displayname = this.checkEndsWith(string);
				String username = displayname.substring(1, displayname.length());
				chunks[j] = "<a href=\"http://twitter.com/" + username + "\" target=\"_blank\">" + displayname + "</a>";
			}
			if (string.startsWith("#")) {
				String displayhash = this.checkEndsWith(string);
				String hash = displayhash.substring(1, displayhash.length());
				chunks[j] = "<a href=\"http://twitter.com/#!/search?q=%23" + hash + "\" target=\"_blank\">" + displayhash + "</a>";
			}
			if (string.startsWith("http")) {
				chunks[j] = "<a href=\"" + string + "\" target=\"_blank\">" + string + "</a>";
			}
			if (string.startsWith("www")) {
				chunks[j] = "<a href=\"http://" + string + "\" target=\"_blank\">" + string + "</a>";
			}
			newText += " " + chunks[j];
		}
		return newText;
	}

	/**
	 * Check ends with.
	 * 
	 * @param string
	 *            the string
	 * @return the string
	 */
	private String checkEndsWith(String string) {
		if (string.length() == 1) {
			return string;
		}
		String ends = string.substring(string.length() - 1, string.length());
		if (ends.matches("\\W")) {
			if (ends.equals("_")) {
				return string;
			} else {
				return this.checkEndsWith(string.substring(0, string.length() - 1));
			}
		} else {
			return string;
		}
	}
}
