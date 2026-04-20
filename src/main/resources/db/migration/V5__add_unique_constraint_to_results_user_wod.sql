ALTER TABLE results
    ADD CONSTRAINT uk_results_user_wod UNIQUE (user_id, wod_id);
