package controllers

import javax.inject.Inject

import models.Table
import play.api.i18n.MessagesApi

import scala.concurrent.ExecutionContext.Implicits.global

class Tables @Inject() (val messagesApi: MessagesApi) extends api.ApiController {
  def list = ApiAction { implicit request =>
    Table.list.flatMap { list =>
      ok(list)
    }
  }

  def info(id: Long) = ApiAction { implicit request =>
    Table.findById(id).flatMap { table =>
      ok(table)
    }
  }

  def goal(id: Long, isPlayer1: Boolean) = SecuredApiAction { implicit request =>
    Table.increment(id, isPlayer1, 1).flatMap { table =>
      ok(table)
    }
  }

  def redact(id: Long, isPlayer1: Boolean) = SecuredApiAction { implicit request =>
    Table.increment(id, isPlayer1, -1).flatMap { table =>
      ok(table)
    }
  }

  def reset(id: Long) = SecuredApiAction { implicit request =>
    Table.reset(id).flatMap { table =>
      ok(table)
    }
  }

  def setOnline(id: Long) = SecuredApiAction { implicit request =>
    Table.setOnline(id, isOnline = true).flatMap { table =>
      ok(table)
    }
  }

  def setOffline(id: Long) = SecuredApiAction { implicit request =>
    Table.setOnline(id, isOnline = false).flatMap { table =>
      ok(table)
    }
  }
}