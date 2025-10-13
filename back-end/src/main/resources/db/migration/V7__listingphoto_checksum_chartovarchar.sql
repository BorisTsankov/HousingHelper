ALTER TABLE listing_photo
  ALTER COLUMN checksum TYPE varchar(64) USING trim(checksum);