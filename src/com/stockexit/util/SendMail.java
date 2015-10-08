package com.stockexit.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Level;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail {
	
	
	public synchronized static void generateAndSendEmail(String message)  {
		 
		try{		
		
				Properties mailServerProperties = System.getProperties();
				mailServerProperties.put("mail.smtp.port", "587");
				mailServerProperties.put("mail.smtp.auth", "true");
				mailServerProperties.put("mail.smtp.starttls.enable", "true");
				
		 
		//Step2		
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Calendar today = Calendar.getInstance();
				
				Session getMailSession = Session.getDefaultInstance(mailServerProperties, null);
				MimeMessage generateMailMessage = new MimeMessage(getMailSession);
				generateMailMessage.setFrom(new InternetAddress("escapeplan555@gmail.com"));
				generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress("kushal.kush12@gmail.com"));
				generateMailMessage.addRecipient(Message.RecipientType.CC, new InternetAddress("pravesh.dudani@gmail.com"));
				generateMailMessage.addRecipient(Message.RecipientType.CC, new InternetAddress("agarwa27@gmail.com"));
				generateMailMessage.setSubject("Notification from EscapePlan "+sdf.format(today.getTime()));
				//String emailBody = "Test email by Crunchify.com JavaMail API example. " + "<br><br> Regards, <br>Crunchify Admin";
				generateMailMessage.setContent(message, "text/html");
				
		 
		//Step3		
				LoggerUtil.getLogger().info("Get Session and Send mail");
				Transport transport = getMailSession.getTransport("smtp");
				
				// Enter your correct gmail UserID and Password (XXXApp Shah@gmail.com)
				transport.connect("smtp.gmail.com", "escapeplan555", "escapeplan12");
				transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
				transport.close();
				LoggerUtil.getLogger().info("Mail sent ...");
			}catch(Exception e){
				LoggerUtil.getLogger().log(Level.SEVERE, "sendmail failed", e);
			}
		}
}
