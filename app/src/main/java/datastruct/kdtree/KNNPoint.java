package datastruct.kdtree;

import patterns.cluster.DoubleArray;

public class KNNPoint implements Comparable<KNNPoint>{
	
	public DoubleArray values;  // a vector
	public double distance; // a distance
	
	/**
	 * Constructor
	 * @param values  vector
	 * @param distance distance
	 */
	public KNNPoint(DoubleArray values, double distance){
		this.values = values;
		this.distance = distance;
	}

	/**
	 * Compare this point to another point
	 * @param point2 the given point.
	 * @return an integer <0 if the distance of this point is smaller than the distance of point2
	 *         an integer >0 if the distance of this point is larger than the one of point2
	 *         0 if the distance is the same.
	 */
	public int compareTo(KNNPoint point2) {
		return Double.compare(this.distance, point2.distance);
	}
	
	/**
	 * Return this point as a string.
	 * @return a string
	 */
	public String toString(){
		StringBuilder buffer = new StringBuilder();
		buffer.append("(");
		for(Double element : values.data ){
			buffer.append(" " + element);
		}
		buffer.append(")");
		return buffer.toString();
	}
	
	/**
	 * Verify if two points are equals.
	 * @param point2  a given point.
	 * @return true if equals to that point.
	 */
	public boolean equals(Object point2){
		if(point2 == null){
			return false;
		}
		KNNPoint o2 = (KNNPoint)point2;
		for(int i=0; i < values.size(); i++ ){
			if(o2.values.data[i] != values.data[i]){
				return false;
			}
		}
		return true;
	}
}
