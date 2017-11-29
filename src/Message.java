import java.io.Serializable;

public class Message implements Serializable {

	private static final long serialVersionUID = 985205199122584865L;
	int length;
	byte type;
	byte[] payload;

	public Message() {
		super();
	}

	public Message(int length, byte type, byte[] payload) {
		this.length = length;
		this.type = type;
		this.payload = payload;
	}

	@Override
	public String toString() {
		return "Message [length=" + length + ", type=" + Byte.toUnsignedInt(type) + "]";
	}

}