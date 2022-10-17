
package com.example.sign.controllers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/signature/") // IMPORTANT: this route it cannot be changed as well as the calls to each function of the controller
public class InstallerController {
	
	// upload installer jar
	@GetMapping("makeInstaller")
	public ResponseEntity<Resource> getMakeInstaller(
			@RequestParam(required = false, defaultValue = "", value = "process") String processAtrr,
			@RequestParam(required = false, defaultValue = "", value = "token") String tokenAtrr) {
		 try {
			 
			// step 1 write parameters
			FileWriter myWriter = new FileWriter("../sign/src/main/resources/DigitalInstallerContent/parametros.txt");
			String checkSum = "952DC89D7ECAD80A2AE6D1C7EC6894F3";// this is to verify the version of digital signature  and security purposes
			  // this is url to download files if you need change for example https://www.application/api/v1/signature/sign.cfc
			String parameters = "url=http://localhost:8080/api/v1/signature/sign.cfc"
			+ "\ntoken="+tokenAtrr 
			+ "\nproceso="+processAtrr // this is OP value example authenticate or authenticateDummy
			+ "\nchecksum="+checkSum 
			+ "\nresponseToUrl=true";			  
			  
			myWriter.write(parameters);
			myWriter.close();
			
			// step 2 make a temporal installer jar
			omtZip("../sign/src/main/resources/DigitalInstallerContent/","./FirmaInstaller"+tokenAtrr+".jar");
			
			// step 3 return temporal installer  file  
			File file = new File("../sign/FirmaInstaller"+tokenAtrr+".jar");
			
			InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
			HttpHeaders header = new HttpHeaders();
			header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=FirmaDigitalInstaller.jar");
			header.add("Cache-Control", "no-cache, no-store, must-revalidate");
			
			header.add("Pragma", "no-cache");
			header.add("Expires", "0");
	        
			System.out.println("Successfully create jar file.");
		
			// finally delete file temporal
			new Thread(() -> {
		        try {
		            Thread.sleep(100000);
		            file.delete();
		        }
		        catch (Exception e){
		            System.err.println(e);
		        }
		    }).start();
			
		      return ResponseEntity.ok()
		              .headers(header)
		              .contentLength(file.length())
		              .contentType(MediaType.parseMediaType("application/octet-stream"))
		              .body(resource);
		      
		      
		      
		    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }    
		 return null;
	}

	// upload firmadigitalserver.jar
	@GetMapping("lib/FirmaDigitalServer.jar")
	public ResponseEntity<Resource> getFirmaDigitalServer() {
		 try {
			 		
			File file = new File("../sign/src/main/resources/FirmaDigitalServer.jar");
			
			InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
			HttpHeaders header = new HttpHeaders();
			header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=FirmaDigitalServer.jar");
			header.add("Cache-Control", "no-cache, no-store, must-revalidate");
			
			header.add("Pragma", "no-cache");
			header.add("Expires", "0");
	        
			System.out.println("Successfully download jar file.");
			
		    return ResponseEntity.ok()
		              .headers(header)
		              .contentLength(file.length())
		              .contentType(MediaType.parseMediaType("application/octet-stream"))
		              .body(resource);
		      
		      
		      
		    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }    
		 return null;
	}
	
	// upload FirmaDigitalServer.lib
	@GetMapping("lib/FirmaDigitalServer.lib")
	public ResponseEntity<Resource> getFirmaDigitalServerlib() {
		 try {
			 		
			File file = new File("../sign/src/main/resources/FirmaDigitalServer.lib");
			
			InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
			HttpHeaders header = new HttpHeaders();
			header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=FirmaDigitalServer.lib");
			header.add("Cache-Control", "no-cache, no-store, must-revalidate");
			
			header.add("Pragma", "no-cache");
			header.add("Expires", "0");
	        
			System.out.println("Successfully download lib file.");
			
			return ResponseEntity.ok()
		              .headers(header)
		              .contentLength(file.length())
		              .contentType(MediaType.parseMediaType("application/octet-stream"))
		              .body(resource);
		      
		      
		      
		    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }    
		 return null;
	}
	
	// upload libASEP11.dylib
	@GetMapping("lib/libASEP11.dylib")
	public ResponseEntity<Resource> getlibASEP11() {
		 try {
			 		
			File file = new File("../sign/src/main/resources/libASEP11.dylib");
			
			InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
			HttpHeaders header = new HttpHeaders();
			header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=libASEP11.dylib");
			header.add("Cache-Control", "no-cache, no-store, must-revalidate");
			
			header.add("Pragma", "no-cache");
			header.add("Expires", "0");
	        
			System.out.println("Successfully download libASEP11.dylib file.");
			
			return ResponseEntity.ok()
		              .headers(header)
		              .contentLength(file.length())
		              .contentType(MediaType.parseMediaType("application/octet-stream"))
		              .body(resource);
		      
		      
		      
		    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }    
		 return null;
	}
	
	// to notify the front end that I installed everything well
	@GetMapping("sign.cfc")
	public ResponseEntity<String> getPort(
			@RequestParam(required = false, defaultValue = "", value = "port") String portAtrr,
			@RequestParam(required = false, defaultValue = "", value = "token") String tokenAtrr) {			 		
		
		System.out.println("Instalacion finish "+tokenAtrr+" en el puert"+portAtrr);
		System.setProperty("finshInstalation", tokenAtrr);	
		return ResponseEntity.ok("OK");
	}
	
	
	
	
	// utils for this controller
	public static void omtZip(String path,String outputFile)
	{
	    final int BUFFER = 2048;
	    boolean isEntry = false;
	    ArrayList<String> directoryList = new ArrayList<String>();
	    File f = new File(path);
	    if(f.exists())
	    {
	    try {
	            FileOutputStream fos = new FileOutputStream(outputFile);
	            ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(fos));
	            byte data[] = new byte[BUFFER];

	            if(f.isDirectory())
	            {
	               //This is Directory
	                do{
	                    String directoryName = "";
	                    if(directoryList.size() > 0)
	                    {
	                        directoryName = directoryList.get(0);
	                        System.out.println("Directory Name At 0 :"+directoryName);
	                    }
	                    String fullPath = path+directoryName;
	                    File fileList = null;
	                    if(directoryList.size() == 0)
	                    {
	                        //Main path (Root Directory)
	                        fileList = f;
	                    }else
	                    {
	                        //Child Directory
	                        fileList = new File(fullPath);
	                    }
	                    String[] filesName = fileList.list();

	                    int totalFiles = filesName.length;
	                    for(int i = 0 ; i < totalFiles ; i++)
	                    {
	                        String name = filesName[i];
	                        File filesOrDir = new File(fullPath+name);
	                        if(filesOrDir.isDirectory())
	                        {
	                            System.out.println("New Directory Entry :"+directoryName+name+"/");
	                            ZipEntry entry = new ZipEntry(directoryName+name+"/");
	                            zos.putNextEntry(entry);
	                            isEntry = true;
	                            directoryList.add(directoryName+name+"/");
	                        }else
	                        {
	                            System.out.println("New File Entry :"+directoryName+name);
	                            ZipEntry entry = new ZipEntry(directoryName+name);
	                            zos.putNextEntry(entry);
	                            isEntry = true;
	                            FileInputStream fileInputStream = new FileInputStream(filesOrDir);
	                            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream, BUFFER);
	                            int size = -1;
	                            while(  (size = bufferedInputStream.read(data, 0, BUFFER)) != -1  )
	                            {
	                                zos.write(data, 0, size);
	                            }
	                            bufferedInputStream.close();
	                        }
	                    }
	                    if(directoryList.size() > 0 && directoryName.trim().length() > 0)
	                    {
	                        System.out.println("Directory removed :"+directoryName);
	                        directoryList.remove(0);
	                    }

	                }while(directoryList.size() > 0);
	            }else
	            {
	                //This is File
	                //Zip this file
	                System.out.println("Zip this file :"+f.getPath());
	                FileInputStream fis = new FileInputStream(f);
	                BufferedInputStream bis = new BufferedInputStream(fis,BUFFER);
	                ZipEntry entry = new ZipEntry(f.getName());
	                zos.putNextEntry(entry);
	                isEntry = true;
	                int size = -1 ;
	                while(( size = bis.read(data,0,BUFFER)) != -1)
	                {
	                    zos.write(data, 0, size);
	                }
	            }               

	            //CHECK IS THERE ANY ENTRY IN ZIP ? ----START
	            if(isEntry)
	            {
	              zos.close();
	            }else
	            {
	                zos = null;
	                System.out.println("No Entry Found in Zip");
	            }
	            //CHECK IS THERE ANY ENTRY IN ZIP ? ----START
	        }catch(Exception e)
	        {
	            e.printStackTrace();
	        }
	    }else
	    {
	        System.out.println("File or Directory not found");
	    }
	 }
}