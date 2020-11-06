package poc.database

import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource
import poc.config.AppConfig
import zio.blocking.Blocking
import zio.config.ZConfig
import zio.{Has, ZIO, ZLayer, blocking}

object Source {

  type Source = Has[Source.Service]

  trait Service {}

  val live: ZLayer[Blocking with ZConfig[AppConfig.DatabaseConfig], Throwable, Has[DataSource]] = ZIO
    .accessM[Blocking with ZConfig[AppConfig.DatabaseConfig]] { env =>
      blocking.effectBlocking {
        val dbConf = env.get[AppConfig.DatabaseConfig]
        val ds = new HikariDataSource()
        ds.setJdbcUrl(dbConf.url)
        ds.setUsername(dbConf.user)
        ds.setPassword(dbConf.password)
        ds.setMaximumPoolSize(dbConf.maxPoolSize)
        ds
      }
    }
    .toLayer

}
