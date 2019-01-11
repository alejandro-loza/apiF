@Grab('mysql:mysql-connector-java:5.1.45')
@GrabConfig(systemClassLoader=true)

import groovy.sql.Sql
import java.sql.*

def sql = Sql.newInstance("jdbc:mysql://put_db_host/backend?verifyServerCertificate=true&useSSL=true&requireSSL=false&noAccessToProcedureBodies=true","put_usr", "put_pwd", "com.mysql.jdbc.Driver")
def counter=0
def total=0

sql.eachRow("select id from user") { row ->

 def userId=row.id
  println "Searching duplicates with user: ${userId} number: ${total++}"

  def checkMovementStat=
  """SELECT count(*) c 
     FROM movement_stat  
     WHERE user_id=${userId} 
     GROUP BY user_id, category_id, type, init_date, final_date HAVING c > 1
  """
  def result = sql.rows( checkMovementStat )
  long count = result.size

  if ( count > 0 ){
        counter++
        println "Duplicate in user ${userId}"
          def deleteMovementStat=
          """DELETE FROM movement_stat  
             WHERE user_id=${userId}
          """
  println "Removed movementStat records for ${userId}"
  sql.execute(deleteMovementStat)
  sql.execute( "CAll sp_insert_by_user(${userId},1,1)" )
  println "Inserted for 1 day"
  sql.execute( "CAll sp_insert_by_user(${userId},13,16)" )
  println "Inserted fro 15 days"
  sql.execute( "CAll sp_insert_by_user(${userId},28,31)" )
   println "Inserted for 1 month"

  }

}
  println "Total of fixed user ${counter}"
sql.close()
