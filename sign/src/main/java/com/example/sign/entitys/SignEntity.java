package com.example.sign.entitys;

public class SignEntity {
	String idProcess;
	String nameFile;
    String error;
 
 	 public void setIdProcess(String idProcessValue) {
    	idProcess = idProcessValue;
    }
    
     public void setNameFile(String nameFileValue) {
    	nameFile = nameFileValue;
    }
    
    public void setError(String errorValue) {
    	error = errorValue;
    }
        
    public String getIdProcess() {
    	return idProcess;
    } 
    
    public String getNameFile() {
    	return nameFile;
    } 
    
    public String getError() {
    	return error;
    } 
 
 }