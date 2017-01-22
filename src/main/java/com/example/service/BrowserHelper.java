package com.example.service;

import java.io.IOException;

public class BrowserHelper {

	public void launchBrowser(){
		try {
			Runtime.getRuntime().exec("cmd /c start http://localhost:8080/");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
