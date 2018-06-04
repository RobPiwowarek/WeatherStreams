package notifications

import java.net.SocketTimeoutException
import java.util.{Date, Properties}

import akka.actor.Actor
import com.typesafe.config._
import javax.mail._
import javax.mail.internet.{InternetAddress, MimeMessage}
import notifications.Errors.{AuthorizationError, InternalError, SendError, TimeoutError}

sealed trait EmailConfig {
  val login: String
  val from: String
  val password: String
  val host: String
  val port: String
  val timeout: String
}

object EmailFileConfig extends EmailConfig {
  // copy src/main/resources/reference.conf to src/main/resources/application.conf
  // and set values manually (it will override reference.conf)
  val conf = ConfigFactory.load()
  val login = conf.getString("email.login")
  val from = login
  val password = conf.getString("email.password")
  val host = conf.getString("email.host")
  val port = conf.getString("email.port")
  val timeout = conf.getString("email.timeout") //ms
}

class Email(conf: EmailConfig) {
  val properties = new Properties()
  properties.put("mail.transport.protocol", "smtp")
  properties.put("mail.smtp.host", conf.host)
  properties.put("mail.smtp.port", conf.port)
  properties.put("mail.smtp.auth", "true")
  properties.put("mail.smtp.starttls.enable", "true")
  properties.put("mail.smtp.connectiontimeout", conf.timeout)
  properties.put("mail.smtp.timeout", conf.timeout)

  val authenticator = new Authenticator() {
    override def getPasswordAuthentication = new
        PasswordAuthentication(conf.login, conf.password)
  }

  def createMessage(to: String, subject: String, content: String): Message = {
    val session = Session.getInstance(properties, authenticator)
    val message = new MimeMessage(session)
    message.setFrom(new InternetAddress(conf.from))
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
      case e: AuthenticationFailedException =>
        throw new AuthorizationError(e.getMessage)
      case e: SendFailedException =>
        throw new SendError(e.getMessage)
      case e: SocketTimeoutException =>
        throw new TimeoutError(e.getMessage)
      case e: MessagingException =>
        throw new InternalError(e.getMessage)
      case e: Throwable =>
        throw new InternalError(e.toString)
    }
  }

  def send(e: EmailNotification) = sendMessage(e.email, "Weather Streams Alert", e.toString())
}
