package poc

import io.github.gaelrenoux.tranzactio.doobie._
import poc.database.Repository
import poc.database.Repository.Repository
import zio.{Has, UIO, ZIO, ZLayer}

object DataService {

  type DataService = Has[DataService.Service]

  trait Service {

    val printCurrentValue: UIO[Unit]

  }

  val printCurrentValue: ZIO[DataService, Nothing, Unit] = ZIO.accessM[DataService](_.get.printCurrentValue)

  val live: ZLayer[Repository with Has[Database.Service], Nothing, DataService] =
    ZLayer.fromServices[Repository.Service, Database.Service, DataService.Service] { (repo, db) =>
      new Service {
        override val printCurrentValue: UIO[Unit] = db
          .transactionOrDie(for {
            result <- repo.get
            _      <- ZIO.effectTotal(println(result))
          } yield ())
          .resurrect
          .catchAll(err => ZIO.effectTotal(err.printStackTrace()))
      }
    }

}
