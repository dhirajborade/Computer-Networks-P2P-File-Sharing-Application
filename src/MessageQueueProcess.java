import java.io.IOException;

public class MessageQueueProcess implements Runnable {
	PeerProcess peerProcess;

	/**
	 * @param peerProcess
	 */
	public MessageQueueProcess(PeerProcess peerProcess) {
		super();
		this.peerProcess = peerProcess;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			while (!peerProcess.exit) {
				while (!peerProcess.bqm.isEmpty()) {
					MessageWriter ms = peerProcess.bqm.take();
					ms.writeObject();
				} 
				/*else {
					int peerCompleteFileReceived = 0;
					for (Peer p : peerProcess.peerList) {
						if (peerProcess.checkIfFullFileRecieved(p)) {
							peerCompleteFileReceived++;
						}
					}
					if (peerCompleteFileReceived == peerProcess.peerList.size()) {
						// check if you recievecd the whole file
						if (peerProcess.checkIfFullFileRecieved(peerProcess.currentPeer)) {
							// now terminate the process of executorService
							// exec.shutdown();

							break;
						}
					}
				}*/
			}
		} catch (InterruptedException | IOException ex) {
			ex.printStackTrace();
			peerProcess.exit=true;
		}

	}

}