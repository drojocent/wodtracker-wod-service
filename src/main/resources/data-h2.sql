INSERT INTO wods (id, name, description, type, date, approved) VALUES
    (1, 'Fran', '21-15-9 thrusters and pull-ups', 'FOR_TIME', CURRENT_DATE, TRUE),
    (2, 'Cindy', '20 minute AMRAP of 5 pull-ups, 10 push-ups and 15 air squats', 'AMRAP', DATEADD('DAY', 1, CURRENT_DATE), TRUE),
    (3, 'EMOM Core', '10 minute EMOM with 12 sit-ups and 8 burpees', 'EMOM', DATEADD('DAY', 2, CURRENT_DATE), FALSE);

INSERT INTO results (id, user_id, wod_id, result, created_at) VALUES
    (1, 2, 1, '04:12', DATEADD('MINUTE', -30, CURRENT_TIMESTAMP)),
    (2, 3, 1, '05:01', DATEADD('MINUTE', -20, CURRENT_TIMESTAMP)),
    (3, 2, 2, '14 rounds + 8 reps', DATEADD('MINUTE', -10, CURRENT_TIMESTAMP));

INSERT INTO wod_proposals (id, user_id, name, description, type, status, created_at) VALUES
    (1, 2, 'Open Prep', 'For time: 30 wall balls, 20 box jumps, 10 muscle-ups', 'FOR_TIME', 'PENDING', DATEADD('DAY', -1, CURRENT_TIMESTAMP)),
    (2, 3, 'Engine Builder', 'AMRAP 18 of 12 calories, 12 kettlebell swings and 12 toes-to-bar', 'AMRAP', 'PENDING', DATEADD('HOUR', -8, CURRENT_TIMESTAMP)),
    (3, 2, 'Lung Burner', 'EMOM 12 with 10 lunges and 8 push-ups', 'EMOM', 'APPROVED', DATEADD('DAY', -2, CURRENT_TIMESTAMP));

ALTER TABLE wods ALTER COLUMN id RESTART WITH 10;
ALTER TABLE results ALTER COLUMN id RESTART WITH 10;
ALTER TABLE wod_proposals ALTER COLUMN id RESTART WITH 10;
