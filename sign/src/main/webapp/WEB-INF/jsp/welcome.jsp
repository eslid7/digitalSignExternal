<%@ include file="commons/header.jspf"%>
<%--  <%@ include file="commons/navigation.jspf"%>--%>
<div class="container" style="font-size: 19px;">
	Welcome ${userName}${lastName} id user ${id}!! 
	<table width="100%">
          <tr>
              <th align="center">Sign file PDF</th>
          </tr>
          <tr>
              <th align="center"><button id="signButton" type="button" onclick="fnSignProcess()" class="button">Sign</button> 
              
              </th>
          </tr>
     </table>
</div>
<%@ include file="commons/footer.jspf"%>
<script type="text/javascript">
 var finishLoginTimeout ='';
 function fnSignProcess(){
	 finishLoginTimeout = (new Date()).getTime() + 20000;//valid for 2 minutes for timeout and use for identify processs
	 // get Token
	 $.ajax({
         type: "GET",
         url: "/services/sign?tokenTime="+finishLoginTimeout,
         beforeSend: function(){ 
        	 //I disable the button and put the user on hold
        	 $("#signButton").hide()
             showIsLoading("Procesando...");
         }
     }).done(function (data) {
         console.log(data)
         //get Sign to Component local (firmadigitallocal.com)
         $.ajax({
              type: "GET",
              url: "https://firmadigitallocal.com:50007/FirmaDigitalServer?OP=signDummy&data="+data,
              beforeSend: function(){
             	 window.setTimeout("fnGetLocalResponseSign();",3000);                	 
              }
          }).done(function (data) {
              console.log(data)                               
          }).fail(function (data) {             
        	console.log("Error", data)
      		hideIsLoading();
          })        
               
     }).fail(function (data) {             
    	 console.log("Error", data)
         hideIsLoading();
     })
 }

 
 function fnGetLocalResponseSign(){
	 $.ajax({
         type: "GET",
         url: "/responseSignPDF?tokenTime="+finishLoginTimeout,
     }).done(function (data) {
    	 console.log(data)
    	 if(data.idProcess){
    		//display user data when successful process
    		 alert("Document is Sign successful")
    		 hideIsLoading();
    	 } else if((new Date()).getTime()>finishLoginTimeout) {
    		 //time out process
    		 hideIsLoading();
    		 $("#signButton").show()    		
    	 } else {
    		 //the process is still waiting
    		 window.setTimeout("fnGetLocalResponseSign();",2000);
    	 }
               
     }).fail(function (data) {             
    	 console.log("Error", data.responseText)
         hideIsLoading();
    	 $("#signButton").show()    	 
    	 alert(JSON.parse(data.responseText).error);
     })
 }
</script>