import java.io.Serializable;

public class Message implements Serializable {

	private static final long serialVersionUID = 985205199122584865L;
	private int length;
	private byte type;
	private byte[] payload;

	public Message() {
		super();
	}

	public Message(int length, byte type, byte[] payload) {
		this.setLength(length);
		this.setType(type);
		this.setPayload(payload);
	}

	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @param length
	 *            the length to set
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * @return the type
	 */
	public byte getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(byte type) {
		this.type = type;
	}

	/**
	 * @return the payload
	 */
	public byte[] getPayload() {
		return payload;
	}

	/**
	 * @param payload
	 *            the payload to set
	 */
	public void setPayload(byte[] payload) {
		this.payload = payload;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	// TODO
	@Override
	public String toString() {
		return "Message [length=" + this.getLength() + ", type=" + Byte.toUnsignedInt(this.getType()) + "]";
	}

}