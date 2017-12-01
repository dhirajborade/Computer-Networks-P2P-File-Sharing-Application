import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MessageWriter {
	private Message message;
	private DataOutputStream outStream;

	/**
	 * @param message
	 * @param outStream
	 */
	public MessageWriter(Message message, DataOutputStream outStream) {
		this.setMessage(message);
		this.setOutStream(outStream);
	}

	/**
	 * @return the message
	 */
	public Message getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(Message message) {
		this.message = message;
	}

	/**
	 * @return the outStream
	 */
	public DataOutputStream getOutStream() {
		return outStream;
	}

	/**
	 * @param outStream
	 *            the outStream to set
	 */
	public void setOutStream(DataOutputStream outStream) {
		this.outStream = outStream;
	}

	public void writeObject() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		System.out.println("Sending Message :" + this.getMessage());
		if (this.getMessage() instanceof HandShake) {
			HandShake handShakeMessage = (HandShake) this.getMessage();
			bos.write(handShakeMessage.getHeader(), 0, handShakeMessage.getHeader().length);
			bos.write(handShakeMessage.getZeroBits(), 0, handShakeMessage.getZeroBits().length);
			bos.write(handShakeMessage.getPeerID(), 0, handShakeMessage.getPeerID().length);
		} else {
			System.out.println(this.getOutStream().size());
			bos.write(ByteBuffer.allocate(4).putInt(this.getMessage().getLength()).array(), 0, 4);
			bos.write(new byte[] { this.getMessage().getType() }, 0, 1);
			if (!((this.getMessage().getPayload() != null) && (this.getMessage().getPayload().length > 0))) {

			} else {
				System.out.println("Payload Length to be sent:" + this.getMessage().getPayload().length);
				bos.write(this.getMessage().getPayload(), 0, this.getMessage().getPayload().length);
			}
		}
		System.out.println("Writing Buffer Size:" + bos.size());
		this.getOutStream().write(bos.toByteArray());
		this.getOutStream().flush();
	}

}