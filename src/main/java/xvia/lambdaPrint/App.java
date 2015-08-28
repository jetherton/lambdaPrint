package xvia.lambdaPrint;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import trackvia.client.TrackviaClient;
import trackvia.client.model.Record;
import trackvia.client.model.RecordData;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpMediaType;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.MultipartContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Maps;
import com.google.gson.Gson;
import com.service.aws.TrackViaLambdaInterface;
import com.xvia.request.lambda.TrackViaLambdaRequest;
import com.xvia.response.lambda.TrackViaLambdaResponse;

/**
 * Hello world!
 *
 */
public class App implements TrackViaLambdaInterface
{
	
	
	
	public void print(String itemName, Long itemId){
		
		
		try {
			
			JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
			HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			
			
			File credFile = new File(this.getClass().getResource("/resources/google_cred.p12").toURI());
			
			
			GoogleCredential credential = new GoogleCredential.Builder()
			    .setTransport(httpTransport)
			    .setJsonFactory(JSON_FACTORY)
			    .setServiceAccountId(PrivateStuff.SERVICE_EMAIL_ADDRESS)
			    .setServiceAccountPrivateKeyFromP12File(credFile)
			    .setServiceAccountScopes(Collections.singleton("https://www.googleapis.com/auth/cloudprint"))
			    .build();
			credential.refreshToken();
			System.out.println("made it here: " + credential.getAccessToken());
			
			
			//make sure we can see some printers
			 String PostUrl = "https://www.google.com/cloudprint/search";
	         HttpRequestFactory requestFactory = httpTransport.createRequestFactory(credential);

			
			
			 GenericUrl searchUrl = new GenericUrl(PostUrl);
			
			 String requestBody = "";//use_cdd=false&extra_fields=-tags&type=&connection_status=";
	         HttpRequest request = requestFactory.buildPostRequest(searchUrl, ByteArrayContent.fromString(null, requestBody));
	         request.getHeaders().setContentType("application/x-www-form-urlencoded");
	         HttpResponse response = request.execute();
	         System.out.println(response.getStatusMessage());
	         
			 InputStream stream = response.getContent();			 
			 StringWriter writer = new StringWriter();
			 IOUtils.copy(stream, writer, "UTF-8");
			 String theString = writer.toString();
			 stream.close();
			 System.out.println(theString);
			 
			 
			 
			 
			 //print a picture
			 System.out.println("Trying to print");
			 String submitUrlString = "https://www.google.com/cloudprint/submit";
			 GenericUrl submitUrl = new GenericUrl(submitUrlString);
			 
			 

			 Map<String, String> parameters = Maps.newHashMap();
			 parameters.put("printerid", PrivateStuff.LABEL_PRINTER_ID);
			 parameters.put("title", "Title");
			 parameters.put("contentType", "text/html");

			 // Map print options into CJT structure
			 Map<String, Object> options = Maps.newHashMap();
			 options.put("version", "1.0");
			 options.put("print", null);
			 parameters.put("ticket", new Gson().toJson(options));

			 // Add parameters
			 MultipartContent content = new MultipartContent().setMediaType(
			         new HttpMediaType("multipart/form-data")
			                 .setParameter("boundary", "__END_OF_PART__"));
			 for (String name : parameters.keySet()) {
			     MultipartContent.Part part = new MultipartContent.Part(
			             new ByteArrayContent(null, parameters.get(name).getBytes()));
			     part.setHeaders(new HttpHeaders().set(
			             "Content-Disposition", String.format("form-data; name=\"%s\"", name)));
			     content.addPart(part);
			 }
			 
			 //create HTML
			 ByteArrayOutputStream htmlFile = createHtmlFile(itemName, itemId);

			 // Add file
			 ByteArrayContent s = new ByteArrayContent("text/html", htmlFile.toByteArray());
			 MultipartContent.Part part = new MultipartContent.Part(s);
			 part.setHeaders(new HttpHeaders().set(
			         "Content-Disposition", 
			         String.format("form-data; name=\"content\"; filename=\"%s\"", "temp-html.jpg")));
			 content.addPart(part);


		     response = requestFactory.buildPostRequest(submitUrl, content).execute();
		     System.out.println(IOUtils.toString(response.getContent()));
			 
			 
			 
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public static ByteArrayOutputStream createHtmlFile(String itemName, Long itemId) throws IOException{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		OutputStreamWriter streamWriter = new OutputStreamWriter(stream);
		BufferedWriter writer = new BufferedWriter(streamWriter);
		//PrintWriter writer = new PrintWriter("temp.html", "UTF-8");
		writer.write("<html style=\"margin:0px;padding:0px;\">");
		//writer.write("<h3>");
		//Date d = new Date();
		//writer.write("Date: " + d.toString());
		//writer.write("</h3>");
		writer.write("<h3 style=\"margin:0px;padding:0px;\" >");
		writer.write("Item Name: " + itemName);
		writer.write("</h3>");
		String idStr = itemId.toString();
		writer.write("<img style=\"margin:0px;padding:0px;\" src=\"http://api.qrserver.com/v1/create-qr-code/?size=150x150&data=" + idStr + "\"/>");
		writer.write("</html>");
		writer.close();
		streamWriter.close();

		return stream;
	}
	
	public TrackViaLambdaResponse handleRequest(TrackViaLambdaRequest request) {
			
		System.setProperty("jsse.enableSNIExtension", "false");
		//setup my client
		
		TrackviaClient openApiClient = TrackviaClient.create(request.getApiHost(), 
				request.getAccessToken(), "/", "https", 443, PrivateStuff.API_KEY);
		
		//grab the record in question
		Record orderRecord = openApiClient.getRecord(request.getViewId(), request.getRecordId());
		
		//now grab the item
		Long itemId = (Long)(orderRecord.getData().get("Link to Items"));
		
		Record itemRecord = openApiClient.getRecord(3, itemId);
		
		RecordData data = itemRecord.getData();

		String itemName = itemRecord.getData().get("Description").toString();

		
			print(itemName, itemId);
		
		
		//now grab the child
		return null;
	}
	
	
		


}
