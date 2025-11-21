-- ============================================
-- EXTRA SEED LISTINGS (CRAZY AMOUNT)
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
-- LISTING 4 — Pararius Apartment
-- ===========================
(
    (SELECT id FROM listing_source WHERE code='PARARIUS'),
    'P123457',
    'https://www.pararius.com/apartment-for-rent/eindhoven/P123457',
    NOW(), NOW(),
    (SELECT id FROM listing_status WHERE code='ACTIVE'),
    'Bright 1-bedroom apartment near Strijp-S',
    'Cozy and bright apartment within walking distance of Strijp-S and local amenities.',
    (SELECT id FROM property_type WHERE code='APARTMENT'),
    (SELECT id FROM furnishing_type WHERE code='FURNISHED'),
    'B',
    1350.00,
    (SELECT id FROM rent_period WHERE code='PER_MONTH'),
    2000.00,
    60.0,
    2, 1, 1,
    '2025-07-15', NULL, 12,
    'NL', 'Eindhoven', '5616TV', 'Klokgebouw', '100', NULL,
    51.4440, 5.4520,
    'https://images.example.com/pararius/p123457.jpg',
    10,
    (SELECT id FROM agency WHERE external_id = 'PARARIUS_AGENCY'),
    'HASH_P_123457',
    2001,
    TRUE
),

-- ===========================
-- LISTING 5 — Pararius Studio
-- ===========================
(
    (SELECT id FROM listing_source WHERE code='PARARIUS'),
    'P123458',
    'https://www.pararius.com/studio-for-rent/eindhoven/P123458',
    NOW(), NOW(),
    (SELECT id FROM listing_status WHERE code='ACTIVE'),
    'Compact studio near Central Station',
    'Well-maintained studio ideal for commuters, close to Eindhoven Central Station.',
    (SELECT id FROM property_type WHERE code='STUDIO'),
    (SELECT id FROM furnishing_type WHERE code='SEMI_FURNISHED'),
    'C',
    895.00,
    (SELECT id FROM rent_period WHERE code='PER_MONTH'),
    1200.00,
    26.0,
    1, 0.5, 1,
    '2025-06-20', NULL, 6,
    'NL', 'Eindhoven', '5611BA', 'Stationsweg', '12', 'Unit 2A',
    51.4412, 5.4795,
    'https://images.example.com/pararius/p123458.jpg',
    6,
    (SELECT id FROM agency WHERE external_id = 'PARARIUS_AGENCY'),
    'HASH_P_123458',
    2002,
    FALSE
),

-- ===========================
-- LISTING 6 — Pararius Loft
-- ===========================
(
    (SELECT id FROM listing_source WHERE code='PARARIUS'),
    'P123459',
    'https://www.pararius.com/loft-for-rent/eindhoven/P123459',
    NOW(), NOW(),
    (SELECT id FROM listing_status WHERE code='ACTIVE'),
    'Industrial loft in former factory',
    'Unique industrial loft in a converted factory building with high ceilings and large windows.',
    (SELECT id FROM property_type WHERE code='APARTMENT'),
    (SELECT id FROM furnishing_type WHERE code='FURNISHED'),
    'A',
    1750.00,
    (SELECT id FROM rent_period WHERE code='PER_MONTH'),
    2500.00,
    85.0,
    3, 1, 1,
    '2025-09-01', NULL, 12,
    'NL', 'Eindhoven', '5617AB', 'Torenallee', '20', 'Loft 4C',
    51.4435, 5.4560,
    'https://images.example.com/pararius/p123459.jpg',
    15,
    (SELECT id FROM agency WHERE external_id = 'PARARIUS_AGENCY'),
    'HASH_P_123459',
    2003,
    TRUE
),

-- ===========================
-- LISTING 7 — Pararius House
-- ===========================
(
    (SELECT id FROM listing_source WHERE code='PARARIUS'),
    'P123460',
    'https://www.pararius.com/house-for-rent/eindhoven/P123460',
    NOW(), NOW(),
    (SELECT id FROM listing_status WHERE code='ACTIVE'),
    'Corner house with spacious garden',
    'Family-friendly corner house with large garden and shed, located in a quiet residential area.',
    (SELECT id FROM property_type WHERE code='HOUSE'),
    (SELECT id FROM furnishing_type WHERE code='UNFURNISHED'),
    'D',
    1950.00,
    (SELECT id FROM rent_period WHERE code='PER_MONTH'),
    2500.00,
    120.0,
    5, 3, 1.5,
    '2025-08-15', NULL, 24,
    'NL', 'Eindhoven', '5624CG', 'Woenselsestraat', '210', NULL,
    51.4600, 5.4780,
    'https://images.example.com/pararius/p123460.jpg',
    18,
    (SELECT id FROM agency WHERE external_id = 'PARARIUS_AGENCY'),
    'HASH_P_123460',
    2004,
    TRUE
),

-- ===========================
-- LISTING 8 — Pararius Room
-- ===========================
(
    (SELECT id FROM listing_source WHERE code='PARARIUS'),
    'P123461',
    'https://www.pararius.com/room-for-rent/eindhoven/P123461',
    NOW(), NOW(),
    (SELECT id FROM listing_status WHERE code='ACTIVE'),
    'Student room near Fontys',
    'Affordable student room in a shared house, within cycling distance of Fontys campus.',
    (SELECT id FROM property_type WHERE code='ROOM'),
    (SELECT id FROM furnishing_type WHERE code='FURNISHED'),
    'E',
    575.00,
    (SELECT id FROM rent_period WHERE code='PER_MONTH'),
    800.00,
    14.0,
    1, 0.5, 0.5,
    '2025-06-10', NULL, 6,
    'NL', 'Eindhoven', '5627DA', 'Bredalaan', '45', 'Kamer 3',
    51.4590, 5.4460,
    'https://images.example.com/pararius/p123461.jpg',
    4,
    (SELECT id FROM agency WHERE external_id = 'PARARIUS_AGENCY'),
    'HASH_P_123461',
    2005,
    FALSE
),

-- ===========================
-- LISTING 9 — Huurwoningen Apartment
-- ===========================
(
    (SELECT id FROM listing_source WHERE code='HUURWONINGEN'),
    'HW98766',
    'https://www.huurwoningen.nl/appartement/eindhoven/98766/',
    NOW(), NOW(),
    (SELECT id FROM listing_status WHERE code='ACTIVE'),
    'Spacious 3-room apartment with balcony',
    'Renovated apartment with sunny balcony and separate storage room.',
    (SELECT id FROM property_type WHERE code='APARTMENT'),
    (SELECT id FROM furnishing_type WHERE code='SEMI_FURNISHED'),
    'B',
    1495.00,
    (SELECT id FROM rent_period WHERE code='PER_MONTH'),
    2000.00,
    78.0,
    3, 2, 1,
    '2025-07-01', NULL, 12,
    'NL', 'Eindhoven', '5613AA', 'Lichtstraat', '8', '3rd floor',
    51.4370, 5.4810,
    'https://images.example.com/huurwoningen/hw98766.jpg',
    9,
    (SELECT id FROM agency WHERE external_id = 'HUURWONINGEN_AGENCY'),
    'HASH_HW_98766',
    2006,
    TRUE
),

-- ===========================
-- LISTING 10 — Huurwoningen Studio
-- ===========================
(
    (SELECT id FROM listing_source WHERE code='HUURWONINGEN'),
    'HW98767',
    'https://www.huurwoningen.nl/studio/eindhoven/98767/',
    NOW(), NOW(),
    (SELECT id FROM listing_status WHERE code='ACTIVE'),
    'Studio in quiet residential street',
    'Compact studio with private bathroom, shared garden and bike storage.',
    (SELECT id FROM property_type WHERE code='STUDIO'),
    (SELECT id FROM furnishing_type WHERE code='UNFURNISHED'),
    'C',
    825.00,
    (SELECT id FROM rent_period WHERE code='PER_MONTH'),
    1000.00,
    24.0,
    1, 0.5, 1,
    '2025-06-25', NULL, 6,
    'NL', 'Eindhoven', '5643AB', 'Geldropseweg', '220', 'Rear',
    51.4280, 5.5020,
    'https://images.example.com/huurwoningen/hw98767.jpg',
    5,
    (SELECT id FROM agency WHERE external_id = 'HUURWONINGEN_AGENCY'),
    'HASH_HW_98767',
    2007,
    FALSE
),

-- ===========================
-- LISTING 11 — Huurwoningen House
-- ===========================
(
    (SELECT id FROM listing_source WHERE code='HUURWONINGEN'),
    'HW98768',
    'https://www.huurwoningen.nl/huurwoning/eindhoven/98768/',
    NOW(), NOW(),
    (SELECT id FROM listing_status WHERE code='ACTIVE'),
    'Semi-detached house with driveway',
    'Spacious semi-detached house with private driveway and garage, ideal for families.',
    (SELECT id FROM property_type WHERE code='HOUSE'),
    (SELECT id FROM furnishing_type WHERE code='UNFURNISHED'),
    'D',
    2050.00,
    (SELECT id FROM rent_period WHERE code='PER_MONTH'),
    2500.00,
    125.0,
    6, 4, 2,
    '2025-09-01', NULL, 24,
    'NL', 'Eindhoven', '5625AK', 'Amerikalaan', '5', NULL,
    51.4690, 5.4770,
    'https://images.example.com/huurwoningen/hw98768.jpg',
    16,
    (SELECT id FROM agency WHERE external_id = 'HUURWONINGEN_AGENCY'),
    'HASH_HW_98768',
    2008,
    TRUE
),

-- ===========================
-- LISTING 12 — Huurwoningen Room
-- ===========================
(
    (SELECT id FROM listing_source WHERE code='HUURWONINGEN'),
    'HW98769',
    'https://www.huurwoningen.nl/kamer/eindhoven/98769/',
    NOW(), NOW(),
    (SELECT id FROM listing_status WHERE code='ACTIVE'),
    'Room in shared apartment near TU/e',
    'Furnished room in modern shared apartment with balcony and shared kitchen.',
    (SELECT id FROM property_type WHERE code='ROOM'),
    (SELECT id FROM furnishing_type WHERE code='FURNISHED'),
    'E',
    650.00,
    (SELECT id FROM rent_period WHERE code='PER_MONTH'),
    800.00,
    16.0,
    1, 0.5, 1,
    '2025-06-30', NULL, 6,
    'NL', 'Eindhoven', '5612BT', 'Insulindelaan', '30', 'Room 2',
    51.4400, 5.4930,
    'https://images.example.com/huurwoningen/hw98769.jpg',
    4,
    (SELECT id FROM agency WHERE external_id = 'HUURWONINGEN_AGENCY'),
    'HASH_HW_98769',
    2009,
    FALSE
),

-- ===========================
-- LISTING 13 — Huurwoningen Maisonette
-- ===========================
(
    (SELECT id FROM listing_source WHERE code='HUURWONINGEN'),
    'HW98770',
    'https://www.huurwoningen.nl/maisonnette/eindhoven/98770/',
    NOW(), NOW(),
    (SELECT id FROM listing_status WHERE code='ACTIVE'),
    'Maisonette with roof terrace',
    'Two-level maisonette with private roof terrace and separate storage.',
    (SELECT id FROM property_type WHERE code='APARTMENT'),
    (SELECT id FROM furnishing_type WHERE code='SEMI_FURNISHED'),
    'B',
    1650.00,
    (SELECT id FROM rent_period WHERE code='PER_MONTH'),
    2200.00,
    90.0,
    4, 2, 1,
    '2025-08-01', NULL, 12,
    'NL', 'Eindhoven', '5615CL', 'Keizersgracht', '23', 'Unit 4',
    51.4385, 5.4725,
    'https://images.example.com/huurwoningen/hw98770.jpg',
    12,
    (SELECT id FROM agency WHERE external_id = 'HUURWONINGEN_AGENCY'),
    'HASH_HW_98770',
    2010,
    TRUE
),

-- ===========================
-- LISTING 14 — R56 Apartment
-- ===========================
(
    (SELECT id FROM listing_source WHERE code='R56'),
    'R56-002',
    'https://r56.example.com/apartment/eindhoven/002',
    NOW(), NOW(),
    (SELECT id FROM listing_status WHERE code='ACTIVE'),
    'City center apartment with canal view',
    'Modern apartment with canal view and elevator access, located in the heart of Eindhoven.',
    (SELECT id FROM property_type WHERE code='APARTMENT'),
    (SELECT id FROM furnishing_type WHERE code='FURNISHED'),
    'A',
    1850.00,
    (SELECT id FROM rent_period WHERE code='PER_MONTH'),
    2500.00,
    88.0,
    3, 2, 1,
    '2025-07-10', NULL, 12,
    'NL', 'Eindhoven', '5611EX', 'Dommelstraat', '5', '5th floor',
    51.4388, 5.4805,
    'https://images.example.com/r56/r56-002.jpg',
    14,
    (SELECT id FROM agency WHERE external_id = 'R56_AGENCY'),
    'HASH_R56_002',
    2011,
    FALSE
),

-- ===========================
-- LISTING 15 — R56 Penthouse
-- ===========================
(
    (SELECT id FROM listing_source WHERE code='R56'),
    'R56-003',
    'https://r56.example.com/penthouse/eindhoven/003',
    NOW(), NOW(),
    (SELECT id FROM listing_status WHERE code='ACTIVE'),
    'Luxury penthouse with rooftop terrace',
    'High-end penthouse with panoramic views of Eindhoven and a large private rooftop terrace.',
    (SELECT id FROM property_type WHERE code='APARTMENT'),
    (SELECT id FROM furnishing_type WHERE code='FURNISHED'),
    'A',
    2950.00,
    (SELECT id FROM rent_period WHERE code='PER_MONTH'),
    3500.00,
    130.0,
    4, 3, 2,
    '2025-09-15', NULL, 24,
    'NL', 'Eindhoven', '5611JM', 'Vestdijk', '150', 'Penthouse',
    51.4375, 5.4820,
    'https://images.example.com/r56/r56-003.jpg',
    20,
    (SELECT id FROM agency WHERE external_id = 'R56_AGENCY'),
    'HASH_R56_003',
    2012,
    TRUE
),

-- ===========================
-- LISTING 16 — R56 Townhouse
-- ===========================
(
    (SELECT id FROM listing_source WHERE code='R56'),
    'R56-004',
    'https://r56.example.com/house/eindhoven/004',
    NOW(), NOW(),
    (SELECT id FROM listing_status WHERE code='ACTIVE'),
    'Renovated townhouse with character',
    'Charming townhouse with original details, modern kitchen and small patio.',
    (SELECT id FROM property_type WHERE code='HOUSE'),
    (SELECT id FROM furnishing_type WHERE code='SEMI_FURNISHED'),
    'C',
    1850.00,
    (SELECT id FROM rent_period WHERE code='PER_MONTH'),
    2200.00,
    105.0,
    4, 3, 1.5,
    '2025-08-10', NULL, 12,
    'NL', 'Eindhoven', '5612CJ', 'Edisonstraat', '75', NULL,
    51.4520, 5.4755,
    'https://images.example.com/r56/r56-004.jpg',
    13,
    (SELECT id FROM agency WHERE external_id = 'R56_AGENCY'),
    'HASH_R56_004',
    2013,
    FALSE
),

-- ===========================
-- LISTING 17 — R56 Family House
-- ===========================
(
    (SELECT id FROM listing_source WHERE code='R56'),
    'R56-005',
    'https://r56.example.com/house/eindhoven/005',
    NOW(), NOW(),
    (SELECT id FROM listing_status WHERE code='ACTIVE'),
    'Detached family home with driveway',
    'Spacious detached home with multiple parking spaces and a large garden.',
    (SELECT id FROM property_type WHERE code='HOUSE'),
    (SELECT id FROM furnishing_type WHERE code='UNFURNISHED'),
    'B',
    2350.00,
    (SELECT id FROM rent_period WHERE code='PER_MONTH'),
    3000.00,
    150.0,
    6, 4, 2,
    '2025-10-01', NULL, 24,
    'NL', 'Eindhoven', '5629PJ', 'Oirschotsedijk', '10', NULL,
    51.4585, 5.3990,
    'https://images.example.com/r56/r56-005.jpg',
    18,
    (SELECT id FROM agency WHERE external_id = 'R56_AGENCY'),
    'HASH_R56_005',
    2014,
    TRUE
),

-- ===========================
-- LISTING 18 — R56 Apartment near High Tech Campus
-- ===========================
(
    (SELECT id FROM listing_source WHERE code='R56'),
    'R56-006',
    'https://r56.example.com/apartment/eindhoven/006',
    NOW(), NOW(),
    (SELECT id FROM listing_status WHERE code='ACTIVE'),
    'Apartment near High Tech Campus',
    'Modern apartment with private parking space, ideal for expats working at HTC.',
    (SELECT id FROM property_type WHERE code='APARTMENT'),
    (SELECT id FROM furnishing_type WHERE code='FURNISHED'),
    'A',
    1750.00,
    (SELECT id FROM rent_period WHERE code='PER_MONTH'),
    2500.00,
    82.0,
    3, 2, 1,
    '2025-07-20', NULL, 12,
    'NL', 'Eindhoven', '5656AE', 'Locht', '25', 'Unit 1.4',
    51.4125, 5.4600,
    'https://images.example.com/r56/r56-006.jpg',
    11,
    (SELECT id FROM agency WHERE external_id = 'R56_AGENCY'),
    'HASH_R56_006',
    2015,
    TRUE
),

-- ===========================
-- LISTING 19 — Pararius Apartment (Strijp)
-- ===========================
(
    (SELECT id FROM listing_source WHERE code='PARARIUS'),
    'P123462',
    'https://www.pararius.com/apartment-for-rent/eindhoven/P123462',
    NOW(), NOW(),
    (SELECT id FROM listing_status WHERE code='ACTIVE'),
    '2-bedroom apartment in Strijp',
    'Neatly finished apartment with balcony and storage room in popular Strijp district.',
    (SELECT id FROM property_type WHERE code='APARTMENT'),
    (SELECT id FROM furnishing_type WHERE code='SEMI_FURNISHED'),
    'B',
    1425.00,
    (SELECT id FROM rent_period WHERE code='PER_MONTH'),
    2000.00,
    70.0,
    3, 2, 1,
    '2025-07-05', NULL, 12,
    'NL', 'Eindhoven', '5616AK', 'Philips de Jonghstraat', '30', 'Unit 3B',
    51.4448, 5.4499,
    'https://images.example.com/pararius/p123462.jpg',
    9,
    (SELECT id FROM agency WHERE external_id = 'PARARIUS_AGENCY'),
    'HASH_P_123462',
    2016,
    FALSE
),

-- ===========================
-- LISTING 20 — Huurwoningen Studio (Gestel)
-- ===========================
(
    (SELECT id FROM listing_source WHERE code='HUURWONINGEN'),
    'HW98771',
    'https://www.huurwoningen.nl/studio/eindhoven/98771/',
    NOW(), NOW(),
    (SELECT id FROM listing_status WHERE code='ACTIVE'),
    'Studio in Gestel with private bathroom',
    'Quiet studio with private facilities, suitable for one person.',
    (SELECT id FROM property_type WHERE code='STUDIO'),
    (SELECT id FROM furnishing_type WHERE code='FURNISHED'),
    'C',
    825.00,
    (SELECT id FROM rent_period WHERE code='PER_MONTH'),
    1100.00,
    23.0,
    1, 0.5, 1,
    '2025-06-22', NULL, 6,
    'NL', 'Eindhoven', '5654LN', 'Fransebaan', '80', 'Rear house',
    51.4275, 5.4482,
    'https://images.example.com/huurwoningen/hw98771.jpg',
    5,
    (SELECT id FROM agency WHERE external_id = 'HUURWONINGEN_AGENCY'),
    'HASH_HW_98771',
    2017,
    TRUE
),

-- ===========================
-- LISTING 21 — R56 Apartment (Woensel)
-- ===========================
(
    (SELECT id FROM listing_source WHERE code='R56'),
    'R56-007',
    'https://r56.example.com/apartment/eindhoven/007',
    NOW(), NOW(),
    (SELECT id FROM listing_status WHERE code='ACTIVE'),
    '3-room apartment in Woensel',
    'Light apartment with modern kitchen and bathroom, located near WoensXL.',
    (SELECT id FROM property_type WHERE code='APARTMENT'),
    (SELECT id FROM furnishing_type WHERE code='SEMI_FURNISHED'),
    'D',
    1325.00,
    (SELECT id FROM rent_period WHERE code='PER_MONTH'),
    1800.00,
    68.0,
    3, 2, 1,
    '2025-07-25', NULL, 12,
    'NL', 'Eindhoven', '5627BR', 'Egaland', '15', '2nd floor',
    51.4710, 5.4760,
    'https://images.example.com/r56/r56-007.jpg',
    8,
    (SELECT id FROM agency WHERE external_id = 'R56_AGENCY'),
    'HASH_R56_007',
    2018,
    FALSE
),

-- ===========================
-- LISTING 22 — Pararius Rooftop Studio
-- ===========================
(
    (SELECT id FROM listing_source WHERE code='PARARIUS'),
    'P123463',
    'https://www.pararius.com/studio-for-rent/eindhoven/P123463',
    NOW(), NOW(),
    (SELECT id FROM listing_status WHERE code='ACTIVE'),
    'Rooftop studio with terrace',
    'Top-floor studio with private terrace and skyline views.',
    (SELECT id FROM property_type WHERE code='STUDIO'),
    (SELECT id FROM furnishing_type WHERE code='FURNISHED'),
    'B',
    950.00,
    (SELECT id FROM rent_period WHERE code='PER_MONTH'),
    1500.00,
    29.0,
    1, 0.5, 1,
    '2025-07-18', NULL, 6,
    'NL', 'Eindhoven', '5611EV', 'Jan van Lieshoutstraat', '10', 'Studio 5',
    51.4382, 5.4789,
    'https://images.example.com/pararius/p123463.jpg',
    7,
    (SELECT id FROM agency WHERE external_id = 'PARARIUS_AGENCY'),
    'HASH_P_123463',
    2019,
    TRUE
),

-- ===========================
-- LISTING 23 — Huurwoningen Ground-floor Apartment
-- ===========================
(
    (SELECT id FROM listing_source WHERE code='HUURWONINGEN'),
    'HW98772',
    'https://www.huurwoningen.nl/appartement/eindhoven/98772/',
    NOW(), NOW(),
    (SELECT id FROM listing_status WHERE code='ACTIVE'),
    'Ground-floor apartment with garden',
    'Nice ground-floor apartment with private garden and shed.',
    (SELECT id FROM property_type WHERE code='APARTMENT'),
    (SELECT id FROM furnishing_type WHERE code='UNFURNISHED'),
    'D',
    1395.00,
    (SELECT id FROM rent_period WHERE code='PER_MONTH'),
    2000.00,
    72.0,
    3, 2, 1,
    '2025-07-08', NULL, 12,
    'NL', 'Eindhoven', '5641BE', 'Heezerweg', '90', 'Bg',
    51.4260, 5.4960,
    'https://images.example.com/huurwoningen/hw98772.jpg',
    10,
    (SELECT id FROM agency WHERE external_id = 'HUURWONINGEN_AGENCY'),
    'HASH_HW_98772',
    2020,
    TRUE
),

-- ===========================
-- LISTING 24 — R56 Studio near PSV Stadium
-- ===========================
(
    (SELECT id FROM listing_source WHERE code='R56'),
    'R56-008',
    'https://r56.example.com/studio/eindhoven/008',
    NOW(), NOW(),
    (SELECT id FROM listing_status WHERE code='ACTIVE'),
    'Studio near PSV Stadium',
    'Studio with separate sleeping area, close to city center and PSV stadium.',
    (SELECT id FROM property_type WHERE code='STUDIO'),
    (SELECT id FROM furnishing_type WHERE code='FURNISHED'),
    'C',
    900.00,
    (SELECT id FROM rent_period WHERE code='PER_MONTH'),
    1300.00,
    27.0,
    1, 0.5, 1,
    '2025-06-28', NULL, 6,
    'NL', 'Eindhoven', '5616NH', 'Frederiklaan', '60', 'Studio 2',
    51.4410, 5.4620,
    'https://images.example.com/r56/r56-008.jpg',
    6,
    (SELECT id FROM agency WHERE external_id = 'R56_AGENCY'),
    'HASH_R56_008',
    2021,
    FALSE
),

-- ===========================
-- LISTING 25 — Pararius Family House (Meerhoven)
-- ===========================
(
    (SELECT id FROM listing_source WHERE code='PARARIUS'),
    'P123464',
    'https://www.pararius.com/house-for-rent/eindhoven/P123464',
    NOW(), NOW(),
    (SELECT id FROM listing_status WHERE code='ACTIVE'),
    'Family house in Meerhoven with driveway',
    'Modern family house with energy-efficient label and low monthly costs.',
    (SELECT id FROM property_type WHERE code='HOUSE'),
    (SELECT id FROM furnishing_type WHERE code='UNFURNISHED'),
    'A',
    2250.00,
    (SELECT id FROM rent_period WHERE code='PER_MONTH'),
    2800.00,
    135.0,
    5, 4, 2,
    '2025-08-05', NULL, 24,
    'NL', 'Eindhoven', '5657EK', 'Zandkasteel', '12', NULL,
    51.4515, 5.3920,
    'https://images.example.com/pararius/p123464.jpg',
    17,
    (SELECT id FROM agency WHERE external_id = 'PARARIUS_AGENCY'),
    'HASH_P_123464',
    2022,
    TRUE
),

-- ===========================
-- LISTING 26 — Huurwoningen Room (Shared House)
-- ===========================
(
    (SELECT id FROM listing_source WHERE code='HUURWONINGEN'),
    'HW98773',
    'https://www.huurwoningen.nl/kamer/eindhoven/98773/',
    NOW(), NOW(),
    (SELECT id FROM listing_status WHERE code='ACTIVE'),
    'Room in shared house near city center',
    'Shared house with international students, common living room and kitchen.',
    (SELECT id FROM property_type WHERE code='ROOM'),
    (SELECT id FROM furnishing_type WHERE code='FURNISHED'),
    'E',
    625.00,
    (SELECT id FROM rent_period WHERE code='PER_MONTH'),
    800.00,
    15.0,
    1, 0.5, 0.5,
    '2025-06-18', NULL, 6,
    'NL', 'Eindhoven', '5611NK', 'Grote Berg', '44', 'Kamer 1',
    51.4370, 5.4705,
    'https://images.example.com/huurwoningen/hw98773.jpg',
    3,
    (SELECT id FROM agency WHERE external_id = 'HUURWONINGEN_AGENCY'),
    'HASH_HW_98773',
    2023,
    FALSE
),

-- ===========================
-- LISTING 27 — R56 Ground-floor Apartment
-- ===========================
(
    (SELECT id FROM listing_source WHERE code='R56'),
    'R56-009',
    'https://r56.example.com/appartement/eindhoven/009',
    NOW(), NOW(),
    (SELECT id FROM listing_status WHERE code='ACTIVE'),
    'Ground-floor apartment with patio',
    'Nicely finished apartment with small private patio and modern kitchen.',
    (SELECT id FROM property_type WHERE code='APARTMENT'),
    (SELECT id FROM furnishing_type WHERE code='SEMI_FURNISHED'),
    'C',
    1390.00,
    (SELECT id FROM rent_period WHERE code='PER_MONTH'),
    1900.00,
    69.0,
    3, 2, 1,
    '2025-07-30', NULL, 12,
    'NL', 'Eindhoven', '5644RL', 'Roostenlaan', '200', 'Bg',
    51.4175, 5.4955,
    'https://images.example.com/r56/r56-009.jpg',
    8,
    (SELECT id FROM agency WHERE external_id = 'R56_AGENCY'),
    'HASH_R56_009',
    2024,
    TRUE
),

-- ===========================
-- LISTING 28 — Pararius Studio (Near TU/e)
-- ===========================
(
    (SELECT id FROM listing_source WHERE code='PARARIUS'),
    'P123465',
    'https://www.pararius.com/studio-for-rent/eindhoven/P123465',
    NOW(), NOW(),
    (SELECT id FROM listing_status WHERE code='ACTIVE'),
    'Modern studio near TU/e & Fontys',
    'Efficiently laid out studio with private bathroom and pantry kitchen.',
    (SELECT id FROM property_type WHERE code='STUDIO'),
    (SELECT id FROM furnishing_type WHERE code='FURNISHED'),
    'A',
    925.00,
    (SELECT id FROM rent_period WHERE code='PER_MONTH'),
    1300.00,
    25.0,
    1, 0.5, 1,
    '2025-06-26', NULL, 6,
    'NL', 'Eindhoven', '5612AR', 'Onze Lieve Vrouwestraat', '5', 'Studio B',
    51.4445, 5.4920,
    'https://images.example.com/pararius/p123465.jpg',
    5,
    (SELECT id FROM agency WHERE external_id = 'PARARIUS_AGENCY'),
    'HASH_P_123465',
    2025,
    TRUE
),

-- ===========================
-- LISTING 29 — Huurwoningen Apartment (Green Area)
-- ===========================
(
    (SELECT id FROM listing_source WHERE code='HUURWONINGEN'),
    'HW98774',
    'https://www.huurwoningen.nl/appartement/eindhoven/98774/',
    NOW(), NOW(),
    (SELECT id FROM listing_status WHERE code='ACTIVE'),
    'Apartment overlooking green park',
    'Light apartment with view on a green park, close to shops and public transport.',
    (SELECT id FROM property_type WHERE code='APARTMENT'),
    (SELECT id FROM furnishing_type WHERE code='SEMI_FURNISHED'),
    'B',
    1475.00,
    (SELECT id FROM rent_period WHERE code='PER_MONTH'),
    2100.00,
    76.0,
    3, 2, 1,
    '2025-07-12', NULL, 12,
    'NL', 'Eindhoven', '5629PH', 'Dr. Berlagelaan', '60', '3rd floor',
    51.4582, 5.4120,
    'https://images.example.com/huurwoningen/hw98774.jpg',
    9,
    (SELECT id FROM agency WHERE external_id = 'HUURWONINGEN_AGENCY'),
    'HASH_HW_98774',
    2026,
    TRUE
),

-- ===========================
-- LISTING 30 — R56 Apartment (Compact)
-- ===========================
(
    (SELECT id FROM listing_source WHERE code='R56'),
    'R56-010',
    'https://r56.example.com/apartment/eindhoven/010',
    NOW(), NOW(),
    (SELECT id FROM listing_status WHERE code='ACTIVE'),
    'Compact 1-bedroom apartment',
    'Modern compact apartment, ideal for a single person or couple.',
    (SELECT id FROM property_type WHERE code='APARTMENT'),
    (SELECT id FROM furnishing_type WHERE code='UNFURNISHED'),
    'C',
    1195.00,
    (SELECT id FROM rent_period WHERE code='PER_MONTH'),
    1700.00,
    55.0,
    2, 1, 1,
    '2025-07-03', NULL, 12,
    'NL', 'Eindhoven', '5613LC', 'Hoogstraat', '155', '2nd floor',
    51.4312, 5.4650,
    'https://images.example.com/r56/r56-010.jpg',
    7,
    (SELECT id FROM agency WHERE external_id = 'R56_AGENCY'),
    'HASH_R56_010',
    2027,
    FALSE
);
