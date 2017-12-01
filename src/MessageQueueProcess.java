import java.io.IOException;

public class MessageQueueProcess implements Runnable {

	private PeerProcess peerProcess;

	public MessageQueueProcess(PeerProcess peerProcess) {
		super();
		this.setPeerProcess(peerProcess);
	}

	/**
	 * @return the peerProcess
	 */
	public PeerProcess getPeerProcess() {
		return peerProcess;
	}

	/**
	 * @param peerProcess
	 *            the peerProcess to set
	 */
	public void setPeerProcess(PeerProcess peerProcess) {
		this.peerProcess = peerProcess;
	}

	@Override
	public void run() {
		try {
			for (; !this.getPeerProcess().exit;) {
				for (; !this.getPeerProcess().blockingQueueMessageWriter.isEmpty();) {
					MessageWriter ms = this.getPeerProcess().blockingQueueMessageWriter.take();
					ms.writeObject();
				}
			}
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
			this.getPeerProcess().exit = true;
		}
	}
}