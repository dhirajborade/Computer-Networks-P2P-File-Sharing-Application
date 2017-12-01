/**
 *
 */

/**
 * @author dhirajborade
 *
 */
public class RequestPayload extends Payload {

	/**
	 *
	 */
	private static final long serialVersionUID = 9208727206802180498L;

	// 4-byte piece index field we will index from -2,147,483,648 to 2,147,483,647
	private byte[] requestPayload;

	/**
	 * @param index
	 */
	public RequestPayload(byte[] payload) {
		super(payload);
		this.setRequestPayload(payload);
	}

	/**
	 * @return the requestPayload
	 */
	public byte[] getRequestPayload() {
		return requestPayload;
	}

	/**
	 * @param requestPayload
	 *            the requestPayload to set
	 */
	public void setRequestPayload(byte[] requestPayload) {
		this.requestPayload = requestPayload;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
