
-- INSERT INTO hibernate_sequence (next_val) VALUES (2);
INSERT INTO role1 (id, rolename) VALUES (1,"Role_ADMIN"),(2,"ROLE_USER");
INSERT INTO user1 (id,name,password) VALUES (1,"admin","6d789d4353c72e4f625d21c6b7ac2982");
INSERT INTO user1_roles (user_id,roles_id) VALUES (1,1);

INSERT INTO ex_info (id,node_ip, port,user_name) VALUES (1,"192.168.142.132",10000,"admin");
INSERT INTO ex_info (id,node_ip, port,user_name) VALUES (2,"192.168.142.132",10001,"123");


INSERT INTO user1 (id,name,password) VALUES (2,"123","1dc568b64c0f67e7a86c89a12fa5bd5f");
INSERT INTO user1_roles (user_id,roles_id) VALUES (2,1);


INSERT INTO info1 VALUES (1,22,"973056182@qq.com","张三","Role_ADMIN","男","15649879234","admin");
INSERT INTO info1 VALUES (2,32,"563056182@qq.com","李四","Role_ADMIN","男","12356783267","123");

INSERT INTO node_info(id,ip,user_name,password,ssh_port) VALUES (1,"192.168.142.132","root","123456",22);
INSERT INTO node_info(id,ip,user_name,password,ssh_port) VALUES (2,"192.168.142.133","root","123456",22);