import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MessageWriter {
	public Message m;
	public DataOutputStream os;

	/**
	 * @param m
	 * @param os
	 */
	public MessageWriter(Message m, DataOutputStream os) {
		this.m = m;
		this.os = os;
	}

	public void writeObject() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		System.out.println("Sending Message :" + m);
		if (m instanceof HandShake) {
			HandShake handShakeMessage = (HandShake) m;
			bos.write(handShakeMessage.getHeader(), 0, handShakeMessage.getHeader().length);
			bos.write(handShakeMessage.getZeroBits(), 0, handShakeMessage.getZeroBits().length);
			bos.write(handShakeMessage.getPeerID(), 0, handShakeMessage.getPeerID().length);
		} else {
			System.out.println(os.size());
			bos.write(ByteBuffer.allocate(4).putInt(m.length).array(), 0, 4);
			bos.write(new byte[] { m.type }, 0, 1);
			if ((m.payload != null) && (m.payload.length > 0)) {
				System.out.println("Payload Length tp be sent:" + m.payload.length);
				bos.write(m.payload, 0, m.payload.length);
			}
		}
		System.out.println("Writing Buffer Size:" + bos.size());
		os.write(bos.toByteArray());
		os.flush();
	}

}