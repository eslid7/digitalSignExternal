
package com.example.sign.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.example.sign.entitys.UserEntity;

@RestController
public class LoginController {
	
	@RequestMapping(value="/viewAuth", method = RequestMethod.GET)
    public ModelAndView viewLogin() { 
		ModelAndView mv = new ModelAndView();

		
		mv.setViewName("viewLogin");
		return mv;
    }
	
	@GetMapping("/responselogin")
	public ResponseEntity<UserEntity> getResponseLogin( HttpServletRequest request, @RequestParam(required = false, defaultValue = "", value = "tokenTime") String tokenTimeAtrr) {

	    String dataUser = System.getProperty("userData");
	    String loginError = System.getProperty("loginError");
	    String finshInstalation = System.getProperty("finshInstalation");	

    	System.out.println(dataUser);
    	System.out.println(loginError);
    	
    	if(finshInstalation!= null && finshInstalation.equals(tokenTimeAtrr)) {
    		UserEntity user = new UserEntity();			
			user.setFinishInstaller(finshInstalation);
    		return ResponseEntity.ok(user);
    	} else if(dataUser!=null &&  dataUser.length() > 1 && (loginError == null || loginError.length()== 0)) {
	    	
	    	System.out.println("user data");
	    	System.out.println(dataUser);
		    String[] arrayUser = dataUser.split(",");


			//validate token is of this user
			if(tokenTimeAtrr.equals(arrayUser[5])) {
				//put in identity for response
				UserEntity user = new UserEntity();			
				user.setId(arrayUser[0]);
				user.setName(arrayUser[2]);
				user.setLastName(arrayUser[3]);
				
				// add to session the user data
				HttpSession session = request.getSession();
				session.setAttribute("id", arrayUser[0]);		
				session.setAttribute("userName", arrayUser[2]);
				session.setAttribute("lastName", arrayUser[3]);	
				
				//clean userData
				System.setProperty("userData", "");
				
				return ResponseEntity.ok(user);
			}  else {
				// in this case, we still don't have an answer
		    	return ResponseEntity.ok(null);
			}
	    } else if(loginError != null && loginError.length() > 1){
	    	
	    	String[] arrayError = loginError.split(",");
	    	
	    	if(arrayError[1].equals(tokenTimeAtrr)) {
		    	//clean error data
		    	System.setProperty("loginError","");
		    	//put error of login
		    	UserEntity user = new UserEntity();		
		    	user.setError(arrayError[0]);
		    	return ResponseEntity.status(400).body(user);
	    	} else {
	    		// in this case, we still don't have an answer
		    	return ResponseEntity.ok(null);
	    	}

	    }
	    else {
	    	// in this case, we still don't have an answer
	    	return ResponseEntity.ok(null);
	    }


	}
	
	@RequestMapping(value="/viewWelcome", method = RequestMethod.GET)
    public ModelAndView viewWelcome(ModelMap model, HttpServletRequest request) { 		
		
		ModelAndView mv = new ModelAndView();
		HttpSession session = request.getSession();
		model.put("id", session.getAttribute("id"));
		model.put("userName", session.getAttribute("userName"));
		model.put("lastName", session.getAttribute("lastName"));
		
		mv.setViewName("welcome");
		return mv;
    }
}