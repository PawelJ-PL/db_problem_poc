package poc.database

import io.github.gaelrenoux.tranzactio.DbException
import io.github.gaelrenoux.tranzactio.doobie.Connection
import zio.{Has, ZIO, ZLayer}

object Repository {

  type Repository = Has[Repository.Service]

  trait Service {

    val get: ZIO[Connection, DbException, Option[String]]

  }

  val live = ZLayer.succeed(new Service {
    import doobie._
    import doobie.implicits._
    import io.github.gaelrenoux.tranzactio._
    import io.github.gaelrenoux.tranzactio.doobie._

    override val get: ZIO[Connection, DbException, Option[String]] = tzio {
      sql"select text from something".query[String].to[List]
    }.map(_.headOption)

  })

}
