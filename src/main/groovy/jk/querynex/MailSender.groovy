package jk.querynex

import javax.mail.*
import javax.mail.internet.*
import javax.activation.*

class MyAuth extends Authenticator {}

class MailSender {
	String host = 'smtp.gmail.com'
	String from = 'nexbot@sample.com'
	List to = []
	List cc = []
	String content = 'test content'
	String encoding = 'UTF-8'
	String type = 'text/plain'
	String subject = 'test subject line'
	def auth = ['getPasswordAuthentication': { new PasswordAuthentication('user', 'password') }] as MyAuth
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
		def session = Session.getInstance(mailProps, auth)
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
