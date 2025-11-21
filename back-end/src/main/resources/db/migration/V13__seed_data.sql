-- ============================================
-- SEED AGENCIES
-- ============================================
INSERT INTO agency (
    source_id, external_id, name, website_url, logo_url,
    country, city, postal_code, street, house_number, unit,
    contact_email_hash, contact_phone_hash
) VALUES
-- Pararius Agency
(
    (SELECT id FROM listing_source WHERE code = 'PARARIUS'),
    'PARARIUS_AGENCY',
    'Pararius Rentals',
    'https://www.pararius.com',
    NULL,
    'NL', 'Eindhoven', '5612AB', 'Strijpseweg', '14A', NULL,
    NULL, NULL
),

-- Huurwoningen.nl Agency
(
    (SELECT id FROM listing_source WHERE code = 'HUURWONINGEN'),
    'HUURWONINGEN_AGENCY',
    'Huurwoningen.nl',
    'https://www.huurwoningen.nl',
    NULL,
    'NL', 'Eindhoven', '5611AX', 'Kerkstraat', '52', NULL,
    NULL, NULL
),

-- R56 Agency
(
    (SELECT id FROM listing_source WHERE code = 'R56'),
    'R56_AGENCY',
    'R56 Makelaars',
    'https://r56.example.com',
    NULL,
    'NL', 'Eindhoven', '5641BB', 'Boschdijk', '301', NULL,
    NULL, NULL
);

-- ============================================
-- SEED LISTINGS
-- ============================================

INSERT INTO listing (
    source_id, external_id, canonical_url,
    first_seen_at, last_seen_at, status_id,
    title, description, property_type_id, furnishing_type_id,
    energy_label, rent_amount, rent_period_id, deposit,
    area_m2, rooms, bedrooms, bathrooms,
    available_from, available_until, minimum_lease_months,
    country, city, postal_code, street, house_number, unit,
    lat, lon,
    primary_photo_url, photos_count,
    agency_id, content_hash, ingest_job_id, pets_allowed
)
VALUES
-- ===========================
-- LISTING 1 — Pararius Apartment
-- ===========================
(
    (SELECT id FROM listing_source WHERE code='PARARIUS'),
    'P123456',
    'https://www.pararius.com/apartment-for-rent/eindhoven/P123456',
    NOW(), NOW(),
    (SELECT id FROM listing_status WHERE code='ACTIVE'),
    'Modern 2-bedroom apartment in Eindhoven',
    'A spacious and modern apartment located near TU/e.',
    (SELECT id FROM property_type WHERE code='APARTMENT'),
    (SELECT id FROM furnishing_type WHERE code='FURNISHED'),
    'A',
    1450.00,
    (SELECT id FROM rent_period WHERE code='PER_MONTH'),
    2000.00,
    72.5,
    3, 2, 1,
    '2025-07-01', NULL, 12,
    'NL', 'Eindhoven', '5612AB', 'Strijpseweg', '14A', NULL,
    51.4416, 5.4697,
    'https://images.example.com/pararius/p123456.jpg',
    12,
    (SELECT id FROM agency WHERE external_id = 'PARARIUS_AGENCY'),
    'HASH_123_ABC',
    1001,
    TRUE
),

-- ===========================
-- LISTING 2 — Huurwoningen.nl Studio
-- ===========================
(
    (SELECT id FROM listing_source WHERE code='HUURWONINGEN'),
    'HW98765',
    'https://www.huurwoningen.nl/studio/eindhoven/98765/',
    NOW(), NOW(),
    (SELECT id FROM listing_status WHERE code='ACTIVE'),
    'Cozy studio near city center',
    'Perfect studio for a student or young professional.',
    (SELECT id FROM property_type WHERE code='STUDIO'),
    (SELECT id FROM furnishing_type WHERE code='SEMI_FURNISHED'),
    'B',
    875.00,
    (SELECT id FROM rent_period WHERE code='PER_MONTH'),
    1000.00,
    28.0,
    1, 0.5, 1,
    '2025-06-15', NULL, 6,
    'NL', 'Eindhoven', '5611AX', 'Kerkstraat', '52', 'Unit 3B',
    51.4380, 5.4750,
    'https://images.example.com/huurwoningen/hw98765.jpg',
    5,
    (SELECT id FROM agency WHERE external_id = 'HUURWONINGEN_AGENCY'),
    'HASH_987_XYZ',
    1002,
    FALSE
),

-- ===========================
-- LISTING 3 — R56 House
-- ===========================
(
    (SELECT id FROM listing_source WHERE code='R56'),
    'R56-001',
    'https://r56.example.com/house/eindhoven/001',
    NOW(), NOW(),
    (SELECT id FROM listing_status WHERE code='ACTIVE'),
    'Family house with garden',
    'Large family home with private garden and parking.',
    (SELECT id FROM property_type WHERE code='HOUSE'),
    (SELECT id FROM furnishing_type WHERE code='UNFURNISHED'),
    'C',
    2100.00,
    (SELECT id FROM rent_period WHERE code='PER_MONTH'),
    2500.00,
    110.0,
    5, 3, 2,
    '2025-08-01', NULL, 24,
    'NL', 'Eindhoven', '5641BB', 'Boschdijk', '301', NULL,
    51.4550, 5.4660,
    'https://images.example.com/r56/r56-001.jpg',
    20,
    (SELECT id FROM agency WHERE external_id = 'R56_AGENCY'),
    'HASH_R56_001',
    1003,
    TRUE
);
