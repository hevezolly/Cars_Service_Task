package modules

import model.Statistics._
import model.logging._
import model.repositiries.CarsDAO
import model.services.CarsServiceImpl
import play.api.db.slick.DatabaseConfigProvider

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class MainRepository @Inject()(val configProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends CarsDAO(configProvider) with SaveToFileRepositoryStatistic with RepoLogger with FileLogger {}

@Singleton
class MainService() extends CarsServiceImpl with ServiceLogger with FileLogger {}
