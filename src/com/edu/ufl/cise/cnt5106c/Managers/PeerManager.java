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

/**
 *
 */

/**
 * @author dhirajborade
 *
 */
public class PeerManager implements Runnable {

	private long unchokingIntervalTimeout;
	private long optimisticUnchokingInterval;
	private Vector<Peer> interestedPeers;
	private PeerProcess peerProc;
	public HashSet<Peer> preferredNeighbors;
	public HashSet<Peer> newPreferredNeighbors;
	public HashSet<Peer> sendUnchokePreferredNeighbors;
	public Peer optimisticallyUnchokedNeighbor;

	/**
	 *
	 */
	public PeerManager(PeerProcess peerProc) {
		// TODO Auto-generated constructor stub
		super();
		this.setUnchokingIntervalTimeout(CommonPropertiesParser.getUnchokingInterval() * 1000);
		this.setOptimisticUnchokingInterval(CommonPropertiesParser.getOptimisticUnchokingInterval() * 1000);
		this.setPeerProc(peerProc);
		this.setInterestedPeers(new Vector<Peer>());
	}

	/**
	 * @return the unchokingIntervalTimeout
	 */
	public long getUnchokingIntervalTimeout() {
		return unchokingIntervalTimeout;
	}

	/**
	 * @param unchokingIntervalTimeout
	 *            the unchokingIntervalTimeout to set
	 */
	public void setUnchokingIntervalTimeout(long unchokingIntervalTimeout) {
		this.unchokingIntervalTimeout = unchokingIntervalTimeout;
	}

	/**
	 * @return the optimisticUnchokingInterval
	 */
	public long getOptimisticUnchokingInterval() {
		return optimisticUnchokingInterval;
	}

	/**
	 * @param optimisticUnchokingInterval
	 *            the optimisticUnchokingInterval to set
	 */
	public void setOptimisticUnchokingInterval(long optimisticUnchokingInterval) {
		this.optimisticUnchokingInterval = optimisticUnchokingInterval;
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
	 * @return the peerProc
	 */
	public PeerProcess getPeerProc() {
		return peerProc;
	}

	/**
	 * @param peerProc
	 *            the peerProc to set
	 */
	public void setPeerProc(PeerProcess peerProc) {
		this.peerProc = peerProc;
	}

	private void findOptimisticallyUnchokedNeighbor(PeerProcess peerProc, Vector<Peer> interestedPeers,
			long optimisticUnchokingIntervalTimeout) {
		// TODO Auto-generated method stub
		new Thread() {
			public void run() {
				for (; !peerProc.isExit();) {
					try {

						Thread.sleep(CommonPropertiesParser.getOptimisticUnchokingInterval() * 1000);

						Iterator<Peer> iteratorPeer = peerProc.getPeerSocketMap().keySet().iterator();
						while (iteratorPeer.hasNext()) {
							Peer p = iteratorPeer.next();
							if (!p.isInterestedInPieces()) {

							} else {
								interestedPeers.addElement(p);
							}
						}
						if (!(interestedPeers.size() > 0)) {

						} else {
							Random ran = new Random();
							if (!(optimisticallyUnchokedNeighbor != null)) {

							} else {
								// check if not a preferred neighbor then only send choke message
								if (!(preferredNeighbors != null
										&& !preferredNeighbors.contains(optimisticallyUnchokedNeighbor))) {

								} else {
									// send a choke message to the previous neighbor
									peerProc.sendChokeMessage(
											new HashSet<>(Arrays.asList(optimisticallyUnchokedNeighbor)));
								}
							}
							optimisticallyUnchokedNeighbor = interestedPeers.get(ran.nextInt(interestedPeers.size()));
							peerProc.sendUnChokeMessage(new HashSet<>(Arrays.asList(optimisticallyUnchokedNeighbor)));
							peerProc.getBlockingQueueLogging()
									.put("Peer " + PeerInfoConfigParser.getCurrentPeer().getPeerID()
											+ " has the optimistically unchoked neighbor "
											+ optimisticallyUnchokedNeighbor.getPeerID() + ".");
						}

					} catch (Exception e) {
						e.printStackTrace();
						peerProc.setExit(true);
						break;
					}
				}
			}
		}.start();
	}

	private void findPreferredNeighbors(PeerProcess peerProc, long unchokingIntervalTimeout) {
		// TODO Auto-generated method stub
		new Thread() {
			public void run() {
				for (; !peerProc.isExit();) {
					try {
						Thread.sleep(CommonPropertiesParser.getUnchokingInterval() * 1000);
						if (!(peerProc.getPeerInfoVector().size() > 0)) {

						} else {
							newPreferredNeighbors = new HashSet<Peer>();
							if (peerProc.getUnchokingIntervalWisePeerDownloadingRate().size() == 0) {

								// as it is a new arraylist, this thread is run for the first time
								// so we do not have previous unchoking interval available
								// thus select any random peers and add them to the preferred neighbors list
								Random ran = new Random();
								int indexI = 0;
								while (indexI < peerProc.getPeerInfoVector().size() && newPreferredNeighbors
										.size() < CommonPropertiesParser.getNumberOfPreferredNeighbors()) {
									Peer p = peerProc.getPeerInfoVector().get(ran.nextInt(peerProc.getPeerInfoVector().size()));
									if (p.isInterestedInPieces()) {
										newPreferredNeighbors.add(p);
									}
									indexI++;
								}

							} else {
								// select top NumberOfPrefferedNeighbors and update the preferred neighbors
								// lists
								Random ran = new Random();
								int indexI = 0;
								while (indexI < CommonPropertiesParser.getNumberOfPreferredNeighbors()) {
									if (!peerProc.getUnchokingIntervalWisePeerDownloadingRate().isEmpty()) {
										newPreferredNeighbors.add(
												peerProc.getUnchokingIntervalWisePeerDownloadingRate().poll().getPeer());
									}
									indexI++;
								}
								// if the previous downloading rates list is less than preferred neighbors size

								int indexJ = 0;
								while (indexJ < peerProc.getPeerInfoVector().size() && newPreferredNeighbors
										.size() < CommonPropertiesParser.getNumberOfPreferredNeighbors()) {
									Peer p = peerProc.getPeerInfoVector().get(ran.nextInt(peerProc.getPeerInfoVector().size()));
									if (p.isInterestedInPieces()) {
										newPreferredNeighbors.add(p);
									}
									indexJ++;
								}
							}
							if (!(newPreferredNeighbors.size() > 0)) {

							} else {
								// send unchoke only to the new ones
								sendUnchokePreferredNeighbors = new HashSet<>();
								// deep copying list
								if (!(preferredNeighbors == null)) {

								} else {
									preferredNeighbors = new HashSet<>();
								}
								Iterator<Peer> iteratorPeerA = newPreferredNeighbors.iterator();
								while (iteratorPeerA.hasNext()) {
									Peer p = iteratorPeerA.next();
									if (preferredNeighbors.contains(p)) {

									} else {
										sendUnchokePreferredNeighbors.add(p);
									}
								}
								sendUnchokePreferredNeighbors.removeAll(preferredNeighbors);
								// send choke messages to other who are not present in the new list of preferred
								// neighbors
								preferredNeighbors.removeAll(newPreferredNeighbors);
								peerProc.sendChokeMessage(preferredNeighbors);
								peerProc.sendUnChokeMessage(new HashSet<>(sendUnchokePreferredNeighbors));
								preferredNeighbors = newPreferredNeighbors;

								String peerIdList = "";

								Iterator<Peer> iteratorPeerB = preferredNeighbors.iterator();
								while (iteratorPeerB.hasNext()) {
									Peer p = iteratorPeerB.next();
									peerIdList = p.getPeerID() + ",";
								}
								peerProc.getBlockingQueueLogging()
										.put("Peer " + PeerInfoConfigParser.getCurrentPeer().getPeerID()
												+ " has the preferred neighbors "
												+ peerIdList.substring(0, peerIdList.length() - 1) + ".");
							}
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
						peerProc.setExit(true);
						break;
					}
				}
			}
		}.start();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		findPreferredNeighbors(this.getPeerProc(), this.getUnchokingIntervalTimeout());
		findOptimisticallyUnchokedNeighbor(this.getPeerProc(), this.getInterestedPeers(),
				this.getOptimisticUnchokingInterval());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
