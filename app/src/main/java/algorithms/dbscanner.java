package algorithms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import geometrypack.Vector;

public class dbscanner {
	private List<Vector> cloud;
	private Set<Vector> visited;
	private int minPts;
	private double eps;
	
	
	
	public dbscanner (List<Vector> cloud,int minPts, double epsilon) {
		visited = new HashSet<>();		
		this.cloud = cloud;
		this.minPts = minPts;
		this.eps = epsilon;
	}
	
	public ArrayList<List<Vector>> Run() {
		int index = cloud.size()/2;
		List <Vector> neighbors;
		ArrayList<List<Vector>> resultList = new ArrayList<List<Vector>>();
		while (cloud.size() > index) {
			Vector p = cloud.get(index);
			if (!visited.contains(p)) {
				visited.add(p);
				neighbors = get_neighbors(p);
				if (neighbors.size() >= minPts) {
					int ind = 0;
					while (neighbors.size() > ind) {
						Vector r = neighbors.get(ind);
						if (!visited.contains(r)) {
							visited.add(r);
							List<Vector> individualNeighbors = get_neighbors(r);
							if (individualNeighbors.size() >= minPts) {
								neighbors = merge_neighbors(
										neighbors,
										individualNeighbors);
							}
						}
						ind++;
					}
					resultList.add(neighbors);
				}
			}
			index++;
		}
		return resultList;
	}
		
	private List<Vector> merge_neighbors(List<Vector>neighborPts1, List<Vector>neighborPts2) {
		for (Vector n2: neighborPts2) {
			if (!neighborPts1.contains(n2)) {
				neighborPts1.add(n2);
			}
		}
		return neighborPts1;
	}
	
	private List<Vector> get_neighbors(Vector pt){
		CopyOnWriteArrayList<Vector> pts = new  CopyOnWriteArrayList<>();
		for (Vector p: cloud) {
				if (computeDistance (pt,p)<=eps*eps) {
					pts.add(p);
			}
		}
		return pts;
	}
	private double computeDistance (Vector core,Vector target) {
		return Math.pow(core.getX()-target.getX(),2)
				+ Math.pow(core.getY()-target.getY(),2)
				+Math.pow(core.getZ()-target.getZ(),2);
	}		
	}
