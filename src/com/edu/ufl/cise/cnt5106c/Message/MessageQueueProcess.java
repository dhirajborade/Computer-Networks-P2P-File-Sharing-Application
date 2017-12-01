package com.edu.ufl.cise.cnt5106c.Message;
import java.io.IOException;

import com.edu.ufl.cise.cnt5106c.Peer.PeerProcess;

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
			for (; !this.getPeerProcess().isExit();) {
				for (; !this.getPeerProcess().getBlockingQueueMessages().isEmpty();) {
					MessageWriter ms = this.getPeerProcess().getBlockingQueueMessages().take();
					ms.writeObject();
				}
			}
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
			this.getPeerProcess().setExit(true);
		}
	}
}