import java.io.UnsupportedEncodingException;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

/**
 * SendGmail is a class for sending emails via Gmail. In order to use this
 * class you must have a gmail account. Methods can be chained together
 * to send an email. To send the email use the method:
 * <ul>
 * <li>sendEmail
 * </ul>
 * The following methods must be used for the email before sending the email:
 * <ul>
 * <li>setFrom
 * <li>setReceipients
 * </ul>
 * All other methods are optional.
 * @author Jeff Enriquez
 * @version 1.0
*/
public class SendGmail {
	
	// Properties needed for sending gmail
	private static Properties properties = new Properties();
	static {
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", "587");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		properties.put("mail.smtp.starttls.required", "true");
		properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
	}
	
	// A container for message body parts
	private Multipart multipartObject = new MimeMultipart();
	
	// The sender's email address
	private InternetAddress fromEmail;
	
	// The receipient's email addresses
	private InternetAddress[] receipients;
	
	// The subject of the email
	private String subject;
	
	/**
	 * Set the sender's email address
	 * 
	 * @param email                the email of the sender
	 * @return SendGmail           current instance of SendGmail
	 * @throws AddressException
	 */
	public SendGmail setFrom(String email) throws AddressException {
		fromEmail = new InternetAddress(email);
		return this;
	}
	
	/**
	 * Set the sender's email address
	 * 
	 * @param email                         the email of the sender
	 * @param name                          the name of the sender
	 * @return SendGmail                    current instance of SendGmail
	 * @throws UnsupportedEncodingException
	 */
	public SendGmail setFrom(String email, String name) throws UnsupportedEncodingException {
		fromEmail = new InternetAddress(email, name);
		return this;
	}
	
	/**
	 * Set the receipients email addresses
	 * All receipients will be added to CC
	 * 
	 * @param emails               the emails of the receipients
	 * @return SendGmail           current instance of SendGmail
	 * @throws MessagingException
	 */
	public SendGmail setReceipients(String... emails) throws MessagingException {
		receipients = new InternetAddress[emails.length];
		for (int i = 0; i < emails.length; i++) {
			receipients[i] = new InternetAddress(emails[i].trim().toLowerCase());
		}
		return this;
	}
	
	/**
	 * Set the subject of the email
	 * 
	 * @param subject              the subject of the email
	 * @return SendGmail           current instance of SendGmail
	 * @throws MessagingException
	 */
	public SendGmail setSubject(String subject) throws MessagingException {
		this.subject = subject;
		return this;
	}
	
	/**
	 * Add text to the email message body
	 * 
	 * @param text                 text to be added to the message body
	 * @return SendGmail           current instance of SendGmail
	 * @throws MessagingException
	 */
	public SendGmail addTextToBody(String text) throws MessagingException {
		BodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setText(text);
		multipartObject.addBodyPart(messageBodyPart);
		return this;
	}
	/**
	 * Add html to the email message body
	 * Html allows you to customize how your email message body looks 
	 * 
	 * @param html                 html to be added to the message body
	 * @return SendGmail           current instance of SendGmail
	 * @throws MessagingException
	 */
	public SendGmail addHtmlToBody(String text) throws MessagingException {
		BodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(text,"text/html");
		multipartObject.addBodyPart(messageBodyPart);
		return this;
	}
	
	/**
	 * Add an image to the email message body
	 * 
	 * @param filename             file location of the image
	 * @return SendGmail           current instance of SendGmail
	 * @throws MessagingException
	 */
	public SendGmail addImageToBody(String filename) throws MessagingException {
		// Create an image html tag with an id of a random number
		double randomNum = Math.random();
		String htmlText = "<img src=\"cid:"
				+ randomNum + "\">";
		// Add the image html tag to the message body
		addHtmlToBody(htmlText);
		// Add the image and use the random number to identify where it belongs in the message body
		BodyPart messageBodyPart = new MimeBodyPart();
		DataSource fds = new FileDataSource(filename);
		messageBodyPart.setDataHandler(new DataHandler(fds));
		messageBodyPart.setHeader("Content-ID", "<" + randomNum + ">");
		multipartObject.addBodyPart(messageBodyPart);
		return this;
	}
	/**
	 * Add an attachment to the email
	 * 
	 * @param filename             file location of the attachment
	 * @return SendGmail           current instance of SendGmail
	 * @throws MessagingException
	 */
	public SendGmail addAttachment(String filename) throws MessagingException {
		BodyPart messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(filename); 
        messageBodyPart.setDataHandler(new DataHandler(source)); 
        messageBodyPart.setFileName(filename);
        multipartObject.addBodyPart(messageBodyPart);
		return this;
	}
	
	/**
	 * Send the email
	 * 
	 * @param user                 gmail username
	 * @param password             gmail password
	 * @throws MessagingException
	 */
	public void sendEmail(final String user, final String password) throws MessagingException {
		Session session = Session.getInstance(properties,
	            new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });;
        Message message = new MimeMessage(session);
        message.setFrom(fromEmail);
        message.setRecipients(Message.RecipientType.TO,receipients);
        message.setSubject(subject);
		message.setContent(multipartObject);
		Transport.send(message);
	}
	
}
