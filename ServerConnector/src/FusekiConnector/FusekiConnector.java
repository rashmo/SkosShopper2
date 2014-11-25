package FusekiConnector;

import org.apache.jena.fuseki.FusekiCmd;

public class FusekiConnector {
	
	public FusekiConnector() {
		FusekiCmd.main("--pages=fuseki/pages", "--update", "--config=fuseki/config-inf-tdb.ttl");
	}

	public static void main(String[] args) {
		FusekiConnector fc = new FusekiConnector();
	}
}
