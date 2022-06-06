package br.gsj.task

import org.apache.spark.sql.{DataFrame, SaveMode}
import org.apache.spark.sql.functions._

trait ReportsDef {

  val reportsSchema = sys.env.getOrElse("REPORT_SCHEMA", throw new NoSuchElementException ("report_schema must be set"))


  /**
   *  How many bookings are cancellable
   *
   * @param bookingsDF
   * @param cancellationsDF
   * @param year_filter
   */
  def generateCancellableBookingsReport(bookingsDF : DataFrame,cancellationsDF: DataFrame)= {
    bookingsDF.join(cancellationsDF, bookingsDF("booking_id") === cancellationsDF("booking_id"))
      .where(col("cancellation_type") === 52)
      .groupBy(col("cancellation_type"))
      .agg(count("cancellation_type").as("cancellation_count"))
      .write
      .format("jdbc")
      .mode(SaveMode.Overwrite)
      .option("url", "jdbc:postgresql://localhost:5432/hcdb")
      .option("dbtable", s"${reportsSchema}.cancellable_bookings")
      .option("user", "postgres")
      .option("password", "postgres")
      .save


  }


  /**
   *   Number of bookings per day
   * @param bookingsDF
   * @param yearFilter
   */
  def generateBookingsPerDayReport(bookingsDF : DataFrame) = {
    bookingsDF
      .withColumn("day_year_booking", to_date(date_trunc("day",col("booking_date"))) )
      .groupBy("day_year_booking")
      .agg(count("day_year_booking").as("booking_per_day"))
      .orderBy("day_year_booking")
      .write
      .format("jdbc")
      .mode(SaveMode.Overwrite)
      .option("url", "jdbc:postgresql://localhost:5432/hcdb")
      .option("dbtable", s"${reportsSchema}.bookings_per_day")
      .option("user", "postgres")
      .option("password", "postgres")
      .save

  }

  /**
   * Most Popular Destinations
   * @param bookingsDF
   */

  def generateMostPopularDestinationsReports(bookingsDF : DataFrame) ={
    bookingsDF
      .groupBy(col("destination"))
      .agg(count("destination").as("count_destination"))
      .orderBy(col("count_destination").desc)
      .write
      .format("jdbc")
      .mode(SaveMode.Overwrite)
      .option("url", "jdbc:postgresql://localhost:5432/hcdb")
      .option("dbtable", s"${reportsSchema}.popular_destinations")
      .option("user", "postgres")
      .option("password", "postgres")
      .save
  }

  /**
   * Peak Travel Season
   * @param bookingsDF
   */

  def generatePeakSeasonReport(bookingsDF : DataFrame) = {

    bookingsDF
      .withColumn("year_departure", year(col("departure_date")))
      .withColumn("month_departure", month(col("departure_date")))
      .groupBy("month_departure","year_departure")
      .agg(count("month_departure").as("count_departures"))
      .write
      .format("jdbc")
      .mode(SaveMode.Overwrite)
      .option("url", "jdbc:postgresql://localhost:5432/hcdb")
      .option("dbtable", s"${reportsSchema}.peak_travel_season")
      .option("user", "postgres")
      .option("password", "postgres")
      .save



  }

}
