// Intentionally Vulnerable Scala/Play Framework Application
// DO NOT USE IN PRODUCTION - FOR SECURITY TESTING ONLY

package com.vulnerable

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import spray.json._
import DefaultJsonProtocol._
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.sys.process._
import scala.io.Source
import java.io.File
import java.security.MessageDigest
import scala.util.Random

// VULNERABILITY: Hardcoded secrets (CWE-798)
object Constants {
  val JWT_SECRET = "super_secret_jwt_key_12345"
  val ADMIN_PASSWORD = "admin123"
  val DB_PASSWORD = "password123"
  val API_KEY = "AKIA_FAKE_SCALA_KEY_FOR_TESTING_ONLY"
}

case class User(id: Int, username: String, password: String, email: String, role: String)
case class LoginRequest(username: String, password: String)
case class RegisterRequest(username: String, password: String, email: String, role: Option[String])
case class HashRequest(password: String)

// JSON formatters
object JsonProtocol extends DefaultJsonProtocol {
  implicit val userFormat = jsonFormat5(User)
  implicit val loginFormat = jsonFormat2(LoginRequest)
  implicit val registerFormat = jsonFormat4(RegisterRequest)
  implicit val hashFormat = jsonFormat1(HashRequest)
}

import JsonProtocol._

object Main extends App {
  implicit val system: ActorSystem = ActorSystem("vulnerable-scala-app")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  // In-memory user store (simulating database)
  var users = List(
    User(1, "admin", "hashed_password", "admin@example.com", "admin"),
    User(2, "user", "hashed_password", "user@example.com", "user")
  )

  val route =
    path("") {
      get {
        complete(HttpResponse(
          entity = HttpEntity(ContentTypes.`text/html(UTF-8)`,
            """<html>
              |<head><title>Vulnerable Scala App</title></head>
              |<body>
              |  <h1>Intentionally Vulnerable Scala/Akka HTTP Application</h1>
              |  <p>This application contains numerous security vulnerabilities for testing purposes.</p>
              |  <h2>Available Endpoints:</h2>
              |  <ul>
              |    <li>POST /api/login - SQL Injection</li>
              |    <li>GET /api/exec?cmd=ls - Command Injection</li>
              |    <li>GET /api/files?filename=test.txt - Path Traversal</li>
              |    <li>GET /api/search?query=test - XSS</li>
              |    <li>GET /api/proxy?url=http://example.com - SSRF</li>
              |    <li>POST /api/eval - Code Injection</li>
              |    <li>POST /api/register - Mass Assignment</li>
              |    <li>GET /api/users/{id} - IDOR</li>
              |    <li>DELETE /api/admin/users/{id} - Missing Authentication</li>
              |    <li>GET /api/debug - Sensitive Data Exposure</li>
              |    <li>GET /api/redirect?url=http://example.com - Open Redirect</li>
              |    <li>POST /api/hash - Weak Cryptography</li>
              |  </ul>
              |</body>
              |</html>""".stripMargin)
        ))
      }
    } ~
    // VULNERABILITY: SQL Injection (CWE-89)
    path("api" / "login") {
      post {
        entity(as[String]) { body =>
          val loginData = body.parseJson.convertTo[LoginRequest]
          // Vulnerable: String interpolation in SQL query
          val query = s"SELECT * FROM users WHERE username = '${loginData.username}' AND password = '${loginData.password}'"
          complete(HttpResponse(
            entity = HttpEntity(ContentTypes.`application/json`,
              s"""{"query": "$query", "vulnerable": true}""")
          ))
        }
      }
    } ~
    // VULNERABILITY: Command Injection (CWE-78)
    path("api" / "exec") {
      get {
        parameter("cmd") { cmd =>
          // Vulnerable: Direct execution of user input
          val output = cmd.!!
          complete(HttpResponse(
            entity = HttpEntity(ContentTypes.`application/json`,
              s"""{"success": true, "output": "${output.replace("\"", "\\\"")}"}""")
          ))
        }
      }
    } ~
    // VULNERABILITY: Path Traversal (CWE-22)
    path("api" / "files") {
      get {
        parameter("filename") { filename =>
          // Vulnerable: No sanitization of file path
          try {
            val content = Source.fromFile(s"./uploads/$filename").mkString
            complete(HttpResponse(
              entity = HttpEntity(ContentTypes.`application/json`,
                s"""{"content": "${content.replace("\"", "\\\"")}"}""")
            ))
          } catch {
            case e: Exception =>
              complete(HttpResponse(
                entity = HttpEntity(ContentTypes.`application/json`,
                  s"""{"error": "${e.getMessage}"}""")
              ))
          }
        }
      }
    } ~
    // VULNERABILITY: Cross-Site Scripting (XSS) (CWE-79)
    path("api" / "search") {
      get {
        parameter("query") { query =>
          // Vulnerable: Reflects user input without sanitization
          complete(HttpResponse(
            entity = HttpEntity(ContentTypes.`text/html(UTF-8)`,
              s"<h1>Search Results for: $query</h1>")
          ))
        }
      }
    } ~
    // VULNERABILITY: Server-Side Request Forgery (SSRF) (CWE-918)
    path("api" / "proxy") {
      get {
        parameter("url") { url =>
          // Vulnerable: No URL validation
          try {
            val content = Source.fromURL(url).mkString
            complete(HttpResponse(
              entity = HttpEntity(ContentTypes.`application/json`,
                s"""{"data": "${content.take(500).replace("\"", "\\\"")}"}""")
            ))
          } catch {
            case e: Exception =>
              complete(HttpResponse(
                entity = HttpEntity(ContentTypes.`application/json`,
                  s"""{"error": "${e.getMessage}"}""")
              ))
          }
        }
      }
    } ~
    // VULNERABILITY: Code Injection / Eval (CWE-94)
    path("api" / "eval") {
      post {
        entity(as[String]) { code =>
          // Vulnerable: This demonstrates the eval pattern (Scala doesn't have direct eval)
          complete(HttpResponse(
            entity = HttpEntity(ContentTypes.`application/json`,
              s"""{"message": "Code evaluation endpoint (vulnerable pattern)", "input": "$code"}""")
          ))
        }
      }
    } ~
    // VULNERABILITY: Mass Assignment (CWE-915)
    path("api" / "register") {
      post {
        entity(as[String]) { body =>
          val registerData = body.parseJson.convertTo[RegisterRequest]
          // Vulnerable: Allows setting 'role' field directly
          val newUser = User(
            users.length + 1,
            registerData.username,
            registerData.password,
            registerData.email,
            registerData.role.getOrElse("user") // Attacker can set role=admin
          )
          users = users :+ newUser
          complete(HttpResponse(
            entity = HttpEntity(ContentTypes.`application/json`,
              s"""{"success": true, "user": ${newUser.toJson.toString}}""")
          ))
        }
      }
    } ~
    // VULNERABILITY: Insecure Direct Object Reference (IDOR) (CWE-639)
    path("api" / "users" / IntNumber) { userId =>
      get {
        // Vulnerable: No authorization check
        val user = users.find(_.id == userId)
        complete(HttpResponse(
          entity = HttpEntity(ContentTypes.`application/json`,
            s"""{"user": ${user.map(_.toJson.toString).getOrElse("null")}}""")
        ))
      }
    } ~
    // VULNERABILITY: Missing Authentication (CWE-306)
    path("api" / "admin" / "users" / IntNumber) { userId =>
      delete {
        // Vulnerable: No authentication or authorization required
        users = users.filterNot(_.id == userId)
        complete(HttpResponse(
          entity = HttpEntity(ContentTypes.`application/json`,
            s"""{"success": true, "deleted": $userId}""")
        ))
      }
    } ~
    // VULNERABILITY: Sensitive Data Exposure (CWE-200)
    path("api" / "debug") {
      get {
        complete(HttpResponse(
          entity = HttpEntity(ContentTypes.`application/json`,
            s"""{
              |  "jwt_secret": "${Constants.JWT_SECRET}",
              |  "admin_password": "${Constants.ADMIN_PASSWORD}",
              |  "db_password": "${Constants.DB_PASSWORD}",
              |  "api_key": "${Constants.API_KEY}",
              |  "users": ${users.toJson.toString}
              |}""".stripMargin)
        ))
      }
    } ~
    // VULNERABILITY: Open Redirect (CWE-601)
    path("api" / "redirect") {
      get {
        parameter("url") { url =>
          // Vulnerable: No validation of redirect URL
          complete(HttpResponse(
            status = StatusCodes.Found,
            headers = List(headers.Location(url))
          ))
        }
      }
    } ~
    // VULNERABILITY: Weak Cryptography (CWE-327)
    path("api" / "hash") {
      post {
        entity(as[String]) { body =>
          val hashData = body.parseJson.convertTo[HashRequest]
          // Vulnerable: Using MD5
          val md5 = MessageDigest.getInstance("MD5")
          val hash = md5.digest(hashData.password.getBytes).map("%02x".format(_)).mkString
          complete(HttpResponse(
            entity = HttpEntity(ContentTypes.`application/json`,
              s"""{"hash": "$hash", "algorithm": "MD5"}""")
          ))
        }
      }
    } ~
    // VULNERABILITY: Insecure Randomness (CWE-330)
    path("api" / "generate-token") {
      get {
        // Vulnerable: Using predictable Random
        val token = Random.nextInt().toString
        complete(HttpResponse(
          entity = HttpEntity(ContentTypes.`application/json`,
            s"""{"token": "$token", "algorithm": "scala.util.Random (predictable)"}""")
        ))
      }
    } ~
    // VULNERABILITY: Hardcoded Credentials (CWE-798)
    path("api" / "admin-login") {
      post {
        entity(as[String]) { body =>
          val password = body.parseJson.asJsObject.fields.get("password").map(_.convertTo[String]).getOrElse("")
          // Vulnerable: Hardcoded admin password
          if (password == Constants.ADMIN_PASSWORD) {
            complete(HttpResponse(
              entity = HttpEntity(ContentTypes.`application/json`,
                """{"success": true, "role": "admin"}""")
            ))
          } else {
            complete(HttpResponse(
              entity = HttpEntity(ContentTypes.`application/json`,
                """{"success": false}""")
            ))
          }
        }
      }
    } ~
    // VULNERABILITY: Information Exposure Through Error Messages (CWE-209)
    path("api" / "database-connect") {
      get {
        // Vulnerable: Detailed error messages exposed
        val errorMsg = s"Connection failed: Access denied for user 'root'@'localhost' using password '${Constants.DB_PASSWORD}'"
        complete(HttpResponse(
          entity = HttpEntity(ContentTypes.`application/json`,
            s"""{"error": "$errorMsg", "stackTrace": "Simulated stack trace"}""")
        ))
      }
    } ~
    // VULNERABILITY: Missing Rate Limiting (CWE-770)
    path("api" / "brute-force-target") {
      post {
        entity(as[String]) { body =>
          val password = body.parseJson.asJsObject.fields.get("password").map(_.convertTo[String]).getOrElse("")
          // Vulnerable: No rate limiting, allows brute force
          if (password == "correct_password") {
            complete(HttpResponse(
              entity = HttpEntity(ContentTypes.`application/json`,
                """{"success": true}""")
            ))
          } else {
            complete(HttpResponse(
              entity = HttpEntity(ContentTypes.`application/json`,
                """{"success": false}""")
            ))
          }
        }
      }
    }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

  println("Starting Vulnerable Scala/Akka HTTP Application...")
  println("WARNING: This application is intentionally vulnerable!")
  println("Access at: http://localhost:8080")

  scala.io.StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
