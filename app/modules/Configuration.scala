package modules

import model.Statistics._
import model.logging._
import model.repositiries.{CarsDAO, CarsRepository}
import model.services.CarsServiceImpl
import play.api.db.slick.DatabaseConfigProvider

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class MainRepository @Inject()(val configProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends CarsDAO(configProvider)
    with SaveToFileRepositoryStatistic
    with RepoLogger
    with FileLogger
    with ConsoleLogger {}

@Singleton
class MainService @Inject()(val repo: CarsRepository)
  extends CarsServiceImpl(repo)
    with ServiceLogger
    with FileLogger
    with ConsoleLogger {}
