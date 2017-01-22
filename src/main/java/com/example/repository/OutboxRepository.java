package com.example.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.example.vo.RedditOutbox;

@Repository
public class OutboxRepository {
	private Map<String, RedditOutbox> sessionOutboxes = new ConcurrentHashMap<String, RedditOutbox>();

	public Map<String, RedditOutbox> getSessionOutboxes() {
		return sessionOutboxes;
	}

	public void setSessionOutboxes(Map<String, RedditOutbox> sessionOutboxes) {
		this.sessionOutboxes = sessionOutboxes;
	}
}
