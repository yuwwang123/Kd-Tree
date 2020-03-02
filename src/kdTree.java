import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Queue;
public class kdTree {
	
	   private Node root;
	   private int size;
	   private static class Node {
		   private Point2D p;      // the point
		   private RectHV rect;    // the axis-aligned rectangle corresponding to this node
		   private Node lb;        // the left/bottom subtree
		   private Node rt;        // the right/top subtree		
		   
		   public Node(Point2D p, RectHV rect) {
			   this.p = p;
			   this.rect = rect;
		   }
		   
	   }
	   
	   public boolean isEmpty() {                     // is the set empty?
		   return size() == 0;
	   }
	   
	   public int size() {                         // number of points in the set
		   return size;
	   }
	   
	   
	   private Node get(Node x, Point2D p, int level) {
		   if (p == null) throw new IllegalArgumentException();
		   if (x == null) return null;
		   double xCmp = p.x() - x.p.x();
		   double yCmp = p.y() - x.p.y();
		   if (level % 2 == 0) {
			   if (xCmp < 0) return get(x.lb, p, level + 1);
			   if (xCmp > 0) return get(x.rt, p, level + 1);
			   else if (yCmp == 0) return x;
			   return get(x.lb, p, level + 1);
		   }
		   else {
			   if (yCmp < 0) return get(x.lb, p, level + 1);
			   if (yCmp > 0) return get(x.rt, p, level + 1);
			   else if (xCmp == 0) return x;
			   return get(x.lb, p, level + 1);	   
		   }
		   
	   }
	   public  void insert(Point2D p)   {           // add the point to the set (if it is not already in the set)
		   if (p == null) throw new IllegalArgumentException();
           root = insert(root, p, new RectHV(0, 0, 1, 1), 0);
           size++;
	   }
	   
	   private Node insert(Node x, Point2D p, RectHV rect, int level) {
		   if (p == null) throw new IllegalArgumentException();
           if (x == null) {
        	   return new Node(p, rect);
           }
           if (x.p.equals(p)) return x;
           
		   x.rect = rect;

		   double xCmp = p.x() - x.p.x();
		   double yCmp = p.y() - x.p.y();
		   		   
		   if (level % 2 == 0) {
			   RectHV rectVL = new RectHV(rect.xmin(), rect.ymin(), x.p.x(), rect.ymax());
			   RectHV rectVR = new RectHV(x.p.x(), rect.ymin(), rect.xmax(), rect.ymax());

			   if (xCmp <= 0) x.lb = insert(x.lb, p, rectVL, level + 1);
			   if (xCmp > 0) x.rt = insert(x.rt, p, rectVR, level + 1);
		   }
		   else {
			   RectHV rectHB = new RectHV(rect.xmin(), rect.ymin(), rect.xmax(), x.p.y());
			   RectHV rectHT = new RectHV(rect.xmin(), x.p.y(), rect.xmax(), rect.ymax());

			   if (yCmp <= 0) x.lb = insert(x.lb, p, rectHB, level + 1);
			   if (yCmp > 0) x.rt = insert(x.rt, p, rectHT, level + 1);
		   }
		   return x;
	   }
	   
	   public boolean contains(Point2D p) {            // does the set contain point p? 
		   return get(root, p, 0) != null;
	   }
	   
	   public void normalDraw() {                        // draw all points to standard draw
	        StdDraw.rectangle(0.5, 0.5, 0.5, 0.5);
	        if (isEmpty()) return;
	        normalDraw(root);
	        StdDraw.show(0);
	   }
	   
	   public void draw() {                        // draw all points to standard draw
	        StdDraw.rectangle(0.5, 0.5, 0.5, 0.5);
	        if (isEmpty()) return;
	        draw(root, 0);
	        StdDraw.show(0);
	   }
	   
	   private void draw(Node x, int level) {
		
	       StdDraw.setPenColor(StdDraw.BLACK);
           StdDraw.setPenRadius(.01);
           
           StdDraw.point(x.p.x(), x.p.y());
   
    	   StdDraw.setPenRadius();
           if (level % 2 == 0) {
        	   StdDraw.setPenColor(StdDraw.RED);
        	   StdDraw.line(x.p.x(), x.rect.ymin(), x.p.x(), x.rect.ymax());
           }
           else {
        	   StdDraw.setPenColor(StdDraw.BLUE);
        	   StdDraw.line(x.rect.xmin(), x.p.y(), x.rect.xmax(), x.p.y());

           }
           if (x.lb != null)     draw(x.lb, level + 1);
           if (x.rt != null)     draw(x.rt, level + 1);
	   }
	   
	   private void normalDraw(Node x) {
		   if (x == null) return;
	       StdDraw.setPenColor(StdDraw.BLACK);
           StdDraw.setPenRadius(.01);
           
           StdDraw.point(x.p.x(), x.p.y());
           normalDraw(x.lb);
           normalDraw(x.rt);
	   }
	  
	   public Iterable<Point2D> range(RectHV rect) {            // all points that are inside the rectangle (or on the boundary)
		   Queue<Point2D> q = new Queue<Point2D>();
		   range(root, rect, q);
		   return q;
	   }
	   
	   private void range(Node x, RectHV rect, Queue<Point2D> q){
		   if (rect == null) throw new IllegalArgumentException();
		   if (x == null) return;
		   if (x.rect.intersects(rect)) {
			   if (rect.contains(x.p)) q.enqueue(x.p);
			   range(x.lb, rect, q);
			   range(x.rt, rect, q);
		   }
	   }
	   
	   
	   public Point2D nearest(Point2D p) {            // a nearest neighbor in the set to point p; null if the set is empty
		   return nearest(root, p, root.p);
	   }
	   
	   private Point2D nearest(Node x, Point2D p, Point2D champion) {
		   if (x == null) return null;
		   if(x.p.distanceSquaredTo(p) < champion.distanceSquaredTo(p)) champion = x.p;
		   if (x.lb == null & x.rt == null) return champion;
		   if (x.lb == null) return nearest(x.rt, p, champion);
		   if (x.rt == null) return nearest(x.lb, p, champion);
		   //if(x.lb.rect.contains(p)) return nearest(x.lb, p, champion);
		   //else return nearest(x.rt, p, champion);
		   
		   if (x.lb.rect.contains(p)) {
			    Point2D t1 = nearest(x.lb, p, champion);
			    if (t1.distanceSquaredTo(p) < x.rt.rect.distanceSquaredTo(p)) {
			    	return t1;
			    }
			    Point2D t2 = nearest(x.rt, p, champion);
			   	if (t2.distanceSquaredTo(p) < t1.distanceSquaredTo(p)) return t2;
			   	else return t1;
		   }
		   else {
			    Point2D t1 = nearest(x.rt, p, champion);
			    if (t1.distanceSquaredTo(p) < x.lb.rect.distanceSquaredTo(p)) {
			    	return t1;
			    }
			   	Point2D t2 = nearest(x.lb, p, champion);
			   	if (t2.distanceSquaredTo(p) < t1.distanceSquaredTo(p)) return t2;
			   	else return t1;
		   }		   	   
	   }
       
	   public static void main(String[] args) {
		   kdTree tree = new kdTree();
		   Point2D p1 = new Point2D(0.7,0.2);
		   Point2D p2 = new Point2D(0.5,0.4);
		   Point2D p3 = new Point2D(0.2,0.3);
		   Point2D p4 = new Point2D(0.4,0.7);
		   Point2D p5 = new Point2D(0.9,0.6);
		   Point2D p6 = new Point2D(0.7,0.1);
		   Point2D p7 = new Point2D(0.5,0.4);
           Point2D query = new Point2D(0.85,0.65);

		   tree.insert(p1);
		   tree.insert(p2);
		   tree.insert(p3);
		   tree.insert(p4);
		   tree.insert(p5);
		   tree.insert(p6);
		   tree.insert(p7);

	       tree.draw();
	       StdDraw.setPenColor(StdDraw.RED);
	       StdDraw.setPenRadius(0.03);
	       StdDraw.point(query.x(), query.y());
	       
	       Point2D nearest = tree.nearest(query);
	       
	       StdDraw.setPenColor(StdDraw.BLUE);
	       StdDraw.point(nearest.x(), nearest.y());

		   System.out.println(nearest);

	   }
		   
	   

}
