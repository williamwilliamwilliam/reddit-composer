package com.example.service;

import org.springframework.stereotype.Service;

import com.example.vo.RedditCredentials;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.http.oauth.OAuthException;

@Service
public class RedditService {

	public RedditClient getRedditClient(RedditCredentials redditCredentials){
		UserAgent myUserAgent = UserAgent.of("desktop", "reddit-composer", "v0.1", redditCredentials.getUserID());
		RedditClient redditClient = new RedditClient(myUserAgent);
		Credentials credentials = Credentials.script(
				redditCredentials.getUserID(), 
				redditCredentials.getPassword(), 
				redditCredentials.getClientID(), 
				redditCredentials.getClientSecret());
		
		OAuthData authData;
		try {
			authData = redditClient.getOAuthHelper().easyAuth(credentials);
		} catch (NetworkException | OAuthException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		redditClient.authenticate(authData);
		
		return redditClient;
	}
}
