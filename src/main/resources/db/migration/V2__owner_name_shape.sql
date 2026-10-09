ALTER TABLE app_user
    ADD CONSTRAINT first_name_shape CHECK (
        char_length(first_name) BETWEEN 2 AND 80
        AND first_name = btrim(first_name)
        AND position('  ' IN first_name) = 0
    ),
    ADD CONSTRAINT last_name_shape CHECK (
        char_length(last_name) BETWEEN 2 AND 80
        AND last_name = btrim(last_name)
        AND position('  ' IN last_name) = 0
    );
