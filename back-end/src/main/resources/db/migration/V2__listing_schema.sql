-- Use types that work well on PostgreSQL and MySQL. (JSONB -> JSON on MySQL.)
-- If you’re on MySQL, change JSONB to JSON and timestamptz to timestamp.

-- ===== Lookup tables =====
CREATE TABLE listing_source (
  id            BIGSERIAL PRIMARY KEY,
  code          VARCHAR(40) NOT NULL UNIQUE,   -- e.g. PARARIUS, HUURWONINGEN, R56
  label         VARCHAR(120) NOT NULL,
  is_active     BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE property_type (
  id            SMALLSERIAL PRIMARY KEY,
  code          VARCHAR(24) NOT NULL UNIQUE,   -- APARTMENT, HOUSE, STUDIO, ROOM
  label         VARCHAR(80) NOT NULL,
  is_active     BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE furnishing_type (
  id            SMALLSERIAL PRIMARY KEY,
  code          VARCHAR(24) NOT NULL UNIQUE,   -- FURNISHED, SEMI_FURNISHED, UNFURNISHED
  label         VARCHAR(80) NOT NULL,
  is_active     BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE rent_period (
  id            SMALLSERIAL PRIMARY KEY,
  code          VARCHAR(24) NOT NULL UNIQUE,   -- PER_MONTH, PER_WEEK
  label         VARCHAR(80) NOT NULL,
  is_active     BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE listing_status (
  id            SMALLSERIAL PRIMARY KEY,
  code          VARCHAR(24) NOT NULL UNIQUE,   -- ACTIVE, EXPIRED, REMOVED, UNKNOWN
  label         VARCHAR(80) NOT NULL,
  is_active     BOOLEAN NOT NULL DEFAULT TRUE
);

-- ===== Raw ingest layer =====
CREATE TABLE raw_listing (
  id            BIGSERIAL PRIMARY KEY,
  source_id     BIGINT NOT NULL REFERENCES listing_source(id),
  external_id   VARCHAR(128) NOT NULL,                -- id from the source site
  url           TEXT NOT NULL,
  fetched_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  payload_json  JSONB NOT NULL,                       -- exact scraped payload
  content_hash  CHAR(64) NOT NULL,                    -- e.g., SHA-256 of payload
  UNIQUE (source_id, external_id)
);

CREATE INDEX idx_raw_listing_fetched_at ON raw_listing(fetched_at DESC);
CREATE INDEX idx_raw_listing_source ON raw_listing(source_id);

-- ===== Canonical listing =====
CREATE TABLE listing (
  id                     BIGSERIAL PRIMARY KEY,
  source_id              BIGINT NOT NULL REFERENCES listing_source(id),
  external_id            VARCHAR(128) NOT NULL,
  canonical_url          TEXT,
  first_seen_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  last_seen_at           TIMESTAMPTZ NOT NULL DEFAULT NOW(),

  status_id              SMALLINT NOT NULL REFERENCES listing_status(id),

  title                  VARCHAR(300),
  description            TEXT,

  property_type_id       SMALLINT REFERENCES property_type(id),
  furnishing_type_id     SMALLINT REFERENCES furnishing_type(id),
  energy_label           VARCHAR(8),

  rent_amount            NUMERIC(12,2),
  rent_period_id         SMALLINT REFERENCES rent_period(id),
  deposit                NUMERIC(12,2),

  area_m2                NUMERIC(10,2),
  rooms                  NUMERIC(5,2),
  bedrooms               NUMERIC(5,2),
  bathrooms              NUMERIC(5,2),

  available_from         DATE,
  available_until        DATE,
  minimum_lease_months   INTEGER,

  country                VARCHAR(2),
  city                   VARCHAR(120),
  postal_code            VARCHAR(16),
  street                 VARCHAR(160),
  house_number           VARCHAR(32),
  unit                   VARCHAR(32),

  lat                    DOUBLE PRECISION,
  lon                    DOUBLE PRECISION,

  primary_photo_url      TEXT,
  photos_count           INTEGER,

  landlord_type          VARCHAR(16),                  -- AGENCY | PRIVATE (free text to start)
  contact_email_hash     CHAR(64),
  contact_phone_hash     CHAR(64),

  content_hash           CHAR(64) NOT NULL,           -- stable similarity/dedup key
  ingest_job_id          BIGINT,

  UNIQUE (source_id, external_id)
);

CREATE INDEX idx_listing_status ON listing(status_id);
CREATE INDEX idx_listing_city_status ON listing(city, status_id);
CREATE INDEX idx_listing_rent ON listing(rent_amount);
CREATE INDEX idx_listing_area ON listing(area_m2);
CREATE INDEX idx_listing_avail_from ON listing(available_from);
CREATE INDEX idx_listing_coords ON listing(lat, lon);

-- ===== Photos =====
CREATE TABLE listing_photo (
  id            BIGSERIAL PRIMARY KEY,
  listing_id    BIGINT NOT NULL REFERENCES listing(id) ON DELETE CASCADE,
  photo_url     TEXT NOT NULL,
  checksum      CHAR(64),
  position      SMALLINT,
  UNIQUE (listing_id, photo_url)
);

-- ===== Price history =====
CREATE TABLE listing_price_history (
  id            BIGSERIAL PRIMARY KEY,
  listing_id    BIGINT NOT NULL REFERENCES listing(id) ON DELETE CASCADE,
  observed_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  rent_amount   NUMERIC(12,2) NOT NULL
);

CREATE INDEX idx_price_hist_listing_time ON listing_price_history(listing_id, observed_at DESC);

-- ===== Optional: Ingest job lineage =====
CREATE TABLE ingest_job (
  id               BIGSERIAL PRIMARY KEY,
  source_id        BIGINT NOT NULL REFERENCES listing_source(id),
  started_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  finished_at      TIMESTAMPTZ,
  status           VARCHAR(24),           -- RUNNING | SUCCESS | ERROR
  items_fetched    INTEGER,
  items_processed  INTEGER,
  error_count      INTEGER
);
