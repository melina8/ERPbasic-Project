DROP DATABASE IF EXISTS productmanager;
CREATE DATABASE IF NOT EXISTS productmanager;
USE productmanager;

CREATE TABLE supplier (
sup_id  varchar(3) not null unique,
company_name varchar (30) not null,
address varchar (30),
phonenumber varchar(10) not null,
email varchar (50),
CONSTRAINT sup_id_pk PRIMARY KEY (sup_id) 
);

CREATE TABLE customer (
cus_id varchar(4) not null unique,
customer_name varchar (30) not null,
address varchar (30),
phonenumber varchar (10) not null,
email varchar (50), 
CONSTRAINT cus_id_pk PRIMARY KEY (cus_id) 
);

CREATE TABLE products (
sup_id varchar(3) not null,
productname varchar(5) not null,
productdescription varchar (100) not null,
unitCost double not null,
taxRate varchar(3) not null, 
tax double not null,
time_changed timestamp,
CONSTRAINT pr_productname_pk PRIMARY KEY(productname, time_changed), 
CONSTRAINT pr_supid_fk FOREIGN KEY (sup_id) REFERENCES supplier (sup_id)
);

-- drop table receiving_orders;
CREATE TABLE receiving_orders (
rec_no int not null unique,
sup_id varchar(3) not null,
supplier_name varchar (30) not null,
rec_date date not null,
CONSTRAINT recno_date_pk PRIMARY KEY (rec_no, rec_date), 
CONSTRAINT ro_supid_fk FOREIGN KEY (sup_id) REFERENCES supplier (sup_id) -- on delete no action on update cascade
);

-- drop table received_items;
 CREATE TABLE received_items (
rec_no int not null,
productname varchar(5) not null, 
pieces int not null, 
CONSTRAINT rn_recno_productname_pk PRIMARY KEY (rec_no, productname), 
CONSTRAINT rn_recno_fk FOREIGN KEY (rec_no) REFERENCES receiving_orders (rec_no), -- on delete cascade on update cascade,
CONSTRAINT rn_prona_fk FOREIGN KEY (productname) REFERENCES products (productname) -- on delete no action on update cascade
); 

CREATE TABLE shipping_orders (
ship_no int not null unique,
cus_id varchar(4) not null,
customer_name varchar (30) not null,
ship_date date not null,
CONSTRAINT so_shipno_pk PRIMARY KEY (ship_no), 
CONSTRAINT so_cusid_fk FOREIGN KEY (cus_id) REFERENCES customer (cus_id) -- on delete no action on update cascade
);

CREATE TABLE ordered_items (
ship_no int not null,
productname varchar(5) not null, 
pieces int not null, 
unitCost double not null,
tax double not null,
discount int,
finalCost double not null,
CONSTRAINT oi_shipno_productname_pk PRIMARY KEY (ship_no, productname), 
CONSTRAINT sn_shipno_fk FOREIGN KEY (ship_no) REFERENCES shipping_orders (ship_no), -- on delete cascade on update cascade,
CONSTRAINT sn_prona_fk FOREIGN KEY (productname) REFERENCES products (productname) -- on delete no action on update cascade
); 

-- drop table cus_receipts;
CREATE TABLE cus_receipts (
receipt_date date not null,
receipt_no int not null unique,
cus_id varchar(4) not null,
customer_name varchar (30) not null,
amount double not null,
select_option varchar(8) not null,
notes varchar(200),
CONSTRAINT cr_recno_cusId_pk PRIMARY KEY (receipt_no, cus_id), 
CONSTRAINT cr_cusId_fk FOREIGN KEY (cus_id) REFERENCES customer (cus_id) -- on delete cascade on update cascade,
); 


-- drop table storage_area;
CREATE TABLE storage_area (
sup_id varchar(3) not null,
productname varchar(5) not null unique,
pieces int not null, 
CONSTRAINT st_supid_productname_pk PRIMARY KEY(sup_id, productname),
CONSTRAINT st_supid_fk FOREIGN KEY (sup_id) REFERENCES supplier (sup_id), -- on delete no action on update cascade,
CONSTRAINT st_prna_fk FOREIGN KEY (productname) REFERENCES products (productname) -- on delete no action on update cascade
);

-- drop table tableView_products_temp;
CREATE TABLE tableView_products_temp (
ship_no int not null,
productname varchar(5) not null, 
pieces int not null, 
unitCost double not null,
tax double not null,
discount int,
finalCost double not null
-- time_changed timestamp
-- CONSTRAINT oi_shipno_productname_pk PRIMARY KEY (ship_no, productname), 
-- CONSTRAINT sn_shipno_fk FOREIGN KEY (ship_no) REFERENCES shipping_orders (ship_no), -- on delete cascade on update cascade,
-- CONSTRAINT sn_prona_fk FOREIGN KEY (productname) REFERENCES products (productname) -- on delete no action on update cascade
);