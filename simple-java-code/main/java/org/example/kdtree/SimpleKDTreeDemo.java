package java.org.example.kdtree;

import java.util.ArrayList;
import java.util.List;


/**
 * @author sichaolong
 * @createdate 2024/3/14 14:19
 */

class KDNode {
    int[] point;
    KDNode left;
    KDNode right;

    public KDNode(int[] point) {
        this.point = point;
        this.left = null;
        this.right = null;
    }
}

public class SimpleKDTreeDemo {
    private KDNode root;

    public SimpleKDTreeDemo() {
        this.root = null;
    }

    public void insert(int[] point) {
        this.root = insertNode(this.root, point, 0);
    }

    private KDNode insertNode(KDNode node, int[] point, int depth) {
        if (node == null) {
            return new KDNode(point);
        }

        int k = point.length;

        // 选定切割轴
        int axis = depth % k;

        if (point[axis] < node.point[axis]) {
            node.left = insertNode(node.left, point, depth + 1);
        } else {
            node.right = insertNode(node.right, point, depth + 1);
        }

        return node;
    }

    public List<int[]> search(int[] target, int n) {
        List<int[]> result = new ArrayList<>();
        searchNode(this.root, target, 0, n, result);
        return result;
    }

    private void searchNode(KDNode node, int[] target, int depth, int k, List<int[]> result) {
        if (node == null) {
            return;
        }

        // 确定当前层的切割维度
        int axis = depth % k;

        if (target[axis] < node.point[axis]) {
            searchNode(node.left, target, depth + 1, k, result);
        } else {
            searchNode(node.right, target, depth + 1, k, result);
        }

        // 还没找够n个，就直接添加
        if (result.size() < k) {
            result.add(node.point);
        } else {
            // 上一个最近的点
            int[] farthestPoint = result.get(result.size() - 1);
            // 如果当前点距离更近，就替换
            if (distance(target, node.point) < distance(target, farthestPoint)) {
                result.remove(result.size() - 1);
                result.add(node.point);
            }
        }

        // 如果切割轴距离更近，就添加
        int[] farthestPoint = result.get(result.size() - 1);
        // 切割轴距离
        double splitDistance = Math.abs(target[axis] - node.point[axis]);
        // 切割轴距离更近
        if (splitDistance < distance(target, farthestPoint)) {
            if (target[axis] < node.point[axis]) {
                searchNode(node.right, target, depth + 1, k, result);
            } else {
                searchNode(node.left, target, depth + 1, k, result);
            }
        }
    }


    /**
     * 欧式距离
     * @param point1
     * @param point2
     * @return
     */
    private double distance(int[] point1, int[] point2) {
        int k = point1.length;
        double sum = 0;
        for (int i = 0; i < k; i++) {
            sum += Math.pow(point1[i] - point2[i], 2);
        }
        return Math.sqrt(sum);
    }

    public static void main(String[] args) {
        SimpleKDTreeDemo kdTree = new SimpleKDTreeDemo();
        int[][] points = {{2, 3}, {5, 4}, {9, 6}, {4, 7}, {8, 1}, {7, 2}};
        for (int[] point : points) {
            kdTree.insert(point);
        }

        int[] target = {6, 3};
        int n = 2;

        // 找出最近的n个点
        List<int[]> result = kdTree.search(target, n);
        System.out.println("The " + n + " nearest neighbors to the target point " + java.util.Arrays.toString(target) + " are:");
        for (int[] point : result) {
            System.out.println(java.util.Arrays.toString(point));
        }
    }
}