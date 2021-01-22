package br.com.redhat.routes;

import java.io.FileNotFoundException;

import javax.sql.DataSource;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExpurgoRoute extends RouteBuilder {

	@Autowired
	DataSource dataSource;
	
	@Override
	public void configure() throws Exception {
		String sqlConsulta = "SELECT SISTEMA, TIPO_DOCUMENTO, DATA_DOCUMENTO, NOME, CAMINHO FROM DOCUMENTO WHERE TIPO_DOCUMENTO = ${header.tipoArquivo} AND DATA_DOCUMENTO < ${header.dataExpurgo}";
		String sqlUpdate = "UPDATE DOCUMENTO d SET d.DELETADO = TRUE WHERE d.NOME = '${header.CamelFileName}'";

		from("timer:qualquer?period=10000")
			.setHeader("segundosRetencao", constant(1200))
			.setHeader("tipoArquivo", constant(3))
		.to("direct:encontra-arquivos");
		
		from("direct:encontra-arquivos")
			.routeId("encontra-arquivos")
			.process(exchange -> {
				Long segundos = exchange.getIn().getHeader("segundosRetencao", Long.class);
				
				exchange.getIn().setHeader("dataExpurgo", segundos);
			})
			.setBody(simple(sqlConsulta))
		.log("${body}")
		.to("jdbc:dataSource")
			.split(body())
			.choice()
				.when(simple("${body != null}"))
					.to("seda:processa-expurgo")
				.otherwise()
					.to("mock:end");
		//[]
		//[{"SISTEMA":1,"TIPO_DOCUMENTO":2,"DATA_DOCUMENTO":20210120145135,"NOME":"1220210120145135.txt","CAMINHO":"1/2/2021/01/20"},
		//{"SISTEMA":2,"TIPO_DOCUMENTO":2,"DATA_DOCUMENTO":20210120145138,"NOME":"2220210120145138.txt","CAMINHO":"2/2/2021/01/20"}]%      
		
		from("seda:processa-expurgo")
			.routeId("processa-expurgo")
			.log("Processando arquivo em: arquivos/${body[CAMINHO]}/${body[NOME]}")
				.pollEnrich().simple("file:arquivos/${body[CAMINHO]}?fileName=${body[NOME]}").timeout(0)
					.to("file:deletados")
					.setBody(simple(sqlUpdate))
			.to("jdbc:dataSource");
		
	}
}
