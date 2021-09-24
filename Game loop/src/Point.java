/**
 * Created by cg8200 on 9/24/2021.
 */
public class Point {
    int X, Y ;
    public Point(int x, int y) {
        X = x;
        Y = y;
    }

    public boolean comparePoint(Point point){
        if(X == point.X&&Y == point.Y){
            return true;
        }else{
            return false;
        }
    }

}
