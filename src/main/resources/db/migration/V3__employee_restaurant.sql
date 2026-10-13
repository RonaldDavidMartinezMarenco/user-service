ALTER TABLE app_user
    ADD CONSTRAINT employee_restaurant CHECK (
        (role_id = 3 AND restaurant_id IS NOT NULL)
        OR (role_id <> 3 AND restaurant_id IS NULL)
    );
