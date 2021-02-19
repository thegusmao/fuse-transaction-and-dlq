package br.com.redhat.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.stereotype.Component;

import br.com.redhat.bootstrap.Compra;

@Component
public class RestRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		restConfiguration().host("0.0.0.0").port(8080).bindingMode(RestBindingMode.auto);

		rest("/compras").post()
//			.type(Compra.class)
			.outType(Compra.class).consumes("application/json")
			.param()
				.name("compra").type(RestParamType.body).dataType("string").required(true)
			.endParam()
			.route()
				.routeId("rest-nova-compra")
			.to("direct:nova-compra")
		.endRest();
		
		rest("/pagamentos").post()
			.type(Compra.class).outType(Compra.class).consumes("application/json")
			.param()
				.name("compra").type(RestParamType.body).dataType("string").required(true)
			.endParam()
			.route()
				.routeId("rest-efetua-pagamento")
			.to("direct:pagamento")
		.endRest();		
	}
}
