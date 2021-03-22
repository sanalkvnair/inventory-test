INSERT INTO INVENTORY(art_id, name, stock) VALUES(1, 'leg', 12);
INSERT INTO INVENTORY(art_id, name, stock) VALUES(2, 'screw', 17);
INSERT INTO INVENTORY(art_id, name, stock) VALUES(3, 'seat', 2);
INSERT INTO INVENTORY(art_id, name, stock) VALUES(4, 'table top', 1);

INSERT INTO PRODUCTS(product_id, name) VALUES(1, 'Dining Chair');
INSERT INTO PRODUCTS(product_id, name) VALUES(2, 'Dining Table');

INSERT INTO PRODUCT_STRUCTURE(id, art_id, product_id, amount_of) VALUES(1, 1, 1, 4);
INSERT INTO PRODUCT_STRUCTURE(id, art_id, product_id, amount_of) VALUES(2, 2, 1, 8);
INSERT INTO PRODUCT_STRUCTURE(id, art_id, product_id, amount_of) VALUES(3, 3, 1, 1);

INSERT INTO PRODUCT_STRUCTURE(id, art_id, product_id, amount_of) VALUES(4, 1, 2, 4);
INSERT INTO PRODUCT_STRUCTURE(id, art_id, product_id, amount_of) VALUES(5, 2, 2, 8);
INSERT INTO PRODUCT_STRUCTURE(id, art_id, product_id, amount_of) VALUES(6, 4, 2, 1);