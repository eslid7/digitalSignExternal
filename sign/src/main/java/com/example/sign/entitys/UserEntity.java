package com.example.sign.entitys;

public class UserEntity {
	String id;
    String name;
    String lastName;
    String error;
    String finishInstaller;
    
    
    public void setId(String idValue) {
    	id = idValue;
    }
    
    public void setName(String nameValue) {
    	name = nameValue;
    }
    
    public void setLastName(String lastNameValue) {
    	lastName = lastNameValue;
    }
    
    public void setError(String errorValue) {
    	error = errorValue;
    }
    
    public void setFinishInstaller(String finishInstallerValue) {
    	finishInstaller = finishInstallerValue;
    }
        
    
    public String getId() {
    	return id;
    }
    
    public String getName() {
    	return name ;
    }
    
    public String getLastName() {
    	return lastName;
    }
    
    public String getError() {
    	return error;
    } 
    
    public String getFinishInstaller() {
    	return finishInstaller;
    }
}