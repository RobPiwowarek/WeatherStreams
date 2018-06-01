package integration

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import org.scalatest.{AsyncFlatSpec, Matchers}
import server.database.MariaDb
import server.database.model.{AlertDefinition, DefinitionParameter, TableQueries}
import slick.jdbc.MySQLProfile.api._
import spray.json._

import scala.concurrent.Future

case class TestAlertRequest(id: Long,
                            userId: Int,
                            alertName: String,
                            duration: Int,
                            location: String,
                            active: Boolean,
                            emailNotif: Boolean,
                            slackNotif: Boolean,
                            parameters: Array[TestParameter]) {

  def ===(alert: AlertDefinition) = {
    alert.weatherUserId == userId &&
      alert.alertName == alertName &&
      alert.duration == duration &&
      alert.location == location &&
      alert.active == active &&
      alert.emailNotif == emailNotif &&
      alert.slackNotif == slackNotif
  }
}

case class TestParameter(parameterName: String,
                         parameterLimit: Int,
                         comparisonType: Int) {

  def ===(defParam: DefinitionParameter) = {
    defParam.parameterName == parameterName &&
      defParam.parameterLimit == parameterLimit &&
      defParam.comparisonType == comparisonType
  }
}

trait JsonSupport extends DefaultJsonProtocol with SprayJsonSupport {

  implicit val testParameterFormat = jsonFormat3(TestParameter)
  implicit val testAlertRequestFormat = jsonFormat9(TestAlertRequest)
}

class WebServerIT extends AsyncFlatSpec with Matchers with JsonSupport {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  val apiUrl = "http://localhost:8090"
  val loginUrl = apiUrl + "/api/user/login"
  val alertDefUrl = apiUrl + "/api/config/definition"
  val userUrl = apiUrl + "/api/user"

  val mariaDb = MariaDb

  def createAlertRequest(request: TestAlertRequest) = {
    val entity = request.toJson.toString
    HttpEntity(ContentTypes.`application/json`, entity)
  }

  def createUserLoginRequest(user: String, pass: String) = {
    val user = "None"
    val pass = "None"
    val entity =
      s"""
         | {
         |   "username": "$user",
         |   "password": "$pass"
         | }
                  """.stripMargin
    HttpEntity(ContentTypes.`application/json`, entity)
  }

  behavior of "WebServer"
  it should "fail to handle a login request with bad credentials" in {
    val httpEntity = createUserLoginRequest("none", "none")

    val request = HttpRequest(method = HttpMethods.POST, uri = loginUrl, entity = httpEntity)

    val responseFuture: Future[HttpResponse] = Http().singleRequest(request)
    responseFuture
      .map {
        response => response.status.isFailure() shouldBe true
      }
  }

  it should "successfully handle a login request with good credentials" in {
    val conf = AutoFileLoginConfig
    val httpEntity = createUserLoginRequest(conf.username, conf.password)

    val request = HttpRequest(method = HttpMethods.POST, uri = loginUrl, entity = httpEntity)

    val responseFuture: Future[HttpResponse] = Http().singleRequest(request)
    responseFuture
      .map {
        response => response.status.isFailure() shouldBe true
      }
  }

  it should "insert a new alert definition into the database" in {
    val parameters: Array[TestParameter] = Array(TestParameter("TEMP", 10, 1))
    val testAlertRequest = TestAlertRequest(0, 0, "test123", 20, "Warsaw", false, true, false, parameters)

    val httpEntity = createAlertRequest(testAlertRequest)
    val request = HttpRequest(method = HttpMethods.PUT, uri = alertDefUrl, entity = httpEntity)
    val httpFuture = Http().singleRequest(request)

    def combinedFuture = for {
      httpResult <- httpFuture

      newAlertDef: TestAlertRequest = httpResult
        .entity
        .getDataBytes()
        .toString()
        .parseJson
        .convertTo[TestAlertRequest]

      alertQuery = TableQueries.alertDefinitions
        .filter(_.id === newAlertDef.id)
        .result
        .headOption

      dbAlertDef: Option[AlertDefinition] <- mariaDb.db.run(alertQuery)

      paramQuery = TableQueries.definitionParameters
        .filter(_.alertDefinitionId === newAlertDef.id)
        .result
        .headOption

      dbParamDef: Option[DefinitionParameter] <- mariaDb.db.run(paramQuery)

      deleteAlertRowCount: Int <- mariaDb.db.run(TableQueries.alertDefinitions
        .filter(_.id === newAlertDef.id)
        .delete)

      deleteParamRowCount: Int <- mariaDb.db.run(TableQueries.definitionParameters
        .filter(_.alertDefinitionId === newAlertDef.id)
        .delete)

    } yield (dbAlertDef, dbParamDef, deleteAlertRowCount, deleteParamRowCount)

    combinedFuture
      .map {
        case (alert: Option[AlertDefinition],
        param: Option[DefinitionParameter],
        deleteAlertRowCount: Int,
        deleteParamRowCount: Int) =>

          alert.nonEmpty shouldBe true
          param.nonEmpty shouldBe true
          deleteAlertRowCount shouldBe 1
          deleteParamRowCount shouldBe 1
      }
  }

  it should "update an existing alert definition in the database" in {
    val alertName = "test1234test1234"
    val city = "Jakarta"
    val parameters: Array[TestParameter] = Array(TestParameter("RAIN", 30, 2))

    // add definition to database
    val oldAlertDef = (TableQueries.alertDefinitions returning TableQueries.alertDefinitions.map(_.id)) +=
      AlertDefinition(999, 0, "test123", 20, "Warsaw", false, true, false)

    // get id
    val alertIdFuture: Future[Long] = mariaDb.db.run(oldAlertDef)

    def combinedFuture = for {
      alertId <- alertIdFuture

      httpEntity = createAlertRequest(
        TestAlertRequest(
          alertId, 0, "test12345", 50, "Jakarta", false, false, false, parameters
        )
      )

      request = HttpRequest(method = HttpMethods.PUT, uri = alertDefUrl, entity = httpEntity)
      result <- Http().singleRequest(request)

      alertQuery = TableQueries.alertDefinitions
        .filter(_.id === alertId)
        .result
        .headOption

      dbAlertDef: Option[AlertDefinition] <- mariaDb.db.run(alertQuery)

      paramQuery = TableQueries.definitionParameters
        .filter(_.alertDefinitionId === alertId)
        .result
        .headOption

      dbParamDef: Option[DefinitionParameter] <- mariaDb.db.run(paramQuery)

      deleteAlertRowCount <- mariaDb.db.run(TableQueries.alertDefinitions
        .filter(_.id === alertId)
        .delete)

      deleteParamRowCount <- mariaDb.db.run(TableQueries.definitionParameters
        .filter(_.alertDefinitionId === alertId)
        .delete)
    } yield (result.status, dbAlertDef, dbParamDef, deleteAlertRowCount, deleteParamRowCount)

    combinedFuture
      .map {
        case (status: StatusCode,
          alert: Option[AlertDefinition],
        param: Option[DefinitionParameter],
        deleteAlertRowCount: Int,
        deleteParamRowCount: Int) =>

          status.isSuccess shouldBe true
          alert.nonEmpty shouldBe true
          param.nonEmpty shouldBe true
          deleteAlertRowCount shouldBe 1
          deleteParamRowCount shouldBe 1
      }
  }
}
