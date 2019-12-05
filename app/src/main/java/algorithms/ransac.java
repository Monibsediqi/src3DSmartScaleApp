package algorithms;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import geometrypack.Plane;
import geometrypack.Vector;

public class ransac {
	
	private List<Vector> PointCloud;
	private int iterations;
	private int numberSample;
	private double thresh;
	private int sufficientsupport;
	public ransac (List<Vector> PointCloud,double thresh,int iterations, int numberSample,int sufficientsupport) {
		this.PointCloud = PointCloud;
		this.iterations = iterations;
		this.numberSample = numberSample;
		this.thresh = thresh;
		this.sufficientsupport = sufficientsupport;
	}
	public Support detect_plane() {
		int supportMax = 0;
		int numPoints = PointCloud.size();
		int [] thisSample = new int[3];
        boolean[] picked = new boolean[numPoints];

		Support support = new Support();
		for(int i=0; i<iterations;i++) {
			for (int j=0;j<numberSample;j++) {
				thisSample = sample (numPoints,picked);
				Support spt = computePlane(PointCloud,thisSample, thresh);
				if (spt.points.size()>supportMax) {
					supportMax = spt.points.size();
					support = spt;
				}
				if (supportMax >= sufficientsupport) {
					break;
				}
			}
		}
		
		return support ;
	}
	
	private int[] sample(int CloudSize, boolean []picked) {
			int[] p = new int[3];
            //
            // (1) pick 3 mutually different points ----------------------------
            for (int i = 0; i < 3; i++) {
                do {
                    p[i] = (int) (Math.random() * CloudSize);
                } while (picked[p[i]]);
                picked[p[i]] = true;
            }
            picked[p[0]] = false;
            picked[p[1]] = false;
            picked[p[2]] = false;
		return p;
	}

	private Support computePlane(List<Vector> Cloud, int [] sample_idx,double thresh) {
		
		List <Vector> points = new ArrayList<Vector>();
		LinkedList<Vector> pts = new LinkedList<Vector>();
		for (int i =0; i<3;++i)
			points.add(i, Cloud.get(sample_idx[i]));
		Plane plane = createPlane(points);
		for (Vector point : Cloud) {
			float distanceToPlane = plane.distanceTo(point);
			if(Math.abs(distanceToPlane)<=thresh)
				pts.add(point);
		}
		return new Support(plane,pts);
	}
	
	private Vector computeNormal(List<Vector> pts) {
		Vector N = new Vector(0,0,0);
		if (pts.size()==3) {
			Vector AB = pts.get(1).subtract(pts.get(0));
			Vector AC = pts.get(2).subtract(pts.get(0));
			N = AB.cross(AC);
			N = N.normalize();
		}
		return N;
	}
	private Plane createPlane(List <Vector> points) {
		Vector normal  = computeNormal (points);
		Vector pointref = points.get(0);

		return new Plane(normal,pointref);
	}
	
	public class Support{
		private Plane plane;
		public LinkedList<Vector> points;
		public Support() {}
		public Support (Plane plane,LinkedList<Vector> points) {
			this.setPlane(plane);
			this.points = points;
		}
		public Plane getPlane() {
			return plane;
		}
		public void setPlane(Plane plane) {
			this.plane = plane;
		}
	}
}
