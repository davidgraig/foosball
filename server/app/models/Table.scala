package models

import play.api.libs.json._

import scala.concurrent._

case class Table(id: Long,
    name: String,
    player1: Option[User],
    player1Score: Long,
    player2: Option[User],
    player2Score: Long,
    isOnline: Boolean) {
}

object Table {
  import FakeDB.tables

  implicit val writes = Json.writes[Table]

  def findById(id: Long): Future[Option[Table]] = Future.successful {
    tables.get(id)
  }

  def list: Future[Seq[Table]] = Future.successful {
    tables.values.sortBy(_.name)
  }

  def increment(id: Long, isPlayer1: Boolean, scoreIncrement: Integer): Future[Option[Table]] = {
    if (isPlayer1) {
      tables.update(id) { toUpdate =>
        toUpdate.copy(player1Score = toUpdate.player1Score + scoreIncrement)
      }
    } else {
      tables.update(id) { toUpdate =>
        toUpdate.copy(player2Score = toUpdate.player2Score + scoreIncrement)
      }
    }
    findById(id)
  }

  def submit(table: Table) = ???

  def reset(id: Long): Future[Option[Table]] = {
    tables.update(id) { toUpdate =>
      toUpdate.copy(player1 = None, player1Score = 0, player2 = None, player2Score = 0)
    }
    findById(id)
  }

  def setOnline(id: Long, isOnline: Boolean) = {
    tables.update(id) { toUpdate =>
      toUpdate.copy(isOnline = isOnline)
    }
    findById(id)
  }

}