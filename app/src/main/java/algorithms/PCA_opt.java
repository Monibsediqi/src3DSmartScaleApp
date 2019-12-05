package algorithms;

import java.util.ArrayList;
import java.util.List;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import patterns.cluster.DoubleArray;


public class PCA_opt {
	
	public PCA_opt() {}
	
	public void compute3Dcentroid(List <DoubleArray> PointCloud,double [] centroid ) {
		for (int i= 0;i<PointCloud.size();++i) {
			centroid[0] += (double)PointCloud.get(i).data[0];
			centroid[1] += (double)PointCloud.get(i).data[1];
			centroid[2] += (double)PointCloud.get(i).data[2];
		}
		centroid[0] /= PointCloud.size();
		centroid[1] /= PointCloud.size();
		centroid[2] /= PointCloud.size();
		centroid[3] = 1;
	}
	
	public int computeCovarianceMatrix(List <DoubleArray> PointCloud,double [] centroid,double [][] covariance_matrix) {
		for (int i =0;i<PointCloud.size();++i) {
			double[] pt = new double [3];
			pt[0] += (double)PointCloud.get(i).data[0] - centroid[0];
			pt[1] += (double)PointCloud.get(i).data[1] - centroid[1];
			pt[2] += (double)PointCloud.get(i).data[2] - centroid[2];
			
			covariance_matrix[1][1] += pt[1] * pt[1]; 
			covariance_matrix[1][2] += pt[1] * pt[2]; 
			covariance_matrix[2][2] += pt[2] * pt[2];
			
			covariance_matrix[0][0] += pt[0] * pt[0];
			covariance_matrix[0][1] += pt[1] * pt[0];
			covariance_matrix[0][2] += pt[2] * pt[0];
		}
		covariance_matrix[1][0] = covariance_matrix[0][1];
		covariance_matrix[2][0] = covariance_matrix[0][2];
		covariance_matrix[2][1] = covariance_matrix[1][2];
		return PointCloud.size();
	}
	
	public int computeCovarianceMatrixNormalized(List <DoubleArray> PointCloud,double [] centroid,double [][] covariance_matrix) {
		int pointsize = computeCovarianceMatrix(PointCloud,centroid,covariance_matrix);
		for (int i =0;i<3;++i)
			for(int j=0;j<3;j++)
				covariance_matrix[i][j]/= pointsize;
		return pointsize;
	}
	
	public double[][] eigenVectors(double[][] matrix){
		Matrix M = new Matrix(matrix);
		EigenvalueDecomposition eigen_val= M.eig();
		return eigen_val.getV().getArray();
		
	}

	public List<DoubleArray> transform_to_origin(double[][] eigenVectors,double[] centroid, List<DoubleArray> PointCloud) {
		Matrix eigen = new Matrix (eigenVectors);
		Matrix C = new Matrix(new double [][]{{centroid[0]},{centroid[1]},{centroid[2]}})  ;
		Matrix projection_transform = (eigen.transpose().times(C));
		
		double [][] eig_trans = eigen.transpose().getArray();
		double [][] proj_trans = projection_transform.getArray();
		double [][] trans = new double [][] {
			{eig_trans[0][0],eig_trans[0][1],eig_trans[0][2],-1.d*proj_trans[0][0]},
			{eig_trans[1][0],eig_trans[1][1],eig_trans[1][2],-1.d*proj_trans[1][0]},
			{eig_trans[2][0],eig_trans[2][1],eig_trans[2][2],-1.d*proj_trans[2][0]},
			{		0		,		0		,		0		,			-1.d*1		}
		};
		
		Matrix transformation = new Matrix (trans);
		
		List<DoubleArray> trans_pcl = new ArrayList<>();
		
		for (DoubleArray v:PointCloud) {
			double [][] X= new double[][] {
				{(double)v.data[0]},
				{(double)v.data[1]},
				{(double)v.data[2]},
				{		1.d		}
				};
				Matrix X_= new Matrix (X);
				double [][] d =transformation.times(X_).getArray();
				DoubleArray q =  new DoubleArray(new double[]{d[0][0],d[1][0],d[2][0]});
				trans_pcl.add(q);
		}
		
		return trans_pcl;
		
	}
	
}
