CREATE DATABASE hcdb;


\connect hcdb;


CREATE SCHEMA data;

CREATE SCHEMA reports;

-- Creation of product table
CREATE TABLE IF NOT EXISTS data.bookings (
  booking_id INT NOT NULL,
  booking_date TIMESTAMP NOT NULL,
  arrival_date TIMESTAMP NOT NULL,
  departure_date TIMESTAMP NOT NULL,
  "source" character(3) not null,
  destination character(3) not null
);


CREATE TABLE IF NOT EXISTS data.cancellations (
  booking_id INT NOT NULL,
  cancellation_type INT NOT NULL,
  enddate TIMESTAMP
);

COPY data.bookings (booking_id, booking_date, arrival_date, departure_date, "source", destination) FROM '/var/lib/postgresql/data_csv/bookings.csv' DELIMITER ',' CSV HEADER;

COPY data.cancellations (booking_id, cancellation_type, enddate) FROM '/var/lib/postgresql/data_csv/cancellation.csv' DELIMITER ',' CSV HEADER;
