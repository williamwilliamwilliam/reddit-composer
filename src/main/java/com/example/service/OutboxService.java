package com.example.service;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.example.repository.OutboxRepository;
import com.example.vo.RedditMessage;
import com.example.vo.RedditOutbox;

import net.dean.jraw.ApiException;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.managers.InboxManager;

@Service
public class OutboxService {

	@Autowired private WebSocketService webSocketService;
	@Autowired private SecurityService securityService;
	@Autowired private OutboxRepository outboxRepository;
	@Autowired private RedditService redditService;
	
	public void processOutbox(HttpServletRequest request, RedditOutbox outbox){
		String clientsChannel = securityService.getSessionHash(request);
		
		webSocketService.sendProcessingUpdateToClient(clientsChannel, "Saving your data in case you refresh the page...", 1d);
		saveOutboxToServerInCaseUserRefreshes(request, outbox);
		
		sleep(500);
		
		webSocketService.sendProcessingUpdateToClient(clientsChannel, "Validating your spreadsheet data...", 2d);
		validateOutbox(outbox);
		
		sleep(500);
		
		webSocketService.sendProcessingUpdateToClient(clientsChannel, "Validating your Reddit Credentials...", 3d);
		RedditClient redditClient = redditService.getRedditClient(outbox);
		
		composeRedditMessages(clientsChannel, outbox, redditClient, request);
		
		webSocketService.sendProcessingUpdateToClient(clientsChannel, "All done!", 100d);
		
		//sleep for a little bit to let the user know processing is all done
		sleep(2000);
	}
	
	private void composeRedditMessages(String clientsChannel, RedditOutbox outbox, RedditClient redditClient, HttpServletRequest request) {
		InboxManager inbox = new InboxManager(redditClient);
		Double processingProgress = 5d;
		//make sure we update the progress bar the appropriate amount (based on how many messages we're sending)
		double remainingProgress = 100d-processingProgress;
		double messageProgressValue = remainingProgress/outbox.getMessages().size();
		Integer index = 0;
		for(RedditMessage message:outbox.getMessages()){
			
			webSocketService.sendProcessingUpdateToClient(clientsChannel, "("+(index+1)+"/"+outbox.getMessages().size()+") Sending message to "+message.getTo()+"...", processingProgress);
			
			sleep(2000); //let's not overwhelm reddit's API
			
			try {
				inbox.compose(message.getTo(), message.getSubject(), message.getMessage());
				setSavedMessageAsSent(request, index);
				webSocketService.sendMessageUpdateToClient(clientsChannel, message.getIndex(), "Sent");
			} catch (NetworkException | ApiException e) {
				webSocketService.sendMessageUpdateToClient(clientsChannel, message.getIndex(), "Error - "+e.getMessage());
				e.printStackTrace();
			}
			
			processingProgress = processingProgress + messageProgressValue;
			index++;
		}
	}

	private void sleep(long milliseconds){
		try {
		    Thread.sleep(2000);
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
	}

	private void setSavedMessageAsSent(HttpServletRequest request, Integer index) {
		outboxRepository.getSessionOutboxes().get(securityService.getSessionHash(request)).getMessages().get(index).setStatus("Sent");
	}

	private void saveOutboxToServerInCaseUserRefreshes(HttpServletRequest request, RedditOutbox outbox) {
		outboxRepository.getSessionOutboxes().put(securityService.getSessionHash(request), outbox);
	}

	private void validateOutbox(RedditOutbox outbox) {
		if(outbox == null){
			throw new IllegalArgumentException("Fill out the credentials and a list of messages to send.");
		}
		
		removeEmptyRows(outbox.getMessages());
		
		removeSentRows(outbox.getMessages());
		
		if(CollectionUtils.isEmpty(outbox.getMessages())){
			throw new IllegalArgumentException("Fill out a list of messages to send.");
		}
		
		for(int i = 0; i < outbox.getMessages().size(); i++){
			if(StringUtils.isBlank(outbox.getMessages().get(i).getTo())){
				throw new IllegalArgumentException("Row "+(i+1)+" has an empty TO");
			}
			if(StringUtils.isBlank(outbox.getMessages().get(i).getSubject())){
				throw new IllegalArgumentException("Row "+(i+1)+" has an empty SUBJECT");
			}
			if(StringUtils.isBlank(outbox.getMessages().get(i).getMessage())){
				throw new IllegalArgumentException("Row "+(i+1)+" has an empty MESSAGE");
			}
		}
	}

	private void removeSentRows(List<RedditMessage> messages) {
		Iterator<RedditMessage> iterator = messages.iterator();
		while(iterator.hasNext()){
			RedditMessage message = iterator.next();
			if(StringUtils.equalsIgnoreCase("Sent", message.getStatus())){
				iterator.remove();
			}
		}
	}

	private void removeEmptyRows(List<RedditMessage> messages) {
		Iterator<RedditMessage> iterator = messages.iterator();
		while(iterator.hasNext()){
			RedditMessage message = iterator.next();
			if(StringUtils.isBlank(message.getTo()) && StringUtils.isBlank(message.getSubject()) && StringUtils.isBlank(message.getMessage())){
				iterator.remove();
			}
		}
	}

	public RedditOutbox getExistingOutbox(org.apache.catalina.servlet4preview.http.HttpServletRequest request) {
		return outboxRepository.getSessionOutboxes().get(securityService.getSessionHash(request));
	}
}
