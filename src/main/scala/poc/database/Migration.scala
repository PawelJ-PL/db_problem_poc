package poc.database

import java.sql.DriverManager

import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import poc.config.AppConfig
import zio.config.ZConfig
import zio.{Has, Task, ZIO, ZLayer, ZManaged}

object Migration {

  type Migration = Has[Migration.Service]

  trait Service {

    def run: Task[Unit]

  }

  val run: ZIO[Migration, Throwable, Unit] = ZIO.accessM[Migration](_.get.run)

  val live: ZLayer[ZConfig[AppConfig.DatabaseConfig], Nothing, Migration] =
    ZLayer.fromService[AppConfig.DatabaseConfig, Migration.Service](dbConfig =>
      new Service {

        private final val LiquibaseChangelogMaster = "db/changelog/changelog-master.yml"

        override val run: Task[Unit] =
          (for {
            connection <- ZManaged.fromAutoCloseable(Task(DriverManager.getConnection(dbConfig.url, dbConfig.user, dbConfig.password)))
            database   <- ZManaged.make(
                            Task(DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection)))
                          )(connection => Task(connection.close()).orDie)
            liquibase  <- ZManaged.fromEffect(
                            Task(new Liquibase(LiquibaseChangelogMaster, new ClassLoaderResourceAccessor(getClass.getClassLoader), database))
                          )
          } yield liquibase).use(liquibase => Task(liquibase.update("main")))

      }
    )

}
