package poc

import java.time.Duration

import DataService.DataService
import io.github.gaelrenoux.tranzactio.ErrorStrategies
import io.github.gaelrenoux.tranzactio.doobie.{Database => DoobieDb}
import poc.config.AppConfig
import poc.database.{Migration, Repository, Source}
import poc.database.Migration.Migration
import zio.{Has, ULayer, ZLayer}
import zio.blocking.Blocking
import zio.clock.Clock
import zio.config.syntax._

object Layers {

  val live: ZLayer[Any, Throwable, Migration with DataService] = {
    val dbConfig = AppConfig.live.narrow(_.db)
    val dbMigration = dbConfig >>> Migration.live
    val blocking = Blocking.live
    val dataSource = blocking ++ AppConfig.live.narrow(_.db) >>> Source.live
    val repo = Repository.live
    val clock = Clock.live

    val errorStrategies = ZLayer.succeed(ErrorStrategies.Nothing)
    val errorStrategiesWithTimeoutAndRetry = ZLayer.succeed(ErrorStrategies.timeout(Duration.ofSeconds(2)).retryCountFixed(5, Duration.ofSeconds(3)))
    val db = dataSource ++ blocking ++ clock ++ errorStrategiesWithTimeoutAndRetry >>> DoobieDb.fromDatasourceAndErrorStrategies
    val dataService = repo ++ db >>> DataService.live

    dbMigration ++ dataService
  }

}
