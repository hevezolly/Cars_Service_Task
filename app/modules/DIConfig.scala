package modules
import model.Statistics.StatisticProvider
import model.logging.{FileLogsCollector, LogsCollector}
import model.services.{CarsService, CarsServiceImpl}
import model.repositiries.CarsRepository
import play.api.{Configuration, Environment}
import play.api.inject.Binding

class DIConfig extends play.api.inject.Module {
  override def bindings(environment: Environment, configuration: Configuration): collection.Seq[Binding[_]] =
    Seq(
      bind[CarsRepository].to[MainRepository],
      bind[StatisticProvider].to[MainRepository],
      bind[LogsCollector].to[FileLogsCollector],
      bind[CarsService].to[MainService],
    )
}
