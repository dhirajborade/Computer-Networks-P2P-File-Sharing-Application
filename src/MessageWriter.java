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
		System.out.println("Sending message :"+m);
		if (m instanceof HandShake) {
			HandShake hs = (HandShake) m;

			bos.write(hs.header, 0, hs.header.length);
			bos.write(hs.zerobits, 0, hs.zerobits.length);
			bos.write(hs.peerID, 0, hs.peerID.length);
		} else {
			System.out.println(os.size());
			bos.write(ByteBuffer.allocate(4).putInt(m.length).array(), 0 ,4);
			//os.flush();
			bos.write(new byte[]{m.type},0,1);
			//os.flush();
			
			if ((m.payload != null) && (m.payload.length > 0)) {
				System.out.println("payload length tp be sent:"+m.payload.length);
				//System.out.println("payload:"+Arrays.toString(m.payload));
				bos.write(m.payload,0,m.payload.length);
			}
		}
		//bos.flush();
		System.out.println("writing buffer size:"+bos.size());
		os.write(bos.toByteArray());
		os.flush();
	}

}