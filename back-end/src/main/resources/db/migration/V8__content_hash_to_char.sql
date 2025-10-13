ALTER TABLE raw_listing
  ALTER COLUMN content_hash TYPE varchar(64) USING trim(content_hash);