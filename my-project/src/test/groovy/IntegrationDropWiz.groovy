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
		def myjson = jsonSlurper.parse(new URL('http://127.0.0.1:443/hello-world?name=meral'))
		def myjson2 = jsonSlurper.parse(new URL('http://127.0.0.1:443/hello-world?name=MeRaL'))
		def myjson3 = jsonSlurper.parse(new URL('http://127.0.0.1:443/hello-world'))
		def myjson4 = jsonSlurper.parse(new URL('http://127.0.0.1:443/hello-world?name='))
		//def myjson2 = jsonSlurper.parse(new URL('http://127.0.0.1:443/hello-world?name='))
		then:
		def firstID = myjson.id 
		myjson.content == 'Hello, meral!'
		myjson2.content == 'Hello, MeRaL!'
		myjson2.id == firstID+1
		myjson3.content == 'Hello, Stranger!'
		myjson3.id == firstID+2
		myjson4.content == 'Hello, !'
		myjson4.id == firstID+3
		//myjson2.content == 'Hello, Stranger!'
	}
}