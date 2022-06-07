package br.gsj.challenge

import br.gsj.challenge.ReportGenerator.{bookingsDF, cancellationsDF, generateBookingsPerDayReport, generateCancellableBookingsReport, generateMostPopularDestinationsReports, generatePeakSeasonReport, generateUntilReport}
import org.apache.spark.sql.SparkSession

object ReportGenerator extends App with ReportGenerator {

  val spark = SparkSession.builder()
    //.master("local[*]")
    .appName("ReportGenerator").getOrCreate()


  val bookingsDF = spark.read
    .format("jdbc")
    .option("url", s"jdbc:postgresql://${postgresHost}/${sourceDB}")
    .option("dbtable", s"${dataSchema}.bookings")
    .option("user", dbUSER)
    .option("password", dbPASSWD)
    .option("driver", driver)
    .load()

  val cancellationsDF = spark.read
    .format("jdbc")
    .option("url", s"jdbc:postgresql://${postgresHost}/${sourceDB}")
    .option("dbtable", s"${dataSchema}.cancellations")
    .option("user", dbUSER)
    .option("password", dbPASSWD)
    .option("driver", driver)
    .load()

  generateReports()

}

trait ReportGenerator extends ReportsDef{

  def generateReports() = {
    generateCancellableBookingsReport(bookingsDF, cancellationsDF)
    generateBookingsPerDayReport(bookingsDF)
    generateMostPopularDestinationsReports(bookingsDF)
    generatePeakSeasonReport(bookingsDF)
    generateUntilReport(cancellationsDF)
  }

}
