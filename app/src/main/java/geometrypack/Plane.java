package geometrypack;

import Jama.Matrix;

public class Plane {
	
	private Vector normal;
										
	private float a;
	private float b;
	private float c;
	private float d;
	public Plane(Vector normal,Vector Point) {
	// TODO Auto-generated constructor stub
		this.normal = normal;
		a= normal.getX();
		b=normal.getY();
		c=normal.getZ();
		d = normal.getX()*Point.getX() + normal.getY()*Point.getY() + normal.getZ()*Point.getZ();
}
	public Plane (float a,float b,float c,float d) {
		this.a=a;
		this.b=b;
		this.c=c;
		this.d=d;
	}
	public float get_a() {
		return this.a;
	}
	public float get_d() {
		return this.d;
	}
	public float[] coefficients() {
		float[] coefficients = new float[4];
		coefficients[0]= a;
		coefficients[1]= b;
		coefficients[2]= c;
		coefficients[3]= d;

		return coefficients;
	}
	public void reflect(Vector p,Vector ref_p) {
		double [][] rot = new double [][] {
			{0,0,1,0},
			{0,1,0,0},
			{-1,0,0,0},
			{0,0,0,1}
		}; 
		double [][] T = new double [][] {
			{1-2*a*a,-2*a*b,-2*a*c,-2*a*d},
			{-2*a*b,1-2*b*b,-2*b*c,-2*b*d},
			{-2*a*c,-2*b*c,1-2*c*c,-2*c*d},
			{0,0,0,1}
			};
		 
		double [][] X= new double[][] {
			{(double)p.getX()},
			{(double)p.getY()},
			{(double)p.getZ()},
			{1}
			};
		Matrix R = new Matrix (rot);
		Matrix T_m = new Matrix (T);
		Matrix X_m = new Matrix(X);
		Matrix X_ref = R.times(T_m.times(X_m));
		double[][] f = X_ref.getArray();
		
		ref_p.setX((float)f[0][0]);
		ref_p.setY((float)f[1][0]);
		ref_p.setZ((float)f[2][0]);
	}
	public Vector getNormal() {
		return normal;
	}
	public float distanceTo(Vector Point) {
		return normal.getX()*Point.getX() + normal.getY()*Point.getY() + normal.getZ()*Point.getZ()-d;
	}
	public String toString() {
		return Float.toString(a)+"*x +"+Float.toString(b)+"*y +"+Float.toString(c)
		+"*z ="+Float.toString(d);
	}
}
