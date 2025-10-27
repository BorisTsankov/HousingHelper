ALTER TABLE agency
  ALTER COLUMN country TYPE varchar(2) USING trim(country);