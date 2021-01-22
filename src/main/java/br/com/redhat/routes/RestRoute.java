package br.com.redhat.routes;

import java.util.OptionalLong;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.stereotype.Component;

@Component
public class RestRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		restConfiguration().host("0.0.0.0").port(8080).bindingMode(RestBindingMode.auto);

		rest("/")
			.post("expurgo")
				.param()
					.name("segundosRetencao").type(RestParamType.query).dataType("long").required(true)
					.name("tipoArquivo").type(RestParamType.query).dataType("int").required(true)
				.endParam()
				.route()
					.routeId("rest-expurga-arquivo")
				.to("direct:encontra-arquivos")
			.endRest();
	}
	
	public static void main(String[] args) {
		java.util.OptionalLong first = new java.util.Random().longs(10000L, 99999L).findFirst();
		String s = "AV" + first.getAsLong();
		System.out.println(s);
	}
}
