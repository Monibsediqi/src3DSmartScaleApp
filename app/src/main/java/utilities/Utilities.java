package utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.openni.CoordinateConverter;
import org.openni.VideoFrameRef;
import org.openni.VideoStream;

import algorithms.Dbscan;
import algorithms.PCA;
import algorithms.PCA_opt;
import algorithms.dbscanner;
import algorithms.ransac;
import algorithms.ransac.Support;
import android.support.annotation.NonNull;
import geometrypack.Plane;
import geometrypack.Vector;
import patterns.cluster.DoubleArray;

public class Utilities {
	
	public static boolean headLeft = true;
	private static final int INPUT_BUFFER_SIZE = 0x10000;
	
	public static List<Vector> frameTocloud(VideoFrameRef frame,VideoStream mStream) throws IOException {
		List<Vector> raw_data = new ArrayList<>();

		ShortBuffer pixels = frame.getData().asShortBuffer();
		
		for (int x=0;x<frame.getWidth();x++) {
			 for (int y=0;y<frame.getHeight();y++){
			 Float X = CoordinateConverter.convertDepthToWorld(mStream, x, y, pixels.get(y*frame.getWidth()+x)).getX();
			 Float Y = CoordinateConverter.convertDepthToWorld(mStream, x, y, pixels.get(y*frame.getWidth()+x)).getY();
			 Float Z = CoordinateConverter.convertDepthToWorld(mStream, x, y, pixels.get(y*frame.getWidth()+x)).getZ();
			 
			 if(Z!=0) {
				 raw_data.add(new Vector(X,Y,Z));
				 }
			 }
		}

	return raw_data;
	}

	public static List<Vector> sample (double ratio,List<Vector> data) {
		List<Vector> cloud = new ArrayList<>();
		if (ratio > 0) {
			int index=0; 
			
			while(index < (data.size()*ratio)) {
				int i = (int) (Math.random() * data.size());
				cloud.add(data.get(i));
				index++;
			}
		}

		
		return cloud;	
	}
	public static List<Vector> ReadXYZ(@NonNull InputStream stream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream), INPUT_BUFFER_SIZE);
        String line;
        String[] lineArr;
        float x,y,z;
        List<Vector> Cloud = new ArrayList<Vector>();
        int vertexCount =0;
        stream.mark(0x100000);
        
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("#")) {
                lineArr = line.split(" ");
                vertexCount = Integer.parseInt(lineArr[1]);
            } else {
                break;
            }
        }
        for (int i = 0; i < vertexCount-2; i++) {
            lineArr = reader.readLine().trim().split(" ");
            x = Float.parseFloat(lineArr[0]);
            y = Float.parseFloat(lineArr[1]);
            z = Float.parseFloat(lineArr[2]);
            Cloud.add(new Vector (x,y,z));
        }
        
		return Cloud;
	}
	
	public static void SaveXYZ(List<DoubleArray> Cloud,String name) throws IOException {
		File file = new File(name+".xyz");
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		
	    String s = "# "+ Integer.toString(Cloud.size()-1)+"\n";
		fileOutputStream.write(s.getBytes());
		
		for(int i=0;i<Cloud.size();++i) {
				String str = Double.toString(Cloud.get(i).data[0])+ " "+ Double.toString(Cloud.get(i).data[1]) + " "+Double.toString(Cloud.get(i).data[2])+ "\n";
	     		fileOutputStream.write(str.getBytes());
		}

		fileOutputStream.close();
	}
	
	public static void SaveXYZasVector(List<Vector> Cloud,String name) throws IOException {
		File file = new File(name+".xyz");
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		
	    String s = "# "+ Integer.toString(Cloud.size()-1)+"\n";
		fileOutputStream.write(s.getBytes());
		
		for(int i=0;i<Cloud.size();++i) {
				String str =Float.toString(Cloud.get(i).getX())+ " "+ Float.toString(Cloud.get(i).getY()) + " "+Float.toString(Cloud.get(i).getZ())+ "\n";
	     		fileOutputStream.write(str.getBytes());
		}

		fileOutputStream.close();
	}
	
	public static  List<Vector> align(List<Vector> cloud) {
		PCA pca = new PCA();
		double [] c = new double[4];
		double [][] covariance = new double [3][3];
		double [][] eigenVectors = new double[3][3];
		
		pca.compute3Dcentroid(cloud, c);
		pca.computeCovarianceMatrixNormalized(cloud, c, covariance);
		eigenVectors = pca.eigenVectors(covariance);
		return pca.transform_to_origin(eigenVectors, c, cloud);
	}
	public static  List<DoubleArray> align_opt(List<DoubleArray> cloud) {
		PCA_opt pca = new PCA_opt();
		double [] c = new double[4];
		double [][] covariance = new double [3][3];
		double [][] eigenVectors = new double[3][3];
		
		pca.compute3Dcentroid(cloud, c);
		pca.computeCovarianceMatrixNormalized(cloud, c, covariance);
		eigenVectors = pca.eigenVectors(covariance);
		return pca.transform_to_origin(eigenVectors, c, cloud);
	}

	
	
	public static List<Vector> outliersRemoval(List<Vector> cloud) {
		List<Vector> output = new ArrayList<>(cloud);
		ransac ransac = new ransac(output, 10.0f, 100, 100, 600);
		
		int max=0;
		
		Support s = ransac.detect_plane();
		output.removeAll(s.points);
				

		dbscanner db = new dbscanner (output,5, 33);
		ArrayList<List<Vector>> clusters = db.Run();

		List<Vector> clusterMax = new ArrayList<>();
		for (List<Vector> cluster:clusters) {
				if (cluster.size() > max) {
					max = cluster.size();
					clusterMax = cluster;
				}
		}

		return clusterMax;
	}
	public static List<DoubleArray> outliersRemoval_opt(List<Vector> cloud) {
		
		ransac ransac = new ransac(cloud, 25.0f, 100, 100, 1000);
		Support s = ransac.detect_plane();
		cloud.removeAll(s.points); 
		int max=0;
		Dbscan db = new Dbscan(cloud,5,33);
		List<List<DoubleArray>> clusters = db.Run();
		List<DoubleArray> clusterMax = new ArrayList<>();
		
		for (List<DoubleArray> cluster:clusters) {
				if (cluster.size() > max) {
					max = cluster.size();
					clusterMax = cluster;
				}
		}

		return clusterMax;

		//return clusters.get(0);
	}
	
	private static float computecurve(Intersection girth) {
		float distance=0;
		
		for (int i=0;i<girth.getIntersection().size()-1;++i)			
			distance += girth.getIntersection().get(i).distance(girth.getIntersection().get(i+1));
		return distance;
		
	}
	//Weight estimation utility using ParamRet class
	
	private static Intersection Find_inter__(Vector pt,int thresh,List<Vector> PointCloud){ // intersection of the plane with normal k = (0,0,1) and ref point pt
		List<Vector> intersection = new ArrayList<>();
		Vector normal = new Vector(0f, 0f,1f);
		Plane plane= new Plane(normal, pt);
		for (Vector point : PointCloud) {
			float distanceToPlane = plane.distanceTo(point);
			if(Math.abs(distanceToPlane)<=thresh)
				intersection.add(point);
		}
		Intersection inter = new Intersection(intersection);
		//Average Y ??? 
		return inter;
	}
	private static float getLength(Intersection girth, List<Vector> PointCloud) {
		Util util = new Util(PointCloud);
		if (headLeft) {
			Plane pl = new Plane (new Vector (0f,0f,-1f), girth.getIntersection().get(0));
			Vector pt = util.getMinZ();
			return pl.distanceTo(pt);
		}
		Plane pl = new Plane (new Vector (0f,0f,1f), girth.getIntersection().get(0));
		Vector pt = util.getMaxZ();
		return pl.distanceTo(pt);

	}
	private static void isHeadLeft(List<Vector> PointCloud) {
		Util u = new Util (PointCloud);
		List<Intersection> intersections = new ArrayList<>();
		Intersection inter = Find_inter__(u.getMinZ(),5,PointCloud);
		intersections.add(inter);
		int index = 0;
		while(!inter.getMaxpt().equals(u.getMaxZ())&&index<1) {
			inter = Find_inter__(inter.getMaxpt(),55,PointCloud);
			intersections.add(inter);
			index++;
		}
		if (intersections.get(0).getMinY().getY() - intersections.get(1).getMinY().getY()>30||
				intersections.get(0).getMaxY().getY() - intersections.get(1).getMaxY().getY()>30) {
		headLeft = true;
		}
		else 
			headLeft = false;
	}
	public static float estimateWeight(List<Vector> PointCloud) {
		ParamRet pr = new ParamRet(PointCloud);
		isHeadLeft(PointCloud);
		Intersection girth = pr.sweep(headLeft);

		float length = getLength(girth,PointCloud)/10;		
		Intersection g = new Intersection(girth.getIntersection());
		g.averageZ();
		g.orderY();
		g.smoothing();
		float girth_distance = computecurve(g);
		float G = ((girth_distance/5)+30) ;
		
//		float G2 = ((girth_distance/5)); 


		return (float)((0.8*(-0.3872587127 * G + 0.1281524568 * length+ 137.33234145))
				+ 0.2*((10.1709* G *0.393701-205.7492)*0.453592));
//		
	}
	class SortbyZ implements Comparator<Vector> 
	{ 
	    // Used for sorting in ascending order of 
	    public int compare(Vector a, Vector b) 
	    { 
	        return (int) (a.getZ() - b.getZ()); 
	    } 
	}
		
}
