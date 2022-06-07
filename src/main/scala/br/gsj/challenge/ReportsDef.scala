package br.gsj.challenge


import org.apache.spark.sql.{DataFrame, SaveMode}
import org.apache.spark.sql.functions._

trait ReportsDef {

  val allowedSaveModes = Map("1"-> "Overwrite", "2" -> "Append")

  val reportsSchema = sys.env.getOrElse("REPORT_SCHEMA", throw new NoSuchElementException ("REPORT_SCHEMA must be set"))
  val dataSchema = sys.env.getOrElse("DATA_SCHEMA", throw new NoSuchElementException ("DATA_SCHEMA must be set"))
  val sourceDB = sys.env.getOrElse("DB_SOURCE", throw new NoSuchElementException ("DATA_SOURCE must be set"))
  val dbUSER = sys.env.getOrElse("DB_USER", throw new NoSuchElementException ("DB_USER must be set"))
  val dbPASSWD = sys.env.getOrElse("DB_PASSWD", throw new NoSuchElementException ("DB_PASSWD must be set"))
  val postgresHost = sys.env.getOrElse("POSTGRES_HOST", throw new NoSuchElementException ("POSTGRES_HOST must be set"))
  var saveMode = sys.env.getOrElse("SAVE_MODE", throw new NoSuchElementException ("SAVE_MODE must be set"))


  saveMode =  allowedSaveModes.getOrElse(saveMode, throw new NoSuchElementException ("Save mode should be 1=Overwrite or 2=Append"))

  val driver = "org.postgresql.Driver"


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
      .withColumn("year", year(col("booking_date")))
      .groupBy(col("cancellation_type"),col("year"))
      .agg(count("cancellation_type").as("cancellation_count"))
      .write
      .format("jdbc")
      .mode(saveMode)
      .option("url", s"jdbc:postgresql://${postgresHost}/${sourceDB}")
      .option("dbtable", s"${reportsSchema}.cancellable_bookings")
      .option("user", dbUSER)
      .option("password", dbPASSWD)
      .option("driver", driver)
      .save


  }

  /**
   * which bookings are free cancellable and which bookings are cancellable with a fee (cheap_cancellable) and until when
   *
   * @param cancellationsDF
   */
  def generateUntilReport(cancellationsDF: DataFrame) = {
    println(cancellationsDF.count())

    cancellationsDF.where(
      col("cancellation_type") === 52
      || col("cancellation_type") === 53)
      .where(col("enddate").isNotNull)
      .withColumn("enddate", to_date(date_trunc("day",col("enddate"))))
      .withColumn("cancellation_type", when(col("cancellation_type") === 52,"free")
        .when(col("cancellation_type") === 53,"cheap fee"))
      .orderBy("enddate")
      .write
      .format("jdbc")
      .mode(saveMode)
      .option("url", s"jdbc:postgresql://${postgresHost}/${sourceDB}")
      .option("dbtable", s"${reportsSchema}.free_and_fee_cancellable")
      .option("user", dbUSER)
      .option("password", dbPASSWD)
      .option("driver", driver)
      .save



  }


  /**
   *   Number of bookings per day
   * @param bookingsDF
   * @param yearFilter
   */
  def generateBookingsPerDayReport(bookingsDF : DataFrame) = {
    bookingsDF
      .withColumn("date", to_date(date_trunc("day",col("booking_date"))) )
      .groupBy("date")
      .agg(count("date").as("num_booking"))
      .orderBy("date")
      .write
      .format("jdbc")
      .mode(saveMode)
      .option("url", s"jdbc:postgresql://${postgresHost}/${sourceDB}")
      .option("dbtable", s"${reportsSchema}.bookings_per_day")
      .option("user", dbUSER)
      .option("password", dbPASSWD)
      .option("driver", driver)
      .save

  }

  /**
   * Most Popular Destinations
   * @param bookingsDF
   */

  def generateMostPopularDestinationsReports(bookingsDF : DataFrame) ={
    bookingsDF
      .withColumn("year_travel",year(col("departure_date")))
      .groupBy(col("destination"),col("year_travel"))
      .agg(count("destination").as("count_destination"))
      .orderBy(col("count_destination").desc)
      .write
      .format("jdbc")
      .mode(saveMode)
      .option("url", s"jdbc:postgresql://${postgresHost}/${sourceDB}")
      .option("dbtable", s"${reportsSchema}.popular_destinations")
      .option("user", dbUSER)
      .option("password", dbPASSWD)
      .option("driver", driver)
      .save
  }

  /**
   * Peak Travel Season
   * @param bookingsDF
   */

  def generatePeakSeasonReport(bookingsDF : DataFrame) = {

    bookingsDF
      .withColumn("year_departure", year(col("departure_date")))
      .withColumn("month_departure", date_format(col("departure_date"),"LLLL"))
      .groupBy("month_departure","year_departure")
      .agg(count("month_departure").as("count_departures"))
      .write
      .format("jdbc")
      .mode(saveMode)
      .option("url", s"jdbc:postgresql://${postgresHost}/${sourceDB}")
      .option("dbtable", s"${reportsSchema}.peak_travel_season")
      .option("user", dbUSER)
      .option("password", dbPASSWD)
      .option("driver", driver)
      .save



  }

}
