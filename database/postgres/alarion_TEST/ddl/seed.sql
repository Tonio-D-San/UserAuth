INSERT INTO acquisition_sources (code, name, description)
VALUES
    ('PURCHASED', 'Purchased', 'Ability acquired spending points'),
    ('GRANTED',   'Granted',   'Ability granted by training/origin'),
    ('ADMIN',     'Admin',     'Ability assigned by administrators')
ON CONFLICT (code) DO NOTHING;
