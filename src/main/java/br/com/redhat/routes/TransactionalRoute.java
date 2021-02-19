package br.com.redhat.routes;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.sql.SqlConstants;
import org.apache.camel.http.common.HttpMethods;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import br.com.redhat.bootstrap.Compra;

@Component("transactionalBean")
public class TransactionalRoute extends RouteBuilder {
	
	@Override
	public void configure() throws Exception {
		
		errorHandler(deadLetterChannel("jms:queue:dead")
				   .useOriginalMessage().maximumRedeliveries(2).redeliveryDelay(5000));
		
		from("direct:nova-compra")
			.routeId("nova-compra")
			.setHeader(SqlConstants.SQL_RETRIEVE_GENERATED_KEYS, constant(true))
			.to("sql:classpath:sql/insert-compra.sql")
			.to("sql:classpath:sql/select-compra-by-id.sql?outputClass=br.com.redhat.bootstrap.Compra&outputType=SelectOne")
			.setProperty("jsonResponse",simple("${body}")) //guarda objeto compra para o Output
			.marshal()
				.json(JsonLibrary.Jackson)
		.inOnly("jms:queue:nova-compra") //fire and forget mensagem
		.setBody(simple("${exchangeProperty.jsonResponse}")) //seta no output objeto compra
		;
		
		from("jms:queue:nova-compra")
			.routeId("processamento-nova-compra")
			.transacted()
				.removeHeaders("CamelHttp*")
				.setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.POST))
				.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_VALUE))
				.log("[ENVIANDO COMPRA PARA APROVAÇÃO DO PAGAMENTO]")
			.to("http://localhost:8080/pagamentos")
				.log("[PAGAMENTO APROVADO]")
				.unmarshal()
					.json(JsonLibrary.Jackson, Map.class)
				.bean("transactionalBean", "setCompraId")
				.log("[ATUALIZANDO STATUS DA COMPRA]")
			.to("sql:classpath:sql/update-compra-pagamento-aprovado.sql")
				.log("[STATUS DA COMPRA ATUALIZADO!]")
			.to("sql:classpath:sql/select-compra-by-id2.sql?outputClass=br.com.redhat.bootstrap.Compra&outputType=SelectOne")
			.bean("transactionalBean", "lancaExceptionBanco")
		;
		
		from("direct:pagamento")
			.routeId("efetua-pagamento")
			.bean("transactionalBean", "lancaExceptionServico")
		.to("mock:end")
		;
		
		from("jms:queue:dead")
			.routeId("processamento-nova-compra-dlq")
			.log("[NOVA COMPRA DLQ]")
			.wireTap("log:br.com.redhat.novacompra?showAll=true&multiline=true")
			.process(ex -> {
				System.out.println();
			})
		.to("mock:end")
		;
	}

	public void setCompraId(Exchange exchange) throws Exception {
		Map compra = exchange.getIn().getBody(Map.class);
		exchange.getIn().setHeader("compraId", compra.get("id"));
		exchange.getIn().setBody(compra);
	}
	
	public void lancaExceptionServico(Exchange exchange) throws Exception {
		Compra compra = exchange.getIn().getBody(Compra.class);
		if(compra.getValor() == 0) {
			throw new Exception("Erro ao efetuar operação [valor]");
		}
		exchange.getIn().setBody(compra);
	}
	
	public void lancaExceptionBanco(Exchange exchange) throws Exception {
		Compra compra = exchange.getIn().getBody(Compra.class);
		System.out.println("[lancaExceptionBanco] Compra: " + compra);
		if(compra.getItens() == 0) {
			throw new RuntimeException("Erro ao efetuar operação [itens]");
		}
		exchange.getIn().setBody(compra);
	}

}
