package controllers

import javax.inject.Inject

import models.{ GameRecord, Table }
import play.api.i18n.MessagesApi

import scala.util.Random
import scala.concurrent.ExecutionContext.Implicits.global

class GameRecords @Inject() (val messagesApi: MessagesApi) extends api.ApiController {

  def submit(id: Long) = SecuredApiAction { implicit request =>
    Table.findById(id).flatMap {
      case Some(table) => ok(Some(GameRecord(Random.nextLong(), table)))
      case None => ???
    }
  }

}
