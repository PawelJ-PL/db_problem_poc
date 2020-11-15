name := "db-poc"

version := "0.1"

scalaVersion := "2.13.3"

libraryDependencies += "dev.zio" %% "zio" % "1.0.3"
libraryDependencies += "dev.zio" %% "zio-interop-cats" % "2.2.0.1"
libraryDependencies += "dev.zio" %% "zio-config" % "1.0.0-RC29"
libraryDependencies += "dev.zio" %% "zio-config-magnolia" % "1.0.0-RC29"
libraryDependencies += "dev.zio" %% "zio-config-typesafe" % "1.0.0-RC29"
libraryDependencies += "io.github.gaelrenoux" %% "tranzactio" % "1.1.0"
libraryDependencies += "org.yaml" % "snakeyaml" % "1.27"
libraryDependencies += "org.postgresql" % "postgresql" % "42.2.18"
libraryDependencies += "org.liquibase" % "liquibase-core" % "4.1.1"
libraryDependencies += "org.liquibase" % "liquibase-core" % "4.1.1"
libraryDependencies += "org.tpolecat" %% "doobie-postgres" % "0.9.2"
libraryDependencies += "com.zaxxer" % "HikariCP" % "3.4.5"
