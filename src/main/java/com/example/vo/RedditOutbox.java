package com.example.vo;

import java.util.List;

public class RedditOutbox extends RedditCredentials {
	
	private List<RedditMessage> messages;

	public List<RedditMessage> getMessages() {
		return messages;
	}

	public void setMessages(List<RedditMessage> messages) {
		this.messages = messages;
	}

}
