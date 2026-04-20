ALTER TABLE results
    ADD COLUMN wod_name VARCHAR(150);

ALTER TABLE results
    ADD COLUMN wod_description TEXT;

UPDATE results
SET
    wod_name = (SELECT w.name FROM wods w WHERE w.id = results.wod_id),
    wod_description = (SELECT w.description FROM wods w WHERE w.id = results.wod_id);

ALTER TABLE results
    ALTER COLUMN wod_name SET NOT NULL;

ALTER TABLE results
    ALTER COLUMN wod_description SET NOT NULL;
