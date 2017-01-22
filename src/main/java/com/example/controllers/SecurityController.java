package com.example.controllers;

import java.util.HashMap;
import java.util.Map;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.service.SecurityService;

@Controller
public class SecurityController {

@Autowired private SecurityService securityService;
	
	@RequestMapping(value="username", method=RequestMethod.GET)
	public ResponseEntity<?> getHashOfSessionId(HttpServletRequest request){
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("username", securityService.getSessionHash(request));
		return ResponseEntity.ok(data);
	}
	
}
