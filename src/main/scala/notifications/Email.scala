package notifications

import java.util.{Date, Properties}

import com.typesafe.config._
import akka.actor.Actor
import javax.mail._
import javax.mail.internet.{InternetAddress, MimeMessage}
import notifications.Errors.{AuthorizationError, TargetError, InternalError}

class Email extends Actor {
  val conf = ConfigFactory.load()
  val login = conf.getString("email.login")
  val from = login
  val password = conf.getString("email.password")
  val host = conf.getString("email.host")
  val port = conf.getString("email.port")


  def createMessage(to: String, subject: String, content: String): Message = {
    val properties = new Properties()
    properties.put("mail.transport.protocol", "smtp")
    properties.put("mail.smtp.host", host)
    properties.put("mail.smtp.port", port)
    properties.put("mail.smtp.auth", "true")
    properties.put("mail.smtp.starttls.enable", "true")
    properties.put("mail.smtp.connectiontimeout", "10000")
    properties.put("mail.smtp.timeout", "10000")

    val authenticator = new Authenticator() {
      override def getPasswordAuthentication = new
          PasswordAuthentication(login, password)
    }

    val session = Session.getInstance(properties, authenticator)
    val message = new MimeMessage(session)
    message.setFrom(new InternetAddress(from))
    message.setRecipient(Message.RecipientType.TO, new InternetAddress(to))
    message.setSubject(subject)
    message.setText(content)

    return message
  }

  def sendMessage(to: String, subject: String, content: String) = {
    try {
      val message = createMessage(to, subject, content)
      message.setSentDate(new Date())
      Transport.send(message)
    }
    catch {
      case e: AuthenticationFailedException => throw new AuthorizationError(e.getMessage)
      case e: SendFailedException => throw new TargetError(e.getMessage)
      case e: MessagingException => throw new InternalError(e.getMessage)
      case e: Throwable => throw new InternalError(e.toString)
    }
  }

  def receive = {
    case e: EmailNotification => sendMessage(e.email, e.title, e.toString())
  }
}
