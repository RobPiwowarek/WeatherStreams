package notifications

trait Sender {
  def send(msg: Any): Unit
}
