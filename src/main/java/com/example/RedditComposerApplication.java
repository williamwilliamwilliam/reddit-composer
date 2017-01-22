package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.service.BrowserHelper;

@SpringBootApplication
public class RedditComposerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedditComposerApplication.class, args);
		
		new BrowserHelper().launchBrowser();
	}
}
