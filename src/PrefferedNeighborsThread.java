import java.util.HashSet;
import java.util.Random;

public class PrefferedNeighborsThread implements Runnable {

	PeerProcess peerProces;

	public PrefferedNeighborsThread(PeerProcess peerProces) {
		super();
		this.peerProces = peerProces;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while (!peerProces.exit) {
			try {

				Thread.sleep(peerProces.UnchokingInterval * 1000);
				if (peerProces.peerInfoVector.size() > 0) {
					peerProces.NewPrefNeighbors = new HashSet<Peer>();
					if (peerProces.unchokingIntervalWisePeerDownloadingRate.size() == 0) {

						// as it is a new arraylist, this thread is run for
						// the
						// first time
						// so we do not have previous unchoking interval
						// available
						// thus select any random peers and add them to the
						// preferred neighbors list
						Random ran = new Random();
						for (int i = 0; i < peerProces.peerInfoVector.size()
								&& peerProces.NewPrefNeighbors.size() < peerProces.NumberOfPreferredNeighbors; i++) {
							Peer p = peerProces.peerInfoVector.get(ran.nextInt(peerProces.peerInfoVector.size()));
							if (p.isInterestedInPieces()) {
								peerProces.NewPrefNeighbors.add(p);
							}
						}

					} else {
						// send unchoke

						// select top NumberOfPrefferedNeighbors and update
						// the
						// preferred neoighbors lists
						Random ran = new Random();
						for (int i = 0; i < peerProces.NumberOfPreferredNeighbors; i++) {
							if (!peerProces.unchokingIntervalWisePeerDownloadingRate.isEmpty()) {
								peerProces.NewPrefNeighbors
										.add(peerProces.unchokingIntervalWisePeerDownloadingRate.poll().p);
							}
						}
						// if the previous downloading rates list is less
						// than
						// preffered neighbors size

						for (int i = 0; i < peerProces.peerInfoVector.size()
								&& peerProces.NewPrefNeighbors.size() < peerProces.NumberOfPreferredNeighbors; i++) {
							Peer p = peerProces.peerInfoVector.get(ran.nextInt(peerProces.peerInfoVector.size()));
							if (p.isInterestedInPieces())
								peerProces.NewPrefNeighbors.add(p);

						}
					}
					if (peerProces.NewPrefNeighbors.size() > 0) {

						// send unchoke only to the new ones
						peerProces.sendUnchokePrefNeig = new HashSet<>();
						// deep copying list
						if (peerProces.PreferedNeighbours == null) {
							peerProces.PreferedNeighbours = new HashSet<>();
						}
						for (Peer p : peerProces.NewPrefNeighbors) {
							if (!peerProces.PreferedNeighbours.contains(p)) {
								peerProces.sendUnchokePrefNeig.add(p);
							}
						}
						peerProces.sendUnchokePrefNeig.removeAll(peerProces.PreferedNeighbours);

						// send choke messages to other who are not present
						// in
						// the
						// new list of preferred neighbors
						peerProces.PreferedNeighbours.removeAll(peerProces.NewPrefNeighbors);

						peerProces.sendChokeMessage(peerProces.PreferedNeighbours);

						peerProces.sendUnChokeMessage(new HashSet<>(peerProces.sendUnchokePrefNeig));
						peerProces.PreferedNeighbours = peerProces.NewPrefNeighbors;

						String peerIdList = "";
						for (Peer p : peerProces.PreferedNeighbours) {
							peerIdList = p.getPeerID() + ",";
						}
						peerProces.bql.put("Peer " + peerProces.currentPeer.getPeerID() + " has the preferred neighbors "
								+ peerIdList.substring(0, peerIdList.length() - 1) + ".");
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				peerProces.exit = true;
				break;
			}

		}

	}

}