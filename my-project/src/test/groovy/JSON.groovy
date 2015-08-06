import spock.lang.*;
import com.meral.restaws.*;
import com.meral.restaws.resources.*;
import com.google.common.base.Optional;
import com.fasterxml.jackson.databind.ObjectMapper;

class JSONTestSpec extends Specification {

//checking if records sent to kinesis in correct format

	def "NDIUI Test"() {
		when: 
		def dropwizServer = new HelloWorldResource("","");
		Optional<String> name = Optional.fromNullable("Meral");
		def bytes  = dropwizServer.makeMyJSON(name, "123.324.12.44", "U-A");

		then:
		ObjectMapper JSON = new ObjectMapper();
		NameDateIpUaId test = JSON.readValue(bytes, NameDateIpUaId.class);
		test.getMyname() == "Meral"
		test.getMyIP() == "123.324.12.44"
		test.getUserAgent() == "U-A"
		test.getGuid().length() == 36
	}
}