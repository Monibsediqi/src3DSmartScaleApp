package algorithms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import datastruct.kdtree.KDTree;
import geometrypack.Vector;
import patterns.cluster.DoubleArray;

public class Dbscan {
	private List<Vector> cloud;
	private Set<DoubleArray> visited;
	private int minPts;
	private double eps;
	private KDTree kdtree = new KDTree();
	private List<DoubleArray> pts;
	public Dbscan (List<Vector> cloud,int minPts, double epsilon) {
		visited = new HashSet<>();		
		this.cloud = cloud;
		this.minPts = minPts;
		this.eps = epsilon;
		pts = new ArrayList<DoubleArray>();
		for (Vector pt:cloud)
			pts.add(new DoubleArray(new double[]{pt.getX(),pt.getY(),pt.getZ()}));
		kdtree.buildtree(pts);
	}
	public ArrayList<List<DoubleArray>> Run() {
		int index = cloud.size()/2;
		List <DoubleArray> neighbors;
		List<DoubleArray> individualNeighbors;
		ArrayList<List<DoubleArray>> resultList = new ArrayList<List<DoubleArray>>();
		while (pts.size() > index) {
			DoubleArray p = pts.get(index);
			if (!visited.contains(p)) {
				visited.add(p);
				neighbors = get_neighbors(p);
				if (neighbors.size() >= minPts-1) {
					int ind = 0;
					while (neighbors.size() > ind) {
						DoubleArray r = neighbors.get(ind);
						if (!visited.contains(r)) {
							visited.add(r);
							individualNeighbors = get_neighbors(r);
							if (individualNeighbors.size() >= minPts-1) {
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
		
	private List<DoubleArray> merge_neighbors(List<DoubleArray>neighborPts1, List<DoubleArray>neighborPts2) {
		for (DoubleArray n2: neighborPts2) {
			if (!neighborPts1.contains(n2)) {
				neighborPts1.add(n2);
			}
		}
		return neighborPts1;
	}
	
	private List<DoubleArray> get_neighbors(DoubleArray pt){
		List<DoubleArray> Pts = kdtree.pointsWithinRadiusOf(pt, eps);
		return Pts;
	}
}
