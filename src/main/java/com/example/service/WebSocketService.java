package com.example.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class WebSocketService {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired 
	private SimpMessagingTemplate simpMessagingTemplate;
	
	public void sayHello(String subscription){
		sendMessage(subscription, "Hello, yall");
	}
	
	public void sendProcessingStatusToClient(String subscription, String status){
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("messageType", "statusMessage");
		data.put("message", status);
		sendMessage(subscription, data);
	}
	
	public void sendProcessingProgressToClient(String subscription, Double progress){
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("messageType", "statusMessage");
		data.put("progress", progress);
		sendMessage(subscription, data);
	}
	
	public void sendProcessingUpdateToClient(String subscription, String status, Double progress){
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("messageType", "statusMessage");
		data.put("progress", progress);
		data.put("message", status);
		sendMessage(subscription, data);
	}
	
	public void sendMessageUpdateToClient(String subscription, int index, String status){
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("messageType", "outboxUpdate");
		data.put("index", index);
		data.put("status", status);
		sendMessage(subscription, data);
	}
	
	
	
	public void sendMessage(String subscription, Object object){
		try {
			String json = new ObjectMapper().writeValueAsString(object);
			log.info("Sending message to {}", subscription);
			simpMessagingTemplate.convertAndSend(subscription, json);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
	
}
