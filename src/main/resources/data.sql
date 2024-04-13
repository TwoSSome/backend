INSERT into member (member_id, member_using_id, point) VALUES (1, 'test', 1000);
INSERT INTO review_post (body, price, member_id) VALUES ('hi my name is', '100000', 1);
INSERT INTO comment (body, member_id, review_id) VALUES ('good', 1, 1);