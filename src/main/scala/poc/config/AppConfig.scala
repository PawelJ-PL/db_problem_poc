package poc.config

import zio.Layer
import zio.config.magnolia.DeriveConfigDescriptor.descriptor
import zio.config.typesafe.TypesafeConfig.fromDefaultLoader
import zio.config.{ReadError, ZConfig, toKebabCase}

final case class AppConfig(db: AppConfig.DatabaseConfig)

object AppConfig {

  final case class DatabaseConfig(url: String, user: String, password: String, maxPoolSize: Int)

  private val configDescriptor = descriptor[AppConfig].mapKey(toKebabCase)

  val live: Layer[ReadError[String], ZConfig[AppConfig]] = fromDefaultLoader(configDescriptor)

}
