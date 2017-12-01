
public class DownloadingRate {
	private Peer peer;
	private double downloadingRate;
	private PeerProcess peerProc;

	public DownloadingRate(PeerProcess peerProc, Peer peer, double downloadingRate) {
		super();
		this.setPeerProc(peerProc);
		this.setPeer(peer);
		this.setDownloadingRate(downloadingRate);
	}

	/**
	 * @return the peer
	 */
	public Peer getPeer() {
		return peer;
	}

	/**
	 * @param peer
	 *            the peer to set
	 */
	public void setPeer(Peer peer) {
		this.peer = peer;
	}

	/**
	 * @return the downloadingRate
	 */
	public double getDownloadingRate() {
		return downloadingRate;
	}

	/**
	 * @param downloadingRate
	 *            the downloadingRate to set
	 */
	public void setDownloadingRate(double downloadingRate) {
		this.downloadingRate = downloadingRate;
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getOuterType().hashCode();
		result = prime * result + ((this.getPeer() == null) ? 0 : this.getPeer().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DownloadingRate other = (DownloadingRate) obj;
		if (!getOuterType().equals(other.getOuterType()))
			return false;
		if (this.getPeer() == null) {
			if (other.getPeer() != null)
				return false;
		} else if (!this.getPeer().equals(other.getPeer()))
			return false;
		return true;
	}

	private PeerProcess getOuterType() {
		return this.getPeerProc();
	}
}