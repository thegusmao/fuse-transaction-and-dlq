INSERT INTO COMPRA( 
    VALOR,
    ITENS,
    STATUS
) VALUES (
	:#${body[valor]},
	:#${body[itens]},
	'NOVA_COMPRA'
)
;