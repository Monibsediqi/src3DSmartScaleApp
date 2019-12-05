package patterns.cluster;

import java.util.ArrayList;
import java.util.List;


public class Cluster {

	protected List<DoubleArray> vectors = new ArrayList<DoubleArray>();

	public Cluster() {
		super();
	}

	/**
	 * Add a vector of doubles to this cluster.
	 * @param vector The vector of doubles to be added.
	 */
	public void addVector(DoubleArray vector) {
		vectors.add(vector);
	}

	/**
	 * Return a string representing this cluster.
	 */
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		if(vectors.size() >=1){
			for(DoubleArray vector : vectors){
				buffer.append("[");
				buffer.append(vector.toString());
				buffer.append("]");
			}
		}
		return buffer.toString();
	}

	/**
	 * Method to get the vectors in this cluster
	 * @return the vectors.
	 */
	public List<DoubleArray> getVectors() {
		return vectors;
	}

	/**
	 * Method to remove a vector from this cluster and update
	 * the internal sum of vectors at the same time.
	 * @param vector  the vector to be removed
	 */
	public void remove(DoubleArray vector) {
		vectors.remove(vector);		
	}

//	/**
//	 * Method to remove a vector from this cluster without updating internal
//	 * structures.
//	 * @param vector  the vector to be removed
//	 */
//	public void removeVector(DoubleArray vector) {
//		vectors.remove(vector);
//	}

	/**
	 * Check if a vector is contained in this cluster.
	 * @param vector A vector of doubles
	 * @return true if the vector is contained in this cluster.
	 */
	public boolean contains(DoubleArray vector) {
		return vectors.contains(vector);
	}

}