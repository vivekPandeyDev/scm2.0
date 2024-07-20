-- Insert user with ROLE_USER
INSERT INTO users (user_id, user_name, email, password, about, profile_pic, phone_number, enabled, email_verified, phone_verified, provider, provider_user_id, role_list, email_token)
VALUES
('user1', 'vivek', 'john.doe@example.com', 'vivek', 'About John', 'profile_pic_url_1', '1234567890', true, true, false, 'SELF', 'provider_user_id_1', ARRAY['ROLE_USER'], 'email_token_1');

-- Insert user with ROLE_ADMIN
INSERT INTO users (user_id, user_name, email, password, about, profile_pic, phone_number, enabled, email_verified, phone_verified, provider, provider_user_id, role_list, email_token)
VALUES
('user2', 'vishal', 'jane.smith@example.com', 'vishal', 'About Jane', 'profile_pic_url_2', '0987654321', true, true, true, 'GOOGLE', 'provider_user_id_2', ARRAY['ROLE_ADMIN'], 'email_token_2');