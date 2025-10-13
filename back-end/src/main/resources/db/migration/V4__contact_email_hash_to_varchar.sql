ALTER TABLE listing
  ALTER COLUMN contact_email_hash
  TYPE varchar(64)
  USING trim(contact_email_hash);
