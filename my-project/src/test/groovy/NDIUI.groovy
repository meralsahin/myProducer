import spock.lang.*;
import com.meral.restaws.*;

class NDIUITestSpec extends Specification {
	def "NDIUI Test"() {
		when: 
		def ndiui = new NameDateIpUaId("Meral", new Date(), "123.34.12.55", "UserAgent")

		then:
		ndiui.getMyname() == "Meral"
		ndiui.getMyIP() == "123.34.12.55"
		ndiui.getUserAgent() == "UserAgent"
		ndiui.getGuid().length() == 36
	}
}