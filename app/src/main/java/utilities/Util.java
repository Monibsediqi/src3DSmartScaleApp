package utilities;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import geometrypack.Vector;

public class Util {
	
	private List<Vector> cloud;
	
	public Util() {
		
	}
	public Util(List<Vector> cloud) {
		this.cloud = cloud;
	}
	protected void averageX() {
		float s =0;
		for(Vector v: cloud)
			s = (s + v.getX())/cloud.size();
		for(Vector v: cloud)
			v.setZ(s);
	}
	protected void averageY() {
		float s =0;
		for(Vector v: cloud)
			s = (s + v.getY())/cloud.size();
		for(Vector v: cloud)
			v.setZ(s);
	}
	protected void averageZ() {
		float s =0;
		for(Vector v: cloud)
			s = (s + v.getZ())/cloud.size();
		for(Vector v: cloud)
			v.setZ(s);
	}
	public Vector getMinZ() {
		Vector minVector = new Vector(0, 0, 5000);
		for(Vector v: cloud) {
			if (v.getZ()<minVector.getZ())
				minVector = v;
		}
		return minVector;
	}
	public Vector getMaxZ() {
		Vector maxVector = new Vector(0, 0, -5000);
		for(Vector v: cloud) {
			if (v.getZ()>maxVector.getZ())
				maxVector = v;
		}
		return maxVector;
	}
	public Vector getMinY() {
		Vector minVector = new Vector(0, 5000, 0);
		for(Vector v: cloud) {
			if (v.getY()<minVector.getY())
				minVector = v;
		}
		return minVector;
	}
	
	public Vector getMaxY() {
		Vector maxVector = new Vector(0, -5000, 0);
		for(Vector v: cloud) {
			if (v.getY()>maxVector.getY())
				maxVector = v;
		}
		return maxVector;
	}
	public Vector getMinX() {
		Vector minVector = new Vector(5000, 0, 0);
		for(Vector v: cloud) {
			if (v.getX()<minVector.getX())
				minVector = v;
		}
		return minVector;
	}
	
	public Vector getMaxX() {
		Vector maxVector = new Vector(-5000
				, 0, 0);
		for(Vector v: cloud) {
			if (v.getX()>maxVector.getX())
				maxVector = v;
		}
		return maxVector;
	}
	public void orderZ() {
		
		Collections.sort(cloud,new SortbyZ());
	}
	protected void orderY() {
		
		Collections.sort(cloud,new SortbyY());
	}
	protected void orderX() {
		
		Collections.sort(cloud,new SortbyX());
	}
	
	
	protected void smoothing () {
		int n_neighbors =35;
		for (int i=0;i<cloud.size();i++) {
			if (i+n_neighbors<cloud.size()) {
				for (int j=0;j<n_neighbors;++j) {
					cloud.get(i).setX(cloud.get(i).getX()+cloud.get(i+j+1).getX());
					cloud.get(i).setY(cloud.get(i).getY()+cloud.get(i+j+1).getY());
				}
				cloud.get(i).setX(cloud.get(i).getX()/n_neighbors);
				cloud.get(i).setY(cloud.get(i).getY()/n_neighbors);
			}
			else {
				for (int j=0;j<n_neighbors;++j) {
					cloud.get(i).setX(cloud.get(i).getX()+cloud.get(i-j-1).getX());
					cloud.get(i).setY(cloud.get(i).getY()+cloud.get(i-j-1).getY());
				}
				cloud.get(i).setX(cloud.get(i).getX()/n_neighbors);
				cloud.get(i).setY(cloud.get(i).getY()/n_neighbors);
			}
		}
	}
	class SortbyX implements Comparator<Vector> 
	{ 
	    // Used for sorting in ascending order of 
	    // roll number 
	    public int compare(Vector a, Vector b) 
	    { 
	        return (int) (a.getX() - b.getX()); 
	    } 
	}
	class SortbyZ implements Comparator<Vector> 
	{ 
	    // Used for sorting in ascending order of 
	    public int compare(Vector a, Vector b) 
	    { 
	        return (int) (a.getZ() - b.getZ()); 
	    } 
	}
	class SortbyY implements Comparator<Vector> 
	{ 
	    // Used for sorting in ascending order of 
	    // roll number 
	    public int compare(Vector a, Vector b) 
	    { 
	        return (int) (a.getY() - b.getY()); 
	    } 
	} 
}
