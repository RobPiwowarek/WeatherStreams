package server.database

abstract class DatabaseProvider {

  def provide(): DatabaseInterface

}
