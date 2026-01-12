-- Evita duplicati: un solo "event_day" per card+giornata
CREATE UNIQUE INDEX IF NOT EXISTS ux_card_tx_event_day_unique
    ON card_transactions(card_id, source, reference_id)
    WHERE is_active = true AND source = 'event_day';

CREATE OR REPLACE FUNCTION apply_ps_from_attendance()
    RETURNS TRIGGER AS
$$
DECLARE
    v_card_id INTEGER;
    v_tx_id   INTEGER;
BEGIN
    -- Serve solo se la riga è attiva
    IF NEW.is_active IS DISTINCT FROM true THEN
        RETURN NEW;
    END IF;

    SELECT c.card_id INTO v_card_id
    FROM characters c
    WHERE c.id = NEW.character_id;

    IF v_card_id IS NULL THEN
        RAISE EXCEPTION 'Character % has no card_id (cannot award PS)', NEW.character_id;
    END IF;

    -- === CASO INSERT ===
    IF TG_OP = 'INSERT' THEN
        IF NEW.status = 'present' THEN
            INSERT INTO card_transactions(card_id, source, amount, reference_id, note)
            VALUES (v_card_id, 'event_day', 1, NEW.event_day_id, 'PS +1 for attendance')
            ON CONFLICT DO NOTHING;

            UPDATE cards
            SET total_points     = COALESCE(total_points, 0) + 1,
                available_points = COALESCE(available_points, 0) + 1
            WHERE id = v_card_id;
        END IF;

        RETURN NEW;
    END IF;

    -- === CASO UPDATE ===
    IF TG_OP = 'UPDATE' THEN
        -- Se prima NON era present e ora È present: aggiungi
        IF (OLD.status IS DISTINCT FROM 'present') AND (NEW.status = 'present') THEN
            INSERT INTO card_transactions(card_id, source, amount, reference_id, note)
            VALUES (v_card_id, 'event_day', 1, NEW.event_day_id, 'PS +1 for attendance (status change)')
            ON CONFLICT DO NOTHING;

            UPDATE cards
            SET total_points     = COALESCE(total_points, 0) + 1,
                available_points = COALESCE(available_points, 0) + 1
            WHERE id = v_card_id;

            RETURN NEW;
        END IF;

        -- Se prima ERA present e ora NON è present: rimuovi
        IF (OLD.status = 'present') AND (NEW.status IS DISTINCT FROM 'present') THEN
            SELECT id INTO v_tx_id
            FROM card_transactions
            WHERE card_id = v_card_id
              AND source = 'event_day'
              AND reference_id = NEW.event_day_id
              AND is_active = true
            ORDER BY id DESC
            LIMIT 1;

            IF v_tx_id IS NOT NULL THEN
                UPDATE card_transactions
                SET is_active = false
                WHERE id = v_tx_id;

                UPDATE cards
                SET total_points     = COALESCE(total_points, 0) - 1,
                    available_points = COALESCE(available_points, 0) - 1
                WHERE id = v_card_id;

                -- Se vai sotto zero, vuol dire che i dati sono già inconsistenti
                IF (SELECT available_points FROM cards WHERE id = v_card_id) < 0 THEN
                    RAISE EXCEPTION 'Card % would go negative on available_points. Data inconsistent.', v_card_id;
                END IF;
            END IF;

            RETURN NEW;
        END IF;

        RETURN NEW;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_apply_ps_from_attendance ON character_attendance;

CREATE TRIGGER trg_apply_ps_from_attendance
    AFTER INSERT OR UPDATE OF status, is_active
    ON character_attendance
    FOR EACH ROW
EXECUTE FUNCTION apply_ps_from_attendance();
