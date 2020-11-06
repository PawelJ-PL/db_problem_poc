package poc

import java.time.Duration

import DataService.DataService
import poc.database.Migration
import poc.database.Migration.Migration
import zio.clock.Clock
import zio.{ExitCode, Schedule, URIO, ZIO, App => ZioApp}
import zio.console.putStrLn

object Application extends ZioApp {

  private val scheduler = Schedule.forever && Schedule.windowed(Duration.ofSeconds(2))

  private val action: ZIO[Clock with Migration with DataService, Throwable, Unit] = for {
    _ <- Migration.run
    _ <- DataService.printCurrentValue.repeat(scheduler).unit
  } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    action.provideCustomLayer(Layers.live).foldCauseM(
      cause => putStrLn(cause.prettyPrint).as(ExitCode.failure),
      _ => ZIO.succeed(ExitCode.success)
    )

}
