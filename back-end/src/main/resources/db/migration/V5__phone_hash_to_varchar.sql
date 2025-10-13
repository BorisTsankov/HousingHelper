ALTER TABLE listing
  ALTER COLUMN contact_email_hash TYPE varchar(64) USING trim(contact_email_hash),
  ALTER COLUMN contact_phone_hash TYPE varchar(64) USING trim(contact_phone_hash);
