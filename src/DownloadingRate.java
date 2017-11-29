
public class DownloadingRate {
	Peer p;
	double downloadingRate;
	PeerProcess peerProc;

	public DownloadingRate(PeerProcess peerProc, Peer p, double downloadingRate) {
		super();
		this.peerProc = peerProc;
		this.p = p;
		this.downloadingRate = downloadingRate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getOuterType().hashCode();
		result = prime * result + ((p == null) ? 0 : p.hashCode());
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
		if (p == null) {
			if (other.p != null)
				return false;
		} else if (!p.equals(other.p))
			return false;
		return true;
	}

	private PeerProcess getOuterType() {
		return this.peerProc;
	}
}