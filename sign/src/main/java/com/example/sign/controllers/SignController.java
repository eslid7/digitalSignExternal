package com.example.sign.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.sign.entitys.SignEntity;

@RestController
public class SignController {
	
	@GetMapping("/responseSignPDF")
	public ResponseEntity<SignEntity> getResponseLogin( HttpServletRequest request, @RequestParam(required = false, defaultValue = "", value = "tokenTime") String tokenTimeAtrr) {
		SignEntity dataSign = new SignEntity();
		
		String signProcess = System.getProperty("signProcess");
		String signFileName =  System.getProperty("signFileName");
		String signError =  System.getProperty("signError");
		
		if(signFileName!=null && signFileName.length()>1 && tokenTimeAtrr.equals(signProcess)) {
			dataSign.setNameFile(signFileName);
			dataSign.setIdProcess(signProcess);
			System.setProperty("signProcess","");
			System.setProperty("signFileName","");
			return ResponseEntity.ok(dataSign);
		} else if(signError.length()>1){
			String [] arrayError = signError.split(",");
			dataSign.setError(arrayError[0]);
			dataSign.setIdProcess(arrayError[1]);
			System.setProperty("signError","");
			return ResponseEntity.status(400).body(dataSign);
		} else {
			return ResponseEntity.ok(null);
		}
		
		
	}

}