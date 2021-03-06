package com.feeyo.util;

import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MailUtil {
	
	private static Properties props = null;
	private static String userName = null;
	private static String password = null;
	
	private static InternetAddress fromAddr;
	private static InternetAddress[] toAddrs;
	
	private static Object _lock = new Object();
	
	public static boolean send(Properties mailProps, String subject, String body, String[] fileNames) {
		
		if(props != mailProps) {
			synchronized ( _lock ) {
				if(props !=  mailProps) {
					try {
						props = mailProps;
						
						userName = props.getProperty("mail.from.userName");
						password = props.getProperty("mail.from.password");
						fromAddr = new InternetAddress(userName);
						
						String toAddrsStr = props.getProperty("mail.to.addrs");
						String[] addrs = toAddrsStr.split(",");
						
						toAddrs = new InternetAddress[ addrs.length ];
						for(int i =0; i < addrs.length; i++) {
							toAddrs[i]= new InternetAddress( addrs[i] );
						}
						
					} catch (Exception e) {
					}
				}
			}
		}

	 	Session session = Session.getDefaultInstance(props, new Authenticator() {
            //身份认证
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        });
	 	
        try {
        
			MimeMessage message = new MimeMessage(session);
	        message.setFrom( fromAddr );
	        message.setRecipients(MimeMessage.RecipientType.TO, toAddrs);
	        message.setSubject(subject, "UTF-8");
	       
            // 附件操作  
            if (fileNames != null && fileNames.length > 0) {  
            	
            	Multipart mp = new MimeMultipart();  
                for (String fileName: fileNames) {  
                    MimeBodyPart mbp = new MimeBodyPart();  
                    FileDataSource fds = new FileDataSource(fileName); 	  // 得到数据源  
                    mbp.setDataHandler(new DataHandler(fds));  	 		  // 得到附件本身并至入BodyPart  
                    mbp.setFileName(fds.getName());  					  // 得到文件名同样至入BodyPart  
                    mp.addBodyPart(mbp);  
                }  
                
                MimeBodyPart mbp = new MimeBodyPart();  
                mbp.setText(body);  
                mp.addBodyPart(mbp);  
                message.setContent(mp);  		// Multipart加入到信件  
                
            } else {
                message.setText(body);  		// 设置邮件正文  
            }
            message.setSentDate(new Date());
	        message.saveChanges();
	        
            // 发送邮件
            Transport.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
	 
}