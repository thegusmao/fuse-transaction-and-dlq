CREATE TABLE COMPRA( 
    ID INT AUTO_INCREMENT PRIMARY KEY,
    VALOR BIGINT NOT NULL,
    ITENS BIGINT NOT NULL,
    STATUS VARCHAR(30) NOT NULL,
    CRIADO_EM DATETIME DEFAULT CURRENT_TIMESTAMP
)
;

CREATE TABLE PAGAMENTO( 
    ID INT AUTO_INCREMENT PRIMARY KEY,
    ID_COMPRA INT,
    BANDEIRA VARCHAR(30) NOT NULL,
    PARCELAS INT,
    CRIADO_EM DATETIME DEFAULT CURRENT_TIMESTAMP
)
;

