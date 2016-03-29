name := "SparkIP"

version := "1.0"

scalaVersion := "2.11.7"

scalacOptions ++= Seq(
  "-optimize",
  "-unchecked",
  "-deprecation"
)

classpathTypes += "maven-plugin"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "1.6.0" % "provided",
  "org.apache.spark" %% "spark-streaming" % "1.6.0",
  "org.apache.spark" %% "spark-mllib" % "1.6.0",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "org.bytedeco" % "javacpp" % "0.11",
  "org.bytedeco" % "javacv" % "0.11",
  "org.bytedeco.javacpp-presets" % "opencv" % "2.4.11-0.11",
  "org.bytedeco.javacpp-presets" % "opencv" % "2.4.11-0.11" classifier "windows-x86_64",
  "org.apache.httpcomponents" % "httpclient" % "4.5.2",
  "net.liftweb" % "lift-json_2.11" % "3.0-M8",
  "commons-validator" % "commons-validator" % "1.5.0",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.3.0",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.3.0" classifier "models"

)

resolvers ++= Seq(
  "Akka Repository" at "http://repo.akka.io/releases/",
  "scala-tools" at "https://oss.sonatype.org/content/groups/scala-tools",
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Second Typesafe repo" at "http://repo.typesafe.com/typesafe/maven-releases/",
  "JavaCV maven repo" at "http://maven2.javacv.googlecode.com/git/",
  "JavaCPP maven repo" at "http://maven2.javacpp.googlecode.com/git/",
  Resolver.sonatypeRepo("public")
)

//javacOptions in Compile ++= Seq("-source", "1.7", "-target", "1.7")