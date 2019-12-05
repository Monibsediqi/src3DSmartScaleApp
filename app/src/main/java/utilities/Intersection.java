package utilities;

import java.util.List;

import geometrypack.Vector;

public class Intersection extends Util{
	private List<Vector> intersection;
	private Vector maxpt;
	private Vector minpt;
	public Intersection() {
		
	}
	public Intersection(List<Vector> intersection) {
		super(intersection);
		setIntersection(intersection);
		setMaxpt(getMaxZ());
		setMinpt(getMinZ());
	}

	public List<Vector> getIntersection() {
		return intersection;
	}

	public void setIntersection(List<Vector> intersection) {
		this.intersection = intersection;
	}

	public Vector getMinpt() {
		return minpt;
	}

	public void setMinpt(Vector minpt) {
		this.minpt = minpt;
	}

	public Vector getMaxpt() {
		return maxpt;
	}

	public void setMaxpt(Vector maxpt) {
		this.maxpt = maxpt;
	}


}
