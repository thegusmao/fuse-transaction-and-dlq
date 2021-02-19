UPDATE COMPRA SET STATUS = 'PAGAMENTO_APROVADO' WHERE ID = :#${body[id]};
-- :#${body[id]}::integer