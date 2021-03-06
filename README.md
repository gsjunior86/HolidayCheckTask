# HolidayCheck Challenge

## Run Instructions
Before running, please make sure that you have **git**, **Docker** and **docker-compose** installed.

Then, clone and access the project folder with with:
```
git clone https://github.com/gsjunior86/HolidayCheckTask
cd HolidayCheckTask/
```

and run with:
```
docker-compose up
```

Wait for the **report-generator** container to finish and access the reports through the dashboard available at:
```
http://localhost:3000/public/dashboard/bb1f855c-2c0c-451d-91c6-d2efc3b2f24f
```

## Build Instructions
In case you want to build locally make sure that you have installed:

- **scala build tool (sbt)** (https://www.scala-sbt.org/)
- **JDK 11 or 8**
- **make**


## Pipeline Description
<img src="https://github.com/gsjunior86/HolidayCheckTask/blob/main/img/holidaycheck.png" align="center" height="550" width="894" >

The entry point to the pipeline is the creation of table and load of the CSV files into a postgresql database instance.
When the database is ready, the spark local container will be executed, with the following parameters:
- DB_HOST: <data_base_address>:<data_base_port>
- DB_USER: database user
- DB_PASSWD: database password
- DATA_SCHEMA: the schema where the data is located
- DB_SOURCE: the data base where the data is located
- REPORT_SCHEMA: the schema where the reports will be saved
- DRIVER_CLASS: the JDBC Driver class

After all the tables were created and data loaded, the spark Job will read the tables from the database and schema that were informed, storing the processed data as tables inside the **REPORT_SCHEMA**.
When the processing is done, the Metabase instance will read the processed tables and generate the dashboards.
Metabase data is stored by it's internal H2 database, stored at /metabase-data and unfortunately, the configuration is not parametrized.

<img src="https://github.com/gsjunior86/HolidayCheckTask/blob/main/img/dashboard.png" align="center" height="432" width="768" >



# Programming Assignment

We want to get a better understanding of the bookings that happen on our website. Specifically, how many bookings are cancellable, number of bookings per day, what are the popular destinations and what is the peak travel season.
The data is available at a central location (assume any file system or database) in csv format and is updated periodically. You need to do the following tasks:

1. Design and build a data pipeline that will save and process this data to be able to answer the above questions. 
2. Use the data to build a report that shows number of bookings per day. 
	The output of the report should have two columns:
		a. date
		b. num_bookings
3. Create a report to show which bookings are free cancellable and which bookings are cancellable with a fee (cheap_cancellable) and until when. You are free to decide the structure of the report as you feel necessary

## Data

1. bookings.csv : Data about bookings. eg. booking_id, booking_date, destination, source, travel_date, arrival_date
2. cancellation.csv : Information about which bookings are cancellable and till when. Columns : booking_id, cancellation_id (52 for free and 53 for cheap), enddate (till what date is the booking cancellable)

You are free to decide the storage location and format of the final report. Make assumptions wherever necessary. Please use **Scala** language and **Spark** framework (in local mode).

## What do we look for?

1. The code that is scalable, easy to maintain and extensible. We want to keep as minimum manual work as possible
2. Tests wherever necessary
3. Readme to explain your assumptions, solution and how to run it
4. Bonus : Architecture diagram	
