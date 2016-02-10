package controllers

import javax.inject.Inject

import models.User
import play.api.i18n.MessagesApi
import play.api.libs.json._

import scala.concurrent.ExecutionContext.Implicits.global

class Users @Inject() (val messagesApi: MessagesApi) extends api.ApiController {

  def usernames = ApiAction { implicit request =>
    User.list.flatMap { list =>
      ok(list.map(u => Json.obj("id" -> u.id, "name" -> u.name)))
    }
  }

  def list = ApiAction { implicit request =>
    ???
  }

  def info(id: Long) = ApiAction { implicit request =>
    ???
  }

}