CREATE OR REPLACE FUNCTION set_updated_at()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DO $$
    DECLARE
        t RECORD;
    BEGIN
        FOR t IN
            SELECT tablename
            FROM pg_tables
            WHERE schemaname = 'public'
              AND tablename IN (
                                'rulesets','realms','abilities','ability_costs','ability_prerequisites',
                                'trainings','training_ability_grants',
                                'characters','acquisition_sources','character_abilities','point_transactions'
                )
            LOOP
                EXECUTE format('
      DROP TRIGGER IF EXISTS trg_%I_updated_at ON %I;
      CREATE TRIGGER trg_%I_updated_at
      BEFORE UPDATE ON %I
      FOR EACH ROW
      EXECUTE FUNCTION set_updated_at();',
                               t.tablename, t.tablename, t.tablename, t.tablename
                        );
            END LOOP;
    END $$;

DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1 FROM pg_constraint WHERE conname = 'fk_characters_alchemy_path'
        ) THEN
            ALTER TABLE characters
                ADD CONSTRAINT fk_characters_alchemy_path
                    FOREIGN KEY (alchemy_path_id) REFERENCES alchemy_paths(id) ON DELETE SET NULL;
        END IF;
    END $$;