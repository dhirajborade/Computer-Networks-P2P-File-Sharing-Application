import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

public class PrefferedNeighborsThread implements Runnable {

	private PeerProcess peerProc;

	public PrefferedNeighborsThread(PeerProcess peerProc) {
		super();
		this.setPeerProc(peerProc);
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

	@Override
	public void run() {
		for (; !this.getPeerProc().exit;) {
			try {
				Thread.sleep(CommonPropertiesParser.getUnchokingInterval() * 1000);
				if (!(this.getPeerProc().peerInfoVector.size() > 0)) {

				} else {
					this.getPeerProc().NewPrefNeighbors = new HashSet<Peer>();
					if (this.getPeerProc().unchokingIntervalWisePeerDownloadingRate.size() == 0) {

						// as it is a new arraylist, this thread is run for the first time
						// so we do not have previous unchoking interval available
						// thus select any random peers and add them to the preferred neighbors list
						Random ran = new Random();
						int indexI = 0;
						while (indexI < this.getPeerProc().peerInfoVector.size() && this.getPeerProc().NewPrefNeighbors
								.size() < CommonPropertiesParser.getNumberOfPreferredNeighbors()) {
							Peer p = this.getPeerProc().peerInfoVector
									.get(ran.nextInt(this.getPeerProc().peerInfoVector.size()));
							if (p.isInterestedInPieces()) {
								this.getPeerProc().NewPrefNeighbors.add(p);
							}
							indexI++;
						}

					} else {
						// select top NumberOfPrefferedNeighbors and update the preferred neighbors
						// lists
						Random ran = new Random();
						int indexI = 0;
						while (indexI < CommonPropertiesParser.getNumberOfPreferredNeighbors()) {
							if (!this.getPeerProc().unchokingIntervalWisePeerDownloadingRate.isEmpty()) {
								this.getPeerProc().NewPrefNeighbors.add(
										this.getPeerProc().unchokingIntervalWisePeerDownloadingRate.poll().getPeer());
							}
							indexI++;
						}
						// if the previous downloading rates list is less than preferred neighbors size

						int indexJ = 0;
						while (indexJ < this.getPeerProc().peerInfoVector.size() && this.getPeerProc().NewPrefNeighbors
								.size() < CommonPropertiesParser.getNumberOfPreferredNeighbors()) {
							Peer p = this.getPeerProc().peerInfoVector
									.get(ran.nextInt(this.getPeerProc().peerInfoVector.size()));
							if (p.isInterestedInPieces()) {
								this.getPeerProc().NewPrefNeighbors.add(p);
							}
							indexJ++;
						}
					}
					if (!(this.getPeerProc().NewPrefNeighbors.size() > 0)) {

					} else {
						// send unchoke only to the new ones
						this.getPeerProc().sendUnchokePrefNeig = new HashSet<>();
						// deep copying list
						if (!(this.getPeerProc().PreferedNeighbours == null)) {

						} else {
							this.getPeerProc().PreferedNeighbours = new HashSet<>();
						}
						Iterator<Peer> iteratorPeerA = this.getPeerProc().NewPrefNeighbors.iterator();
						while (iteratorPeerA.hasNext()) {
							Peer p = iteratorPeerA.next();
							if (this.getPeerProc().PreferedNeighbours.contains(p)) {

							} else {
								this.getPeerProc().sendUnchokePrefNeig.add(p);
							}
						}
						this.getPeerProc().sendUnchokePrefNeig.removeAll(this.getPeerProc().PreferedNeighbours);
						// send choke messages to other who are not present in the new list of preferred
						// neighbors
						this.getPeerProc().PreferedNeighbours.removeAll(this.getPeerProc().NewPrefNeighbors);
						this.getPeerProc().sendChokeMessage(this.getPeerProc().PreferedNeighbours);
						this.getPeerProc().sendUnChokeMessage(new HashSet<>(this.getPeerProc().sendUnchokePrefNeig));
						this.getPeerProc().PreferedNeighbours = this.getPeerProc().NewPrefNeighbors;

						String peerIdList = "";

						Iterator<Peer> iteratorPeerB = this.getPeerProc().PreferedNeighbours.iterator();
						while (iteratorPeerB.hasNext()) {
							Peer p = iteratorPeerB.next();
							peerIdList = p.getPeerID() + ",";
						}
						this.getPeerProc().blockingQueueLogging
								.put("Peer " + PeerProcess.currentPeer.getPeerID() + " has the preferred neighbors "
										+ peerIdList.substring(0, peerIdList.length() - 1) + ".");
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				this.getPeerProc().exit = true;
				break;
			}
		}
	}

}