package models

import api.Page
import java.text.SimpleDateFormat
import scala.collection.mutable.Map

/*
* A fake DB to store and load all the data
*/
object FakeDB {

  val dt = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss")

  // API KEYS
  val apiKeys = FakeTable(
    1L -> ApiKey(apiKey = "AbCdEfGhIjK1", name = "ios-app", active = true),
    2L -> ApiKey(apiKey = "AbCdEfGhIjK2", name = "android-app", active = true)
  )

  // TOKENS
  val tokens = FakeTable[ApiToken]()

  // API REQUEST LOG
  val logs = FakeTable[ApiLog]()

  // USERS
  val users = FakeTable(
    1L -> User(1L, "user1@mail.com", "123456", "User 1", true, true),
    2L -> User(2L, "user2@mail.com", "123456", "User 2", true, true),
    3L -> User(3L, "user3@mail.com", "123456", "User 3", true, true)
  )

  val tables = FakeTable(
    1L -> Table(1L, "HQ 1.1", users.get(1L), 0, users.get(2L), 0, isOnline = true),
    2L -> Table(2L, "HQ 1.2", None, 0, users.get(3L), 0, isOnline = false)
  )

  val games = FakeTable(
    1L -> GameRecord(1L, tables.get(1).get)
  )

  /*
	* Fake table that emulates a SQL table with an auto-increment index
	*/
  case class FakeTable[A](var table: Map[Long, A], var incr: Long) {
    def nextId: Long = {
      if (!table.contains(incr))
        incr
      else {
        incr += 1
        nextId
      }
    }
    def get(id: Long): Option[A] = table.get(id)
    def find(p: A => Boolean): Option[A] = table.values.find(p)
    def insert(a: Long => A): (Long, A) = {
      val id = nextId
      val tuple = (id -> a(id))
      table += tuple
      incr += 1
      tuple
    }
    def update(id: Long)(f: A => A): Boolean = {
      get(id).exists { a =>
        table += (id -> f(a))
        true
      }
    }
    def delete(id: Long): Unit = table -= id
    def delete(p: A => Boolean): Unit = table = table.filterNot { case (id, a) => p(a) }

    def values: List[A] = table.values.toList
    def map[B](f: A => B): List[B] = values.map(f)
    def filter(p: A => Boolean): List[A] = values.filter(p)
    def exists(p: A => Boolean): Boolean = values.exists(p)
    def count(p: A => Boolean): Int = values.count(p)
    def size: Int = table.size
    def isEmpty: Boolean = size == 0

    def page(p: Int, s: Int)(filterFunc: A => Boolean)(sortFuncs: ((A, A) => Boolean)*): Page[A] = {
      val items = filter(filterFunc)
      val sorted = sortFuncs.foldRight(items)((f, items) => items.sortWith(f))
      Page(
        items = sorted.drop((p - 1) * s).take(s),
        page = p,
        size = s,
        total = sorted.size
      )
    }
  }

  object FakeTable {
    def apply[A](elements: (Long, A)*): FakeTable[A] = apply(Map(elements: _*), elements.size + 1)
  }

}
