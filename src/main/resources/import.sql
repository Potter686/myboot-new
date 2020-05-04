INSERT INTO ex_info (id, port,user_name) VALUES (1,10000,"admin");
-- INSERT INTO hibernate_sequence (next_val) VALUES (2);
INSERT INTO role1 (id, rolename) VALUES (1,"Role_ADMIN"),(2,"ROLE_USER");
INSERT INTO user1 (id,name,password) VALUES (1,"admin","6d789d4353c72e4f625d21c6b7ac2982");
INSERT INTO user1_roles (user_id,roles_id) VALUES (1,1);