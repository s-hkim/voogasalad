package network.framework.format;

/**
 * @author Austin Liu (abl17) and Chris Streiffer (cds33)
 *
 */
public enum Request {
	ERROR("Error"),
	CONNECTION("Connetion"),
	LOADGROUP("Load Group"),
	CREATEGROUP("Create Group");
	
	private final String requestType;
	
	Request (String request) {
		this.requestType = request;
	}
	
	@Override
	public String toString () {
		return requestType;
	}
}
