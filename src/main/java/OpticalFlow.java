package main.java;

import org.opencv.core.*;
import org.opencv.video.Video;
import java.util.ArrayList;

public class OpticalFlow {

    public OpticalFlow() {

    }

    //Reference: L-K optical flow https://docs.opencv.org/3.4/d4/dee/tutorial_optical_flow.html
    public ArrayList<Dot> calcOptFlow(Mat prevImg, Mat nextImg, ArrayList<Dot> initPos, int minX, int minY, int maxX, int maxY){
        ArrayList<Dot> newDotPosition = new ArrayList<Dot>();
        MatOfPoint2f prevPts = new MatOfPoint2f() , nextPts = new MatOfPoint2f(); //list of prev/ next dot coordinates
        // Convert dots to points
        ArrayList<Point> initPosPoints = new ArrayList<Point>();
        for(int i = 0;  i < initPos.size(); i++) {
            Point newPoint = new Point(initPos.get(i).getX(), initPos.get(i).getY());
            initPosPoints.add(newPoint);
        }

        prevPts.fromList(initPosPoints); //convert arraylist of points (initial dot coordinates) to MatofPoints
        MatOfByte status = new MatOfByte();
        MatOfFloat err = new MatOfFloat();
        TermCriteria criteria = new TermCriteria(TermCriteria.COUNT + TermCriteria.EPS,10,0.03);

        //Calculate optical flow
        Video.calcOpticalFlowPyrLK(prevImg, nextImg, prevPts, nextPts, status, err, new Size(15,15),2, criteria);

        Point nextPtsArr[] = nextPts.toArray();

        //Add new dot positions to the dots to move them. Clip dot positions if they go outside of video width/height
        for (int i = 0; i < nextPtsArr.length; i++) {
            Point currPoint = nextPtsArr[i];
            int finalX, finalY;

            if(currPoint.x > maxX) {
                finalX = maxX;
            } else if(currPoint.x < minX) {
                finalX = minX;
            } else {
                finalX = (int)currPoint.x;
            }

            if(currPoint.y > maxY) {
                finalY = maxY;
            } else if(currPoint.y < minY) {
                finalY = minY;
            } else {
                finalY = (int)currPoint.y;
            }

            Dot currDot = new Dot(finalX, finalY, initPos.get(i).getRadius(), initPos.get(i).getColor());
            newDotPosition.add(currDot);
        }

        return newDotPosition;
    }
}
