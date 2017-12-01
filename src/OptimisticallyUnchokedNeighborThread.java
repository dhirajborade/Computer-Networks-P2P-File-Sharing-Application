import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class OptimisticallyUnchokedNeighborThread implements Runnable {
	private Vector<Peer> interestedPeers;
	private PeerProcess peerProcess;

	/**
	 * @param peerProcess
	 */
	public OptimisticallyUnchokedNeighborThread(PeerProcess peerProcess) {
		super();
		this.setPeerProcess(peerProcess);
		interestedPeers = new Vector<>();
	}

	/**
	 * @return the interestedPeers
	 */
	public List<Peer> getInterestedPeers() {
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
		for (; !peerProcess.exit;) {
			try {

				Thread.sleep(CommonPropertiesParser.getOptimisticUnchokingInterval() * 1000);

				Iterator<Peer> iter = peerProcess.peerSocketMap.keySet().iterator();
				while (iter.hasNext()) {
					Peer p = iter.next();
					if (!p.isInterestedInPieces()) {

					} else {
						interestedPeers.addElement(p);
					}
				}
				if (!(interestedPeers.size() > 0)) {

				} else {
					Random ran = new Random();
					if (!(peerProcess.optimisticallyUnchokedNeighbor != null)) {

					} else {
						// check if not a preferred neighbor then only send choke message
						if (peerProcess.PreferedNeighbours != null && !peerProcess.PreferedNeighbours
								.contains(peerProcess.optimisticallyUnchokedNeighbor)) {

						} else {
							// send a choke message to the previous neighbor
							peerProcess.sendChokeMessage(
									new HashSet<>(Arrays.asList(peerProcess.optimisticallyUnchokedNeighbor)));
						}
					}
					peerProcess.optimisticallyUnchokedNeighbor = interestedPeers
							.get(ran.nextInt(interestedPeers.size()));
					peerProcess.sendUnChokeMessage(
							new HashSet<>(Arrays.asList(peerProcess.optimisticallyUnchokedNeighbor)));
					peerProcess.blockingQueueLogWriter.put(
							"Peer " + PeerProcess.currentPeer.getPeerID() + " has the optimistically unchoked neighbor "
									+ peerProcess.optimisticallyUnchokedNeighbor.getPeerID() + ".");
				}

			} catch (Exception e) {
				e.printStackTrace();
				peerProcess.exit = true;
				break;
			}
		}
	}
}
