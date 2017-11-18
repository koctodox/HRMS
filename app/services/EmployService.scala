package services

import javax.inject.Inject

import daos.EmployDao
import models.EmployEntity
import play.api.libs.json.{Json, Writes}

import scala.concurrent.{ExecutionContext, Future}

class EmployService @Inject()(employDao: EmployDao)(implicit val ec: ExecutionContext) {

  def addEmploy(employ: EmployEntity): Future[String] = {
    (employ.name.isEmpty || employ.family.isEmpty) match {
      case true => Future.successful("""{"ok":"false","message":"name or family is empty. please fill it"}""")
      case false => (employ.nationalId.isEmpty || employ.zipCode.isEmpty || employ.address.isEmpty || employ.employStatus.isEmpty || employ.phone.isEmpty) match {
        case true => Future.successful("""{"ok":"false","message":"please fill the empty fields"}""")
        case false => (employ.salary < 7000) match {
          case true => Future.successful("""{"ok":"false","message":"the employ salary must bigger than employs low"}""")
          case false => employDao.insert(employ).map(newId => s"""{"ok":"true","message":"operation success full","id":"$newId"}""")
        }
      }
    }
  }

  def editEmploy(employ: EmployEntity): Future[String] = {
    (employ.id.isEmpty) match {
      case true => Future.successful("""{"ok":"false","message","id field is empty"}""")
      case false => (employ.name.isEmpty || employ.family.isEmpty) match {
        case true => Future.successful("""{"ok":"false","message":"name or family is empty. please fill it"}""")
        case false => (employ.nationalId.isEmpty || employ.zipCode.isEmpty || employ.address.isEmpty || employ.employStatus.isEmpty || employ.phone.isEmpty) match {
          case true => Future.successful("""{"ok":"false","message":"please fill the empty fields"}""")
          case false => (employ.salary < 7000) match {
            case true => Future.successful("""{"ok":"false","message":"the employ salary must bigger than employs low"}""")
            case false => employDao.update(employ).map(many => s"""{"ok":"true","message":"${many} row is edited"}""")
          }
        }
      }
    }
  }

  def deleteEmploy(employId: Long): Future[String] = {
    employDao.delete(employId) flatMap {
      case 0 => Future.successful("""{"ok":"false","message":"operation failed !!!"}""")
      case _ => Future.successful("""{"ok":"false","message":"operation successful"}""")
    }
  }

  def getById(employId: Long): Future[String] = {

    implicit val employWrites = new Writes[EmployEntity] {
      def writes(employEntity: EmployEntity) = Json.obj(
        "name" -> employEntity.name,
        "family" -> employEntity.family,
        "nationalId" -> employEntity.nationalId,
        "zipCode" -> employEntity.zipCode,
        "phone" -> employEntity.phone,
        "address" -> employEntity.address,
        "employStatus" -> employEntity.employStatus,
        "salary" -> employEntity.salary,
        "id" -> employEntity.id
      )
    }

    employDao.findById(employId) flatMap {
      case None => Future.successful("""{"ok":"false","message":"not found!"}""")
      case Some(employEntity)=>
        val jsonEmploy = Json.toJson(employEntity)
        Future.successful(s"""{"ok":"true","result":"$jsonEmploy"}""")
    }
  }

}
