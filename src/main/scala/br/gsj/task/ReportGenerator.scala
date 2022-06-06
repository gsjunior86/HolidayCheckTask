package br.gsj.task

import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}
import org.apache.spark.sql.functions._

object ReportGenerator extends App with ReportsDef{

  val spark = SparkSession.builder().master("local[*]").appName("ReportGenerator").getOrCreate()


  val bookingsDF = spark.read
    .format("jdbc")
    .option("url", "jdbc:postgresql://localhost:5432/hcdb")
    .option("dbtable", "taskdb.bookings")
    .option("user", "postgres")
    .option("password", "postgres")
    .load()

  val cancellationsDF = spark.read
    .format("jdbc")
    .option("url", "jdbc:postgresql://localhost:5432/hcdb")
    .option("dbtable", "taskdb.cancellations")
    .option("user", "postgres")
    .option("password", "postgres")
    .load()

  val year_report: Int = 2021

  generateCancellableBookingsReport(bookingsDF, cancellationsDF)
  generateBookingsPerDayReport(bookingsDF)
  generateMostPopularDestinationsReports(bookingsDF)
  generatePeakSeasonReport(bookingsDF)



}
