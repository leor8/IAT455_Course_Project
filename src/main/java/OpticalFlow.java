package main.java;

import org.opencv.core.*;
import org.opencv.video.Video;
import java.util.ArrayList;

public class OpticalFlow {
    //call inputVideo.read(frames) in calculate optflow method
    //Mat2BufferedImage -> gets frames to calculate optical flow

    ArrayList<Point> newDotPosition = new ArrayList<>(); //should this be in UI class?

    //Reference: L-K optical flow https://docs.opencv.org/3.4/d4/dee/tutorial_optical_flow.html
    public void calcOptFlow(Mat prevImg, Mat nextImg, ArrayList<Point> initPos){
        MatOfPoint2f prevPts = new MatOfPoint2f() , nextPts = new MatOfPoint2f(); //list of prev/ next dot coordinates
        prevPts.fromList(initPos); //convert arraylist of points (initial dot coordinates) to MatofPoints
        MatOfByte status = new MatOfByte();
        MatOfFloat err = new MatOfFloat();
        TermCriteria criteria = new TermCriteria(TermCriteria.COUNT + TermCriteria.EPS,10,0.03);

        Video.calcOpticalFlowPyrLK(prevImg, nextImg, prevPts, nextPts, status, err, new Size(15,15),2, criteria);

        Point nextPtsArr[] = nextPts.toArray();

        //add new dot positions to new positions arraylist to draw dots
        for (int i = 0; i < nextPtsArr.length; i++) {
            newDotPosition.add(nextPtsArr[i]);
        }
    }


}
