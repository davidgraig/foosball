package models

import play.api.libs.json.Json

import scala.concurrent.Future

case class User(
  id: Long,
  email: String,
  password: String,
  name: String,
  emailConfirmed: Boolean,
  active: Boolean)

object User {
  import FakeDB.users

  implicit val formats = Json.format[User]

  def findById(id: Long): Future[Option[User]] = Future.successful {
    users.get(id)
  }
  def findByEmail(email: String): Future[Option[User]] = Future.successful {
    users.find(_.email == email)
  }

  def insert(email: String, password: String, name: String): Future[(Long, User)] = Future.successful {
    users.insert(User(_, email, password, name, emailConfirmed = false, active = false))
  }

  def update(id: Long, name: String): Future[Boolean] = Future.successful {
    users.update(id)(_.copy(name = name))
  }

  def confirmEmail(id: Long): Future[Boolean] = Future.successful {
    users.update(id)(_.copy(emailConfirmed = true, active = true))
  }

  def updatePassword(id: Long, password: String): Future[Boolean] = Future.successful {
    users.update(id)(_.copy(password = password))
  }

  def delete(id: Long): Future[Unit] = Future.successful {
    users.delete(id)
  }

  def list: Future[Seq[User]] = Future.successful {
    users.values.sortBy(_.name)
  }

}
