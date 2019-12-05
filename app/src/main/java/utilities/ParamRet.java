package utilities;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import geometrypack.Plane;
import geometrypack.Vector;

public class ParamRet {
	
	private List<Vector> PointCloud;
	public ParamRet(List<Vector> PointCloud) {
		this.PointCloud = PointCloud;
	}

	
	
	//If the head is at the left side the sweeping will be from right to left.
	public Intersection Find_inter(Vector pt){ // intersection of the plane with normal k = (0,0,1) and ref point pt
		List<Vector> intersection = new ArrayList<>();
		Vector normal = new Vector(0f, 0f,1f);
		Plane plane= new Plane(normal, pt);
		for (Vector point : PointCloud) {
			float distanceToPlane = plane.distanceTo(point);
			if(Math.abs(distanceToPlane)<=20)
				intersection.add(point);
		}
		Intersection inter = new Intersection(intersection);
		//Average Y ??? 
		return inter;
	}
	public List<Intersection> find_intersections(Vector startPt, Vector stopPt){
		List<Intersection> intersections = new ArrayList<>();
		Intersection inter = Find_inter(startPt);
		intersections.add(inter);
		while(!inter.getMaxpt().equals(stopPt)) {
			inter = Find_inter(inter.getMaxpt());
			intersections.add(inter);
		}
		
		return intersections;
	}
	public Intersection Find_inter2(Vector pt){ // intersection of the plane with normal k = (0,0,1) and ref point pt
		List<Vector> intersection = new ArrayList<>();
		Vector normal = new Vector(0f, 0f,-1f);
		Plane plane= new Plane(normal, pt);
		for (Vector point : PointCloud) {
			float distanceToPlane = plane.distanceTo(point);
			if(Math.abs(distanceToPlane)<=20)
				intersection.add(point);
		}
		Intersection inter = new Intersection(intersection);
		//Average Y ??? 
		return inter;
	}
	public List<Intersection> find_intersections2(Vector startPt, Vector stopPt){
		List<Intersection> intersections = new ArrayList<>();
		Intersection inter = Find_inter2(startPt);
		intersections.add(inter);
		while(!inter.getMinpt().equals(stopPt)) {
			inter = Find_inter(inter.getMinpt());
			intersections.add(inter);
		}
		
		return intersections;
	}
	
	
	
	
	//To find the intersections we start from the extreme points of the 3D shape 
	//For each intersection the band of intersection is 2cm
	//The sweeping algorithm sweeps the point cloud
	//---> step scans the 
	
	public Intersection sweep(boolean headLeft) {
		Util util = new Util(PointCloud);
		Vector origin = new Vector (0,0,0);
		List <Intersection> intersections = new ArrayList<>();
		if(headLeft) {
			intersections = find_intersections(origin,util.getMaxZ());
		}
		else {
			intersections = find_intersections2(origin,util.getMinZ());
		}
		
		return girth(intersections);
	}
	
	public Intersection girth(List<Intersection> intersections) {
		Iterator<Intersection> it = intersections.iterator();
		Intersection girth = new Intersection();
		int index =0;
		while(it.hasNext()) {
			girth = it.next();
			
			Intersection i = intersections.get(index+1);
			if (Math.abs(i.getMinY().getY() - girth.getMinY().getY())>40||
					Math.abs(i.getMaxY().getY() - girth.getMaxY().getY())>40) {
				break;
			}
			index++;
		}
	
		return girth;
	}
	
	/*********************************************************/
	
	
		
	


}
