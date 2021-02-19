--H2
SELECT * FROM COMPRA C WHERE C.ID = :#${header.CamelSqlGeneratedKeyRows[0][ID]};
--MySQL
--SELECT * FROM COMPRA C WHERE C.ID = :#${header.CamelSqlGeneratedKeyRows[0][GENERATED_KEY]};