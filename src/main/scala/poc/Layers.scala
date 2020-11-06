package poc

import java.time.Duration

import DataService.DataService
import io.github.gaelrenoux.tranzactio.{ErrorStrategies, ErrorStrategiesRef}
import io.github.gaelrenoux.tranzactio.doobie.{Database => DoobieDb}
import poc.config.AppConfig
import poc.database.{Migration, Repository, Source}
import poc.database.Migration.Migration
import zio.ZLayer
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
    val errorStrategyWithRetryTimeout = ZLayer.succeed(
      ErrorStrategies.RetryForever.withTimeout(Duration.ofSeconds(10)).withRetryTimeout(Duration.ofSeconds(3)): ErrorStrategiesRef
    )
    val errorStrategyWithoutRetryTimeout =
      ZLayer.succeed(ErrorStrategies.RetryForever.withTimeout(Duration.ofSeconds(10)): ErrorStrategiesRef)
    val dbWithRetryTimeout =
      dataSource ++ blocking ++ errorStrategyWithRetryTimeout ++ clock >>> DoobieDb.fromDatasourceAndErrorStrategies // with retry timeout
    val dbWithoutRetryTimeout =
      dataSource ++ blocking ++ errorStrategyWithoutRetryTimeout ++ clock >>> DoobieDb.fromDatasourceAndErrorStrategies // without retry timeout - only this is able to restore connection
    val dbWithDefaultErrorStrategy = dataSource ++ blocking ++ clock >>> DoobieDb.fromDatasource // with default error strategy
    val dataService = repo ++ dbWithoutRetryTimeout >>> DataService.live

    dbMigration ++ dataService
  }

}
