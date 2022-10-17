<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="commons/header.jspf" %>
<html>
    <head>
        <title>View Login</title>
        <link href="<c:url value="/css/common.css"/>" rel="stylesheet" type="text/css">
    </head>
    <body>
        <table width="100%">
             <tr>
                 <th align="center">Autenticathe user</th>
             </tr>
             <tr>
                 <th align="center"><button id="loginButton" type="button" onclick="fnlogin()" class="button">Login</button> 
                 
                 </th>
             </tr>
        </table>
    </body>
</html>
<%@ include file="commons/footer.jspf" %>
<script type="text/javascript">
 var finishLoginTimeout ='';
 function fnlogin(){
	 finishLoginTimeout = (new Date()).getTime() + 20000;//valid for 2 minutes for timeout and use for identify processs
	 // get Token
	 $.ajax({
         type: "GET",
         url: "/services/authenticate?tokenTime="+finishLoginTimeout,
         beforeSend: function(){ 
        	 //I disable the button and put the user on hold
        	 $("#loginButton").hide()
             showIsLoading("Procesando...");
         }
     }).done(function (data) {
         console.log(data)
         //get Auth to Component local (firmadigitallocal.com)
         $.ajax({
              type: "GET",
              url: "https://firmadigitallocal.com:50007/FirmaDigitalServer?OP=authenticateDummy&data="+data,
              beforeSend: function(){
             	 window.setTimeout("fnGetLocalResponseAuth();",3000);                	 
              },
             timeout: 10000 // sets timeout to 10 seconds
          }).done(function (data) {
              console.log(data)                               
          }).fail(function (data) {    
         	 // in this case return file installer because the user don`t have         
         	urlToSend = "/api/v1/signature/makeInstaller?token="+finishLoginTimeout+"&process=authenticateDummy"			
			var req = new XMLHttpRequest();
			req.open("GET", urlToSend, true);
			req.responseType = "blob";
			req.onload = function (event) {
			    var blob = req.response;
			    var fileName = "FirmaDigitalInstaller.jar"
			    var link=document.createElement('a');
			    link.href=window.URL.createObjectURL(blob);
			    link.download=fileName;
			    link.click();
			};

		    req.send();
		    
        	console.log("Error", data)
      		hideIsLoading();
          })         
               
     }).fail(function (data) {    	 
    	 console.log("Error", data)
         hideIsLoading();
     })
 }

 
 function fnGetLocalResponseAuth(){
	 $.ajax({
         type: "GET",
         url: "/responselogin?tokenTime="+finishLoginTimeout,
     }).done(function (data) {
    	 console.log(data)
    	 if(data.finishInstaller == finishLoginTimeout){
    		 // the instalation is finish recall the auth digital sign to Auth process
    		 fnlogin();
    	 }else if(data.id){
    		//display user data when successful process
    		window.location.href ='/viewWelcome';
    		 hideIsLoading();
    	 } else if((new Date()).getTime()>finishLoginTimeout) {
    		 //time out process
    		 hideIsLoading();
    		 $("#loginButton").show()    		
    	 } else {
    		 //the process is still waiting
    		 window.setTimeout("fnGetLocalResponseAuth();",2000);
    	 }
               
     }).fail(function (data) {             
    	 console.log("Error", data.responseText)
         hideIsLoading();
    	 $("#loginButton").show()    	 
    	 alert(JSON.parse(data.responseText).error);
     })
 }
</script>