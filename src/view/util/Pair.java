package view.util;

/**
 * A Generic immutable pair of objects 
 * @author Bilal Hussain
 */
public class Pair<L, R> {

	public final L left;
	public final R right;

	public Pair(L left, R right) {
		this.left  = left;
		this.right = right;
	}

	/**
	 * Creates a new immutable pair
	 * @return a immutable pair
	 */
    public static <L, R> Pair<L, R> of(L left, R right) {
        return new Pair<L, R>(left, right);
    }
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof Pair)) return false;
		Pair other = (Pair) obj;
		if (left == null) {
			if (other.left != null) return false;
		} else if (!left.equals(other.left)) return false;
		if (right == null) {
			if (other.right != null) return false;
		} else if (!right.equals(other.right)) return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("Pair [left=%s, right=%s]", left, right);
	}

}
