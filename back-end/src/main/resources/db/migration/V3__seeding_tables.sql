-- Sources
INSERT INTO listing_source (code, label) VALUES
  ('PARARIUS','Pararius'),
  ('HUURWONINGEN','Huurwoningen.nl'),
  ('R56','R56');

-- Property types
INSERT INTO property_type (code, label) VALUES
  ('APARTMENT','Apartment'),
  ('HOUSE','House'),
  ('STUDIO','Studio'),
  ('ROOM','Room');

-- Furnishing
INSERT INTO furnishing_type (code, label) VALUES
  ('FURNISHED','Furnished'),
  ('SEMI_FURNISHED','Semi-furnished'),
  ('UNFURNISHED','Unfurnished');

-- Rent period
INSERT INTO rent_period (code, label) VALUES
  ('PER_MONTH','Per month'),
  ('PER_WEEK','Per week');

-- Status
INSERT INTO listing_status (code, label) VALUES
  ('ACTIVE','Active'),
  ('EXPIRED','Expired'),
  ('REMOVED','Removed'),
  ('UNKNOWN','Unknown');
