package org.example.kmeans;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sichaolong
 * @createdate 2024/3/29 10:20
 */

public class SimpleKMeansDemo {
    private int k; // 聚类的个数
    private List<Point> points; // 数据集
    private List<Cluster> clusters; // 聚类结果

    public SimpleKMeansDemo(int k, List<Point> points) {
        this.k = k;
        this.points = points;
        this.clusters = new ArrayList<>();
    }

    public void run() {
        // 初始化聚类中心, 随机选取k个数据点作为初始聚类中心
        for (int i = 0; i < k; i++) {
            Cluster cluster = new Cluster();
            cluster.setCentroid(points.get(i));
            clusters.add(cluster);
        }

        // 循环直到收敛
        boolean converged = false;
        while (!converged) {
            // 清空聚类结果
            for (Cluster cluster : clusters) {
                cluster.clearPoints();
            }

            // 将每个数据点分配到最近的聚类中心
            for (Point point : points) {
                Cluster nearestCluster = null;
                double minDistance = Double.MAX_VALUE;
                // 遍历每个聚类中心，找到最近的
                for (Cluster cluster : clusters) {
                    double distance = point.distanceTo(cluster.getCentroid());
                    if (distance < minDistance) {
                        minDistance = distance;
                        nearestCluster = cluster;
                    }
                }
                nearestCluster.addPoint(point);
            }

            // 更新聚类中心
            converged = true;
            // 遍历每个聚类中心，如果聚类中心没有发生变化，则收敛
            for (Cluster cluster : clusters) {
                Point oldCentroid = cluster.getCentroid();
                Point newCentroid = cluster.calculateCentroid();
                if (!oldCentroid.equals(newCentroid)) {
                    cluster.setCentroid(newCentroid);
                    converged = false;
                }
            }
        }
    }

    public List<Cluster> getClusters() {
        return clusters;
    }

    public static void main(String[] args) {
        // 创建数据集
        List<Point> points = new ArrayList<>();
        points.add(new Point(1, 1));
        points.add(new Point(1, 2));
        points.add(new Point(2, 2));
        points.add(new Point(5, 5));
        points.add(new Point(6, 6));
        points.add(new Point(7, 7));

        // 创建K-means对象并运行算法
        SimpleKMeansDemo kMeans = new SimpleKMeansDemo(2, points);
        kMeans.run();

        // 输出聚类结果
        List<Cluster> clusters = kMeans.getClusters();
        for (int i = 0; i < clusters.size(); i++) {
            System.out.println("Cluster " + (i + 1) + ":");
            for (Point point : clusters.get(i).getPoints()) {
                System.out.println("(" + point.getX() + ", " + point.getY() + ")");
            }
            System.out.println();
        }
    }
}

class Point {
    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    /**
     * 计算两个点之间的距离,使用欧几里徳距离
     * @param other
     * @return
     */
    public double distanceTo(Point other) {
        double dx = x - other.x;
        double dy = y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Point other = (Point) obj;
        return Double.compare(other.x, x) == 0 && Double.compare(other.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(x) + Double.hashCode(y);
    }
}

class Cluster {
    private Point centroid;
    private List<Point> points;

    public Cluster() {
        this.points = new ArrayList<>();
    }

    public Point getCentroid() {
        return centroid;
    }

    public void setCentroid(Point centroid) {
        this.centroid = centroid;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void addPoint(Point point) {
        points.add(point);
    }

    public void clearPoints() {
        points.clear();
    }


    /**
     * 计算聚类中心点
     * @return
     */
    public Point calculateCentroid() {
        double sumX = 0;
        double sumY = 0;
        for (Point point : points) {
            sumX += point.getX();
            sumY += point.getY();
        }
        double avgX = sumX / points.size();
        double avgY = sumY / points.size();
        return new Point(avgX, avgY);
    }
}



