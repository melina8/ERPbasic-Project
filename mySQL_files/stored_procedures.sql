-- input
SELECT * FROM supplier;


DROP PROCEDURE IF EXISTS input;
DELIMITER $$

CREATE PROCEDURE input(IN sup_id_param varchar(3))
BEGIN 
	SELECT company_name FROM supplier WHERE sup_id = sup_id_param;

END $$
DELIMITER ;

CALL input('ABC');

-- fillDescription

SELECT * FROM products;

DROP PROCEDURE IF EXISTS fillDescription;
DELIMITER $$

CREATE PROCEDURE fillDescription(IN productname_param varchar(5))
BEGIN 
	SELECT productdescription FROM products WHERE productname = productname_param;

END $$
DELIMITER ;

CALL fillDescription('ABC01');


-- loadSupItems
DROP PROCEDURE IF EXISTS loadSupItems;
DELIMITER $$

CREATE PROCEDURE loadSupItems(IN rec_no_param int)
BEGIN 
	SELECT distinct r.productname, p.productdescription, r.pieces 
         FROM received_items r JOIN products p INNER JOIN 
                (SELECT productname, MAX(time_changed) AS MaxDateTime FROM products GROUP BY productname) groupedp ON p.productname = groupedp.productname AND p.time_changed = groupedp.MaxDateTime
         WHERE  r.productname = p.productname AND rec_no = rec_no_param;


END $$
DELIMITER ;

-- CALL loadSupItems(1);

select * from received_items;

-- loadCusItems
-- DROP PROCEDURE IF EXISTS loadCusItems;
-- DELIMITER $$

-- CREATE PROCEDURE loadCusItems(IN ship_no_param int)
-- BEGIN 
-- 	SELECT o.productname, p.productdescription, p.unitCost, p.tax, o.discount, o.pieces, o.finalCost 
 --         FROM ordered_items o JOIN products p
  --        WHERE  o.productname = p.productname AND ship_no = ship_no_param;


-- END $$
-- DELIMITER ;

-- CALL loadCusItems(5);

-- loadCusItems
DROP PROCEDURE IF EXISTS loadCusItems;
DELIMITER $$

CREATE PROCEDURE loadCusItems(IN ship_no_param int)
BEGIN 
	SELECT distinct o.productname, p.productdescription, o.unitCost, o.tax, o.discount, o.pieces, o.finalCost 
         FROM ordered_items o JOIN products p INNER JOIN 
                (SELECT productname, MAX(time_changed) AS MaxDateTime FROM products GROUP BY productname) groupedp ON p.productname = groupedp.productname AND p.time_changed = groupedp.MaxDateTime
         WHERE  o.productname = p.productname AND ship_no = ship_no_param;


END $$
DELIMITER ;

-- CALL loadCusItems(1);

select * from received_items;

-- ----------------------------------------------------------------------
-- ----------------------------------------------------------------------

-- input2
SELECT * FROM customer;


DROP PROCEDURE IF EXISTS input2;
DELIMITER $$

CREATE PROCEDURE input2(IN cus_id_param varchar(4))
BEGIN 
	SELECT customer_name FROM customer WHERE cus_id = cus_id_param;

END $$
DELIMITER ;

-- CALL input('ABC');

-- fillDescription2

SELECT * FROM products;

DROP PROCEDURE IF EXISTS fillDescription2;
DELIMITER $$

CREATE PROCEDURE fillDescription2(IN productname_param varchar(5))
BEGIN 
	SELECT productdescription, unitCost, tax FROM products WHERE productname = productname_param;

END $$
DELIMITER ;

-- CALL fillDescription2('ABC02');

-- loadStock
DROP PROCEDURE IF EXISTS loadStock;
DELIMITER $$

CREATE PROCEDURE loadStock()
BEGIN 
	SELECT distinct s.productname, p.productdescription, s.pieces, s.sup_id 
         FROM storage_area s JOIN products p INNER JOIN 
		 (SELECT productname, MAX(time_changed) AS MaxDateTime FROM products GROUP BY productname) groupedp ON p.productname = groupedp.productname AND p.time_changed = groupedp.MaxDateTime
         WHERE  s.productname = p.productname;


END $$
DELIMITER ;

CALL loadStock();
select * from storage_area;
select * from ordered_items where productname = "ABC01";

-- loadBalance
DROP PROCEDURE IF EXISTS loadBalance;
DELIMITER $$

CREATE PROCEDURE loadBalance()
BEGIN 
create or replace view  mytable1 as (SELECT  c.cus_id, c.customer_name, sum(o.finalCost) as COST     

FROM  customer c JOIN shipping_orders s JOIN ordered_items o 
         WHERE  c.cus_id = s.cus_id AND s.ship_no = o.ship_no
         group by c.cus_id);   
         
create or replace view mytable2 as (SELECT  c.cus_id, c.customer_name, sum(r.amount) as RECEIPT
         FROM  customer c JOIN cus_receipts r 
         WHERE  c.cus_id = r.cus_id 
         group by c.cus_id); 

create or replace view mytable3 as (select mytable1.cus_id, mytable1.customer_name, round(ifnull(COST,0),2) AS COST, round(IFNULL(RECEIPT,0),2) AS RECEIPT, round(IFNULL(COST,0)-IFNULL(RECEIPT,0), 2) AS BALANCE
       FROM mytable1 left join mytable2
       on mytable1.cus_id = mytable2.cus_id
       
       union
       
       select mytable2.cus_id, mytable2.customer_name, round(IFNULL(COST,0),2) AS COST, round(IFNULL(RECEIPT,0),2) AS RECEIPT,  round(IFNULL(COST,0)-IFNULL(RECEIPT,0), 2) AS BALANCE
       FROM mytable1 right join mytable2
       on mytable1.cus_id = mytable2.cus_id
       ORDER BY BALANCE desc);
	
    SELECT * FROM mytable3;

END $$
DELIMITER ;

CALL loadBalance();

-- loadCustomerMoves()
DROP PROCEDURE IF EXISTS loadCustomerMoves;
DELIMITER $$

CREATE PROCEDURE loadCustomerMoves(IN cus_id_param varchar(4))
BEGIN 

SELECT  'ORDER' AS TYPE1, s.ship_no AS NO1, s.ship_date AS DATE1,  c.customer_name, round(sum(o.finalCost),2) as AMOUNT      
FROM  customer c JOIN shipping_orders s JOIN ordered_items o 
         WHERE  c.cus_id = s.cus_id AND s.ship_no = o.ship_no AND s.cus_id = cus_id_param
         GROUP BY s.ship_no
        
        UNION ALL
         
SELECT  'RECEIPT' AS TYPE1, r.receipt_no, r.receipt_date,  c.customer_name, round(-r.amount,2)
FROM  customer c JOIN cus_receipts r 
         WHERE  c.cus_id = r.cus_id AND r.cus_id = cus_id_param
         ORDER BY 3,2;
         
	
END $$
DELIMITER ;

-- CALL loadCustomerMoves('MENI');

-- loadCustomerMoves()
DROP PROCEDURE IF EXISTS loadProductMoves;
DELIMITER $$

CREATE PROCEDURE loadProductMoves(IN productname_param varchar(5))
BEGIN 

SELECT  'CUSTOMER' AS TYPE1, s.ship_no AS NO1, s.ship_date AS DATE1,  c.customer_name as NAME1, -o.pieces as ITEMS      
FROM  customer c JOIN shipping_orders s JOIN ordered_items o 
         WHERE  c.cus_id = s.cus_id AND s.ship_no = o.ship_no AND o.productname = productname_param
         
         
        
        UNION ALL
         
SELECT  'SUPPLIER' AS TYPE1, r.rec_no, r.rec_date,  s.company_name, i.pieces
FROM  supplier s JOIN receiving_orders r JOIN received_items i
         WHERE  s.sup_id = r.sup_id AND r.rec_no = i.rec_no AND i.productname = productname_param
         ORDER BY 3,2;
         
	
END $$
DELIMITER ;

CALL loadProductMoves('ABC01');


DROP PROCEDURE IF EXISTS findReceipt;
DELIMITER $$

CREATE PROCEDURE findReceipt(IN no_param int, IN date_param date)
BEGIN 
	SELECT * from cus_receipts  WHERE receipt_no = no_param AND receipt_date = date_param;

END $$
DELIMITER ;

-- CALL findReceipt(1, '2022-05-18');

DROP PROCEDURE IF EXISTS findOrder;
DELIMITER $$

CREATE PROCEDURE findOrder(IN no_param int, IN date_param date)
BEGIN 
	SELECT * from shipping_orders  WHERE ship_no = no_param AND ship_date = date_param;

END $$
DELIMITER ;

-- CALL findOrder(2, '2021-09-14');

DROP PROCEDURE IF EXISTS findSupOrder;
DELIMITER $$

CREATE PROCEDURE findSupOrder(IN no_param int, IN date_param date)
BEGIN 
	SELECT * from receiving_orders  WHERE rec_no = no_param AND rec_date = date_param;

END $$
DELIMITER ;

-- CALL findSupOrder(1, '2021-12-18');
