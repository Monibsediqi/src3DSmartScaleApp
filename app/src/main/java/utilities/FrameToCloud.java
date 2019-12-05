package utilities;

import java.io.IOException;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import org.openni.CoordinateConverter;
import org.openni.VideoFrameRef;
import org.openni.VideoStream;

import geometrypack.Vector;

public class FrameToCloud {
	public static List<Vector> export(VideoFrameRef frame,VideoStream mStream) throws IOException {
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

}
