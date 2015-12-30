CREATE DATABASE orderDBsmall;

USE orderDBsmall;

CREATE TABLE publisher (
  id int(10) NOT NULL,
  name varchar(100) NOT NULL,
  state varchar(2),
  PRIMARY KEY  (id)
);

CREATE TABLE book (
  id int(10) NOT NULL,
  title varchar(100) NOT NULL,
  authors varchar(200),
  publisher_id int(10) NOT NULL,
  copies int(10),
  pages int(10),
  PRIMARY KEY  (id)
);

CREATE TABLE customer (
  id int(10) NOT NULL,
  name varchar(25) NOT NULL,
  gender varchar(1),
  PRIMARY KEY  (id)
);

CREATE TABLE orders (
  customer_id int(10) NOT NULL,
  book_id int(10) NOT NULL,
  quantity int(10) NOT NULL
);

INSERT INTO book VALUES(200001,'Marias Diary (Plus S.)','Mark P. O. Morford',100082,5991,2530);
INSERT INTO book VALUES(200002,'Standing in the Shadows','Richard Bruce Wright',101787,2900,1860);
INSERT INTO book VALUES(200003,'Children of the Thunder','Carlo DEste',102928,3447,2154);
INSERT INTO book VALUES(200004,'The Great Gilly Hopkins','Gina Bari Kolata',101339,39,2809);
INSERT INTO book VALUES(200005,'Meine Juden--eure Juden','E. J. W. Barber',103089,206,2771);
INSERT INTO book VALUES(200006,'You Can Draw a Kangaroo','Amy Tan',101850,5296,2092);
INSERT INTO book VALUES(200007,'The Little Drummer Girl','Robert Cowley',104382,1006,2764);
INSERT INTO book VALUES(200008,'A Walk Through the Fire','Scott Turow',102008,8795,2543);
INSERT INTO book VALUES(200009,'The Nursing Home Murder','David Cordingly',102866,7380,2019);
INSERT INTO book VALUES(200010,'The Blanket of the Dark','Ann Beattie',103933,5242,1483);


INSERT INTO orders VALUES(304948,210140,5);
INSERT INTO orders VALUES(301767,207771,7);
INSERT INTO orders VALUES(301760,200306,8);
INSERT INTO orders VALUES(305236,205932,5);
INSERT INTO orders VALUES(300713,212656,9);
INSERT INTO orders VALUES(306314,200004,10);
INSERT INTO orders VALUES(305505,204891,5);
INSERT INTO orders VALUES(303694,201031,8);
INSERT INTO orders VALUES(302096,202729,8);
INSERT INTO orders VALUES(303495,212332,3);
INSERT INTO orders VALUES(303763,211233,3);
INSERT INTO orders VALUES(301261,214433,5);

INSERT INTO customer VALUES(306307,'VICTOR BILES','F');
INSERT INTO customer VALUES(306308,'WILEY VEITCH','F');
INSERT INTO customer VALUES(306309,'PAUL MERCEDES','M');
INSERT INTO customer VALUES(306310,'HERMA DANT','F');
INSERT INTO customer VALUES(306311,'LORRI HORNE','F');
INSERT INTO customer VALUES(306312,'WALTER SILVERMAN','M');
INSERT INTO customer VALUES(306313,'CLAUDE BRUNET','F');
INSERT INTO customer VALUES(306314,'FRED WHITENER','M');
INSERT INTO customer VALUES(306315,'OMER DOMINGUES','M');
INSERT INTO customer VALUES(306316,'FOSTER FLYTHE','F');
INSERT INTO customer VALUES(306317,'JORDON YEUNG','M');
INSERT INTO customer VALUES(306318,'FRANCISCO LAUGHTER','F');
INSERT INTO customer VALUES(306319,'ELSA HISLE','F');