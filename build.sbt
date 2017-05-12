name := "Inventory"

version := "1.0"

lazy val `inventory` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq( cache , ws   , specs2 % Test )

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.36"

libraryDependencies += "com.typesafe.play" %% "play-slick" % "2.1.0"

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"  

fork in run := false