
package com.example.sign.controllers;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.keys.AesKey;
import org.jose4j.lang.JoseException;
import org.json.JSONObject;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.*;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

@RestController
@RequestMapping("/services")
public class Controller {
	
	@GetMapping("/authenticate")
	public String getAuthenticate(@RequestParam(required = false, defaultValue = "", value = "tokenTime") String tokenTimeAtrr) {

		String SECRET = "0kQZrhqOMP7Xjtmi@rx_506_soin@8qDN4pebwmXFcMAZ"; // llave para encrytar el acces token
		Long datetimeEXP = new Date().getTime() + 40000; // valid for 4 minutes
		Long datetime = new Date().getTime(); // time request initial

		JSONObject tokenData = new JSONObject();
		tokenData.put("jti", "token");
		tokenData.put("iat", datetime.toString().substring(0, 10));
		tokenData.put("iss", "firmDigital");
		tokenData.put("exp", datetimeEXP.toString().substring(0, 10));

		Key key = new AesKey(SECRET.getBytes());
		JsonWebEncryption jwe = new JsonWebEncryption();

		jwe.setPayload(tokenData.toString());
		jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.PBES2_HS512_A256KW);
		jwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_256_CBC_HMAC_SHA_512);
		jwe.setKey(key);
		String serializedJwe = "";
		try {
			serializedJwe = jwe.getCompactSerialization();
		} catch (JoseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		JSONObject JWTDataFinal = new JSONObject();
		JWTDataFinal.put("jti", "token");
		JWTDataFinal.put("iss", "rxFirmaInterno");
		JWTDataFinal.put("token", datetime);
		JWTDataFinal.put("accessToken", serializedJwe.toString());
		JWTDataFinal.put("url", "http://localhost:8080/responseExternalLogin");
		JWTDataFinal.put("title", "Authenticate");
		JWTDataFinal.put("idProcess", 12312);

		long nowMillis = System.currentTimeMillis();
		long expMillis = nowMillis + 70000;
		String token = "";
		Date exp = new Date(expMillis);
		System.out.println("......");
		System.out.println(JWTDataFinal.toString());
		Algorithm algorithm;
		try {
			algorithm = Algorithm.HMAC512(SECRET);
			System.out.println(SECRET);
			token = JWT.create().withIssuer("rxFirmaInterno").withSubject(JWTDataFinal.toString()).withExpiresAt(exp)
					.withClaim("accessToken", serializedJwe.toString()).withClaim("title", "Authenticate")
					.withClaim("url", "http://localhost:8080/services/responseExternalLogin")
					.withClaim("idProcess", 12323).withClaim("token",tokenTimeAtrr).sign(algorithm);

		} catch (IllegalArgumentException ex) {
			ex.getMessage();
		} catch (UnsupportedEncodingException ex) {
			ex.getMessage();
		}

		return token;

	}

	@GetMapping("/responseExternalLogin")
	public ResponseEntity<String> getResponseExternalLogin(
			@RequestParam(required = false, defaultValue = "", value = "MSG") String msgAtrr, 
			@RequestParam(required = false, defaultValue = "", value = "token") String tokenAtrr,
			@RequestParam(required = false, defaultValue = "", value = "idProcess") String idProcessAtrr) {
		System.out.println("Possible error response to auth");
		//Error message
		System.out.println(msgAtrr);
		//Token to identify process
		System.out.println(tokenAtrr);

		System.setProperty("loginError", msgAtrr+","+tokenAtrr);
		return ResponseEntity.ok("OK");

	}

	@PostMapping("/responseExternalLogin")
	public ResponseEntity<String> postResponseExternalLogin(
			@RequestParam(required = false, defaultValue = "", value = "data") String dataAtrr,
			@RequestParam(required = false, defaultValue = "", value = "token") String tokenAtrr,
			@RequestParam(required = false, defaultValue = "", value = "idProcess") String idProcessAtrr) {
		String SECRET_PRIVATE = "AjfM0QA2hKYAXQ@rx_506_soin@hA4KsN0P2OA7uwHsih";
		Map<String, Claim> DecodeData;

		try {
			System.out.println(dataAtrr);// Is a JWT
			System.out.println(tokenAtrr);
			System.out.println(idProcessAtrr);
			DecodeData = decodeJWT(dataAtrr, SECRET_PRIVATE);

			String dataUser = DecodeData.get("sub").asString();
			String[] array = dataUser.split(",");
			System.out.println("User Data:");

			// Id have CPF is Fisica
			Boolean typeIdentification = array[6].contains("CPF");
			String codeTypeIdentification = "";
			String id = "";
			if (typeIdentification) {
				id = array[6].substring(19, array[6].length());
				id = id.replace("/-/g", "");
				codeTypeIdentification = "CPF";
			} else {
				id = array[6].substring(18, array[6].length());
				codeTypeIdentification = "NUP";
			}
			
			System.out.println("Id " + id);
			System.out.println("Identification code " + codeTypeIdentification);
			// name user
			System.out.println("Name " + array[4].replace("GIVENNAME=", ""));
			// last name
			System.out.println("Last name " + array[5].replace("SURNAME=", ""));
			
			String userData =id+","+codeTypeIdentification+","+array[4].replace("GIVENNAME=", "")+","+array[5].replace("SURNAME=","")+","+idProcessAtrr+","+tokenAtrr;;
			 
			System.setProperty("userData",userData);
		} catch (Exception e) {
			System.out.println("Errorr JWT");
			e.printStackTrace();
		}

		return ResponseEntity.ok("OK");

	}

	@GetMapping("/sign")
	public ResponseEntity<String> getSignDoc(@RequestParam(required = false, defaultValue = "", value = "tokenTime") String tokenTimeAtrr) {
		String SECRET = "0kQZrhqOMP7Xjtmi@rx_506_soin@8qDN4pebwmXFcMAZ"; // key from Access token
		Long datetimeEXP = new Date().getTime() + 40000;// valid for 4 minutes
		Long datetime = new Date().getTime(); // time request initial

		byte[] fileData = readFile();
		// compress file
		String fileDataCompress = "";
		try {
			fileDataCompress = zipCompress(fileData);
		} catch (IOException e) {
			e.printStackTrace();
		}

		JSONObject tokenData = new JSONObject();
		tokenData.put("jti", "token");
		tokenData.put("iat", datetime.toString().substring(0, 10));
		tokenData.put("iss", "firmDigital");
		tokenData.put("exp", datetimeEXP.toString().substring(0, 10));

		Key key = new AesKey(SECRET.getBytes());
		JsonWebEncryption jwe = new JsonWebEncryption();

		jwe.setPayload(tokenData.toString());
		jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.PBES2_HS512_A256KW);
		jwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_256_CBC_HMAC_SHA_512);
		jwe.setKey(key);
		String serializedJwe = "";
		try {
			serializedJwe = jwe.getCompactSerialization();
		} catch (JoseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		long nowMillis = System.currentTimeMillis();
		long expMillis = nowMillis + 70000;
		String token = "";
		Date exp = new Date(expMillis);
		System.out.println(fileDataCompress);
		Algorithm algorithm;
		try {
			algorithm = Algorithm.HMAC512(SECRET);

			token = JWT.create().withIssuer("externalApplication").withExpiresAt(exp)
					.withClaim("accessToken", serializedJwe.toString()).withClaim("title", "Firmado de PDF")
					.withClaim("type", "PDF").withClaim("pdf", fileDataCompress)
					.withClaim("url", "http://localhost:8080/services/responseExternalSign")
					.withClaim("idProcess", 37777).withClaim("token", tokenTimeAtrr).sign(algorithm);

		} catch (IllegalArgumentException ex) {
			ex.getMessage();
		} catch (UnsupportedEncodingException ex) {
			ex.getMessage();
		}

		return ResponseEntity.ok(token);
	}

	@GetMapping("/responseExternalSign")
	public ResponseEntity<String> getResponseExternalSign(
			@RequestParam(required = false, defaultValue = "", value = "MSG") String msgAtrr,
			@RequestParam(required = false, defaultValue = "", value = "token") String tokenAtrr) {
		System.out.println("Possible error response to sign file");
		//Error message
		System.out.println(msgAtrr);
		//Token to identify process
		System.out.println(tokenAtrr);

		System.setProperty("signError", msgAtrr+","+tokenAtrr);
		return ResponseEntity.ok("OK");

	}

	@PostMapping("/responseExternalSign")
	public ResponseEntity<String> postResponseExternalSign(
			@RequestParam(required = false, defaultValue = "", value = "signedMSG") String signedMSGAtrr,
			@RequestParam(required = false, defaultValue = "", value = "token") String tokenAtrr,
			@RequestParam(required = false, defaultValue = "", value = "idProcess") String idProcessAtrr) {
		System.out.println("recieve data");
		String SECRET_PRIVATE = "AjfM0QA2hKYAXQ@rx_506_soin@hA4KsN0P2OA7uwHsih";
		Map<String, Claim> DecodeData;

		System.out.println(signedMSGAtrr);// Is a JWT
		System.out.println(tokenAtrr);
		System.out.println(idProcessAtrr);

		String fileData = "";
		try {
			DecodeData = decodeJWT(signedMSGAtrr, SECRET_PRIVATE);
			fileData = DecodeData.get("sub").asString();

			String[] array = fileData.split(",");
			System.out.println("File save PDF");
			byte[] filePdf = zipDecompressToByteArray(array[0]);
			FileUtils.writeByteArrayToFile(new File("../sign/Signfile"+tokenAtrr+".pdf"), filePdf);
			// to identify process finish
			System.setProperty("signProcess",tokenAtrr);
			System.setProperty("signFileName","Signfile"+tokenAtrr+".pdf");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ResponseEntity.ok("OK");
	}		

	/// utils functions ////////////
	public static Map<String, Claim> decodeJWT(String token, String key) throws Exception {
		try {
			Algorithm algorithm;

			algorithm = Algorithm.HMAC512(key);

			JWTVerifier verifier = JWT.require(algorithm).build(); // Reusable verifier instance
			DecodedJWT jwt = verifier.verify(token);
			Map<String, Claim> claims = jwt.getClaims();

			return claims;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("ERROR: No se pudo desencriptar el JWT " + e.getMessage());
		}
	}

	public static String byteArrayToBase64(byte[] bs) {
		Base64.Encoder LvarEncoder = Base64.getEncoder();
		return LvarEncoder.encodeToString(bs);
	}

	public static String zipCompress(byte[] data) throws IOException {
		ByteArrayInputStream bis = null;
		ByteArrayOutputStream bos = null;
		GZIPOutputStream gos = null;
		try {
			bis = new ByteArrayInputStream(data);
			bos = new ByteArrayOutputStream();
			gos = new GZIPOutputStream(bos);

			byte[] buffer = new byte[8192];
			int bytesRead;
			while ((bytesRead = bis.read(buffer, 0, buffer.length)) != -1) {
				gos.write(buffer, 0, bytesRead);
			}

			gos.flush();
			gos.close();
			gos = null;

			byte[] compressed = bos.toByteArray();

			bos.close();
			bos = null;
			bis.close();
			bis = null;

			return byteArrayToBase64(compressed);
		} finally {
			if (gos != null)
				gos.close();
			if (bos != null)
				bos.close();
			if (bis != null)
				bis.close();
		}
	}

	public byte[] readFile() {
		try {
			File myObj = new File("../sign/src/main/resources/985.pdf");
			@SuppressWarnings("resource")
			InputStream targetStream = new FileInputStream(myObj);

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();

			int nRead;
			byte[] data = new byte[16384];

			while ((nRead = targetStream.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}
			return buffer.toByteArray();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] zipDecompressToByteArray(String base64) throws IOException {
		ByteArrayInputStream bis = null;
		GZIPInputStream gis = null;
		ByteArrayOutputStream bos = null;
		try {
			byte[] compressed = base64ToByteArray(base64);

			bis = new ByteArrayInputStream(compressed);
			gis = new GZIPInputStream(bis);
			bos = new ByteArrayOutputStream();

			byte[] buffer = new byte[8192];
			int bytesRead;
			while ((bytesRead = gis.read(buffer, 0, buffer.length)) != -1) {
				bos.write(buffer, 0, bytesRead);
			}

			gis.close();
			gis = null;
			byte[] uncompressed = bos.toByteArray();

			bos.close();
			bos = null;
			bis.close();
			bis = null;

			return uncompressed;
		} finally {
			if (bos != null)
				bos.close();
			if (gis != null)
				gis.close();
			if (bis != null)
				bis.close();
		}
	}

	public static byte[] base64ToByteArray(String s) {
		Base64.Decoder LvarDecoder = Base64.getDecoder();
		return LvarDecoder.decode(s);
	}	    

}