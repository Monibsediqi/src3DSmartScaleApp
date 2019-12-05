package patterns.cluster;


public class DoubleArray {

	// the vector
	public double[] data;
	
	/**
	 * Constructor
	 * @param data an array of double values
	 */
	public DoubleArray(double [] data){
		this.data = data;
	}
	
	/**
	 * Get a string representation of this double array.
	 * @return a string
	 */
	public String toString(){
		StringBuilder buffer = new StringBuilder();
		for(int i=0; i<data.length; i++){
			buffer.append(data[i]);
			if(i < data.length -1){
				buffer.append(",");
			}
		}
		return buffer.toString();
	}
	
	/**
	 * Return a copy of this double array
	 */
	public DoubleArray clone(){
		return new DoubleArray(data.clone());
	}
	
	/**
	 * return the size of this double array
	 * @return the size (int)
	 */
	public int size() {
		return data.length;
	}
	
	/**
	 * Return the double at a given index position
	 * @param index the index
	 * @return the double value
	 */
	public double get(int index) {
		return data[index];
	}
}
