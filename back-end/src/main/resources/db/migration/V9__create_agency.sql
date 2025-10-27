CREATE TABLE agency (
    id BIGSERIAL PRIMARY KEY,
    source_id BIGINT NOT NULL REFERENCES listing_source(id),
    external_id VARCHAR(128) NOT NULL,
    name VARCHAR(200) NOT NULL,
    website_url VARCHAR(512) ,
    logo_url TEXT,
    country CHAR(2),
    city VARCHAR(120),
    postal_code VARCHAR(16),
    street VARCHAR(160),
    house_number VARCHAR(32),
    unit VARCHAR(32),
    contact_email_hash VARCHAR(64),
    contact_phone_hash VARCHAR(64)
);

ALTER TABLE agency
  ADD CONSTRAINT uq_agency_source_external UNIQUE (source_id, external_id);

ALTER TABLE listing
  ADD COLUMN agency_id BIGINT NULL REFERENCES agency(id);

INSERT INTO agency (source_id, external_id, name, website_url, logo_url, country, city, postal_code, street, house_number, unit, contact_email_hash, contact_phone_hash)
SELECT DISTINCT
    l.source_id,
    COALESCE(l.contact_email_hash, l.contact_phone_hash, l.external_id) AS external_id,  -- fallback if no agency external id available
    COALESCE(NULLIF(l.title, ''), 'Unknown Agency') AS name,     -- adjust if you already store agency name elsewhere
    NULL, NULL,
    l.country, l.city, l.postal_code, l.street, l.house_number, l.unit,
    l.contact_email_hash, l.contact_phone_hash
FROM listing l
WHERE l.landlord_type = 'AGENCY';


UPDATE listing l
SET agency_id = a.id
FROM agency a
WHERE l.landlord_type = 'AGENCY'
  AND l.source_id = a.source_id
  AND (
        (l.contact_email_hash IS NOT NULL AND l.contact_email_hash = a.contact_email_hash)
     OR (l.contact_phone_hash IS NOT NULL AND l.contact_phone_hash = a.contact_phone_hash)
     OR (a.external_id = l.external_id)
  );

ALTER TABLE listing DROP COLUMN landlord_type;
ALTER TABLE listing DROP COLUMN contact_email_hash;
ALTER TABLE listing DROP COLUMN contact_phone_hash;
