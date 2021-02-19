package br.com.redhat.routes;

import java.util.Random;

import javax.sql.DataSource;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class BootstrapRoute extends RouteBuilder {

	@Autowired
	DataSource dataSource;

	@Override
	public void configure() throws Exception {
		from("direct:createFile")
			.setHeader(Exchange.FILE_NAME, simple("${header.expurgoFileName}"))
		.toD("file:arquivos/${header.expurgoPath}");
		
		from("timer:geraArquivo?period=3000")
		.routeId("gera-arquivo")
			.process( e -> { 
				Random random = new Random();
				e.getIn().setHeader("expurgoTipoArquivo", random.ints(1, 4).findFirst().getAsInt());
				e.getIn().setHeader("expurgoSistema", random.ints(1, 4).findFirst().getAsInt());
			}) 
			.setHeader("expurgoDate", simple("${date:now:yyyyMMddHHmmss}"))
			.setHeader("expurgoPath", simple("${header.expurgoSistema}/${header.expurgoTipoArquivo}/${date:now:yyyy}/${date:now:MM}/${date:now:dd}"))
			.setHeader("expurgoFileName", simple("${header.expurgoSistema}${header.expurgoTipoArquivo}${header.expurgoDate}.txt"))
			.setBody(simple("Arquivo gerado: ${header.expurgoFileName}\nDiretório: ${header.expurgoPath}"))
//			.log("Gerando arquivo ${header.expurgoFileName} no diretório ${header.expurgoPath}")
		.to("direct:createFile")
		.to("direct:salva-database");
		
		from("direct:salva-database")
		.routeId("salva-database")
//			.log("Salvando no banco os dados do arquivo ${header.expurgoFileName}")
			.setBody(simple("INSERT INTO DOCUMENTO(SISTEMA, TIPO_DOCUMENTO, DATA_DOCUMENTO, NOME, CAMINHO, DELETADO) values('${header.expurgoSistema}','${header.expurgoTipoArquivo}', '${header.expurgoDate}', '${header.expurgoFileName}', '${header.expurgoPath}', FALSE)"))
		.to("jdbc:dataSource");
		
	}

}
