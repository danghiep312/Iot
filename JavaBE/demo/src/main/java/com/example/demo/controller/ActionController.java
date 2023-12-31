package com.example.demo.controller;

import java.io.IOException;
import java.sql.Timestamp;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dao.ActionDao;
import com.example.demo.model.Action;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@RestController

public class ActionController {
	private int lastTime = -1;
	private ActionDao actionDao = new ActionDao();
	
	@PostMapping("/addaction")
	public void addAction(@RequestBody String payload) {
		System.out.println(payload + "   add action");
		payloadHandle(payload);
	}
	
	@GetMapping("/actioncount")
	public String getCountAction(@RequestParam String device) {
		//System.out.println("count " + device);
		return actionDao.getCountOfAction(device);
		
	}
	
	private void payloadHandle(String payload) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			
			JsonNode jsonNode = objectMapper.readTree(payload);
			
			JsonNode led = jsonNode.get("led");
			JsonNode fan = jsonNode.get("fan");
			JsonNode seconds = jsonNode.get("seconds");
			if (seconds != null && seconds.asInt() == lastTime) return;
			lastTime = seconds.asInt();
			
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			
			if (led != null) {
				actionDao.AddActionToDb(new Action("led", led.asText(), timestamp));
			}
			
			if (fan != null) {
				actionDao.addActionToDb(new Action("fan", fan.asText(), timestamp));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	@GetMapping("/action")
	public ResponseEntity<?> getPatients () throws IOException{
		return actionDao.getAllAction();
	}
	
}
