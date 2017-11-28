import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * 
 */

/**
 * @author Tejas
 *
 */
public class MessageReader {

	Socket socket;
	PeerProcess peerProcess;
	/**
	 * @param in
	 * @throws IOException
	 */

	private boolean isHandshakeDone = false;

	public MessageReader(Socket socket, PeerProcess p) throws IOException {
		// super(in);
		this.socket = socket;
		this.peerProcess = p;
	}

	public Object readObject() throws Exception {
		InputStream inputStream = socket.getInputStream();
		if (isHandshakeDone) {
			while (!peerProcess.exit && inputStream.available() < 4)
				;
			
			byte[] lengthBytes = new byte[4];
			inputStream.read(lengthBytes, 0, 4);
			int length = ByteBuffer.wrap(lengthBytes).getInt();

			while (inputStream.available() < length)
				;
			byte[] typeBuffer = new byte[1];
			try {
				inputStream.read(typeBuffer, 0, 1);
			} catch (IOException e) {
				e.printStackTrace();
				throw e;
			}
			byte type = typeBuffer[0];
			byte[] payload = null;
			if (length > 1) {
				payload = new byte[length - 1];
				int recievedBytes = 0;
				while (recievedBytes < (length - 1)) {
					try {
						recievedBytes += inputStream.read(payload, recievedBytes, length - 1);
					} catch (IOException e) {
						e.printStackTrace();
						throw e;
					}
				}
			}
			System.out.println("available after reading payload:"+inputStream.available());
			Message m = new Message(length, type, payload);
			return m;
		} else {
			byte[] header = new byte[18];
			try {
				inputStream.read(header, 0, 18);
			} catch (IOException e) {
				e.printStackTrace();
				throw e;
			}
			byte[] zerobits = new byte[10];
			try {
				inputStream.read(zerobits, 0, 10);
			} catch (IOException e) {
				e.printStackTrace();
				throw e;
			}
			byte[] peerId = new byte[4];
			try {
				inputStream.read(peerId);
			} catch (IOException e) {
				e.printStackTrace();
				throw e;
			}
			HandShake h = new HandShake(ByteBuffer.wrap(peerId).getInt());
			isHandshakeDone = true;
			return h;
		}

	}

}
