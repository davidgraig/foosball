package models

import play.api.libs.json.Json

import scala.concurrent.Future

case class GameRecord(id: Long, table: Table) {
  val player1 = table.player1
  val player2 = table.player2
  val player1Score = table.player1Score
  val player2Score = table.player2Score

}

object GameRecord {
  import FakeDB.games

  implicit val writes = Json.writes[GameRecord]

  def insert(table: Table): Future[(Long, GameRecord)] = Future.successful {
    games.insert(GameRecord(_, table))
  }
}
