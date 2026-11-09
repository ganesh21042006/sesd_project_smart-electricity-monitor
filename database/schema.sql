CREATE DATABASE IF NOT EXISTS electricity_monitor;
USE electricity_monitor;

-- Spring Data JPA creates/updates meter_readings automatically.
-- This script is included for database setup. The first meter reading
-- is a baseline; later readings calculate the difference from the prior reading.
