package com.edu.ufl.cise.cnt5106c.Managers;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import com.edu.ufl.cise.cnt5106c.Configuration.CommonPropertiesParser;
import com.edu.ufl.cise.cnt5106c.Configuration.PeerInfoConfigParser;
import com.edu.ufl.cise.cnt5106c.Peer.Peer;
import com.edu.ufl.cise.cnt5106c.Peer.PeerProcess;

public class OptimisticallyUnchokedNeighborThread implements Runnable {
	private Vector<Peer> interestedPeers;
	private PeerProcess peerProc;

	/**
	 * @param peerProc
	 */
	public OptimisticallyUnchokedNeighborThread(PeerProcess peerProc) {
		super();
		this.setPeerProc(peerProc);
		this.setInterestedPeers(new Vector<Peer>());
	}

	/**
	 * @return the interestedPeers
	 */
	public Vector<Peer> getInterestedPeers() {
		return interestedPeers;
	}

	/**
	 * @param interestedPeers
	 *            the interestedPeers to set
	 */
	public void setInterestedPeers(Vector<Peer> interestedPeers) {
		this.interestedPeers = interestedPeers;
	}

	/**
	 * @return the peerProcess
	 */
	public PeerProcess getPeerProc() {
		return peerProc;
	}

	/**
	 * @param peerProc
	 *            the peerProcess to set
	 */
	public void setPeerProc(PeerProcess peerProc) {
		this.peerProc = peerProc;
	}

	@Override
	public void run() {
		for (; !this.getPeerProc().exit;) {
			try {

				Thread.sleep(CommonPropertiesParser.getOptimisticUnchokingInterval() * 1000);

				Iterator<Peer> iteratorPeer = this.getPeerProc().peerSocketMap.keySet().iterator();
				while (iteratorPeer.hasNext()) {
					Peer p = iteratorPeer.next();
					if (!p.isInterestedInPieces()) {

					} else {
						this.getInterestedPeers().addElement(p);
					}
				}
				if (!(this.getInterestedPeers().size() > 0)) {

				} else {
					Random ran = new Random();
					if (!(this.getPeerProc().optimisticallyUnchokedNeighbor != null)) {

					} else {
						// check if not a preferred neighbor then only send choke message
						if (!(this.getPeerProc().PreferedNeighbours != null && !this.getPeerProc().PreferedNeighbours
								.contains(this.getPeerProc().optimisticallyUnchokedNeighbor))) {

						} else {
							// send a choke message to the previous neighbor
							this.getPeerProc().sendChokeMessage(
									new HashSet<>(Arrays.asList(this.getPeerProc().optimisticallyUnchokedNeighbor)));
						}
					}
					this.getPeerProc().optimisticallyUnchokedNeighbor = this.getInterestedPeers()
							.get(ran.nextInt(this.getInterestedPeers().size()));
					this.getPeerProc().sendUnChokeMessage(
							new HashSet<>(Arrays.asList(this.getPeerProc().optimisticallyUnchokedNeighbor)));
					this.getPeerProc().blockingQueueLogging.put(
							"Peer " + PeerInfoConfigParser.getCurrentPeer().getPeerID() + " has the optimistically unchoked neighbor "
									+ this.getPeerProc().optimisticallyUnchokedNeighbor.getPeerID() + ".");
				}

			} catch (Exception e) {
				e.printStackTrace();
				this.getPeerProc().exit = true;
				break;
			}
		}
	}
}
