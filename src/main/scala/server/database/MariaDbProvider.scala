package server.database

class MariaDbProvider extends DatabaseProvider {
  override def provide(): DatabaseInterface = new MariaDb
}
