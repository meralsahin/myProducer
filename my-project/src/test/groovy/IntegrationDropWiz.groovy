import spock.lang.*;
//import com.meral.restaws.*;
//import com.meral.restaws.resources.*;
//import com.google.common.base.Optional;
//import com.fasterxml.jackson.databind.ObjectMapper;

import groovy.json.JsonSlurper;

import java.net.URL;

//checking if dropwizard service sends correct response to the client

class IntegrationDropWizTestSpec extends Specification {
	def "NDIUI Test"() {
		when: 
		def jsonSlurper = new JsonSlurper();
		def myjson = jsonSlurper.parse(new URL('http://52.2.122.251:443/hello-world?name=meral'))
		//def myjson2 = jsonSlurper.parse(new URL('http://52.2.122.251:443/hello-world?name='))
		then:
		myjson.content == 'Hello, meral!'
		//myjson2.content == 'Hello, Stranger!'
	}
}