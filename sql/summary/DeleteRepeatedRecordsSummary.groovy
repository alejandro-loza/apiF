@Grab('mysql:mysql-connector-java:5.1.45')
@GrabConfig(systemClassLoader=true)

import groovy.sql.Sql
import java.sql.*

def sql = Sql.newInstance("jdbc:mysql://hostdb:3306/backend?verifyServerCertificate=true&useSSL=true&requireSSL=false&noAccessToProcedureBodies=true","put_user", "put_pwd", "com.mysql.jdbc.Driver")
def counter=0
def total=0

sql.eachRow("select id from user") { row ->

 def userId=row.id
  println "Searching duplicates with user: ${userId} number: ${total++}"

def checkSummary=
  """SELECT count(*) c 
     FROM summary  
     WHERE user_id=${userId}
     GROUP BY user_id, category_id, description, init_date, final_date HAVING c > 1
  """
  
  def result = sql.rows( checkSummary )
  long count = result.size

  if ( count > 0 ){
   counter++
  println "Duplicate in user ${userId}"
  def deleteSummary=
  """DELETE FROM summary  
     WHERE user_id=${userId}
  """
  sql.execute(deleteSummary)
  sql.execute( "CAll sp_summary_insert_by_user(${userId})" )
  println "Inserted summary"

  }

}
  println "Total of fixed user ${counter}"
sql.close()
