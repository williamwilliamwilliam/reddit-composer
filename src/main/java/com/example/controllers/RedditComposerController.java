package com.example.controllers;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.repository.OutboxRepository;
import com.example.service.OutboxService;
import com.example.service.RedditService;
import com.example.vo.RedditCredentials;
import com.example.vo.RedditOutbox;

import net.dean.jraw.RedditClient;

@Controller
public class RedditComposerController {
	
	@Autowired private OutboxService outboxService;
	@Autowired private RedditService redditService;
	
	@RequestMapping(value="testCredentials", method=RequestMethod.POST)
	public ResponseEntity<?> test(@RequestBody RedditCredentials redditCredentials){
		RedditClient redditClient = redditService.getRedditClient(redditCredentials);
		return ResponseEntity.ok(redditClient.me());
	}
	
	@RequestMapping(value="existingSessionOutbox", method=RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getExistingOutbox(HttpServletRequest request){
		return ResponseEntity.ok(outboxService.getExistingOutbox(request));
	}
	
	@RequestMapping(value="processOutbox", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> processOutbox(HttpServletRequest request, @RequestBody RedditOutbox outbox){
		outboxService.processOutbox(request, outbox);
		return ResponseEntity.ok(outbox);
	}
	
	
}
