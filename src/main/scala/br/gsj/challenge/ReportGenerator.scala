package br.gsj.challenge

import org.apache.spark.sql.SparkSession

object ReportGenerator extends App with ReportsDef{

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

  val year_report: Int = 2021

  generateCancellableBookingsReport(bookingsDF, cancellationsDF)
  generateBookingsPerDayReport(bookingsDF)
  generateMostPopularDestinationsReports(bookingsDF)
  generatePeakSeasonReport(bookingsDF)
  generateUntilReport(cancellationsDF)



}
