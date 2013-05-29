package jk.querynex.notify

import javax.mail.*
import javax.mail.internet.*
import javax.activation.*

/*
 * example:
	new MailSender(
		'to':['myemailaccount@sample.com'],
		'from':'nexbot@sample.com',
		'subject':'Nex update')
*/

class MyAuth extends Authenticator {}

class MailSender implements INotifier {
	String user = null
	String password = null
	String host = 'smtp.gmail.com'
	String from = 'nexbot@sample.com'
	List to = []
	List cc = []
	String content = 'test content'
	String encoding = 'UTF-8'
	String type = 'text/plain'
	String subject = 'test subject line'
	def auth = ['getPasswordAuthentication': { new PasswordAuthentication(user, password) }] as MyAuth
	def msgSend = { Transport.send(it) }
	
	InternetAddress[] mkAddress(List list)  {
		list.collect { new InternetAddress(it) }.toArray()
	}
	
	def mailProps = [
		'mail.host':host,
		'mail.smtp.auth':'true',
		'mail.smtp.starttls.enable':'true',
		'mail.smtp.host':'smtp.gmail.com',
		'mail.smtp.port':'587'] as Properties
		
	def init = {
		def session = Session.getInstance(mailProps, user ? auth : null)
		def msg = new MimeMessage(session)
		msg.from = new InternetAddress(from)
		msg.setRecipients(Message.RecipientType.TO, mkAddress(to))
		msg.setRecipients(Message.RecipientType.CC, mkAddress(cc))
		msg.subject = subject
		msg
	}
	
	public void send() {
		def msg = init()
		msg.setContent(content, type)
		msgSend(msg)
	}
	
	public void send(String msg) {
		content = msg
		type = 'text/plain'
		send()
	}
	
	public MimeMessage buildMultipart(Map files) {
		def msg = init()
		def multipart = new MimeMultipart()

		//add text body
		def msgBody = new MimeBodyPart()
		msgBody.setText(content, encoding, type)
		multipart.addBodyPart(msgBody)

		//add attachments
		files.each { name, file ->
			msgBody = new MimeBodyPart()
			DataSource src = new FileDataSource(file)
			msgBody.setDataHandler(new DataHandler(src))
			msgBody.setFileName(name)
			multipart.addBodyPart(msgBody)
		}
		msg.setContent(multipart)
		msg
	}

	public void sendMultipart(Map files) {
		msgSend(buildMultipart(files))
	}
}
