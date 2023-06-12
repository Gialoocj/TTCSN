
import java.io.IOException;
import java.time.LocalTime;
import java.lang.Integer;
import data.TrafficJam;
import data.TSPFileReader;
import data.TSPProblem;

class BrandAndBound {
    public static int[] bestPath; // mảng lưu đường đi ngắn nhất
    private static int bestDistance; // khoảng cách ngắn nhất

    public static void main(String[] args) {
        String filename = "D:\\BrandAndBound\\data.txt";

        try {
            TSPProblem tspProblem = TSPFileReader.readTSPProblemFromFile(filename);
            BrandAndBound brandAndBound = new BrandAndBound();
            TSPProblem.displayTSPProblem(tspProblem);
            brandAndBound.solveTSP(tspProblem);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Invalid data format in the file: " + e.getMessage());
        }
    }

    private void solveTSP(TSPProblem tspProblem) {
        // Sử dụng dữ liệu trong tspProblem để giải quyết bài toán TSP
        int n = tspProblem.getVertexNames().length;
        bestPath = new int[n];
        bestDistance = Integer.MAX_VALUE;

        int[] currentPath = new int[n];
        boolean[] visited = new boolean[n];

        currentPath[0] = TSPProblem.getVertexId(tspProblem.getStartPoint(), tspProblem.getVertexNames());
        visited[TSPProblem.getVertexId(tspProblem.getStartPoint(), tspProblem.getVertexNames())] = true;
        tspRecursive(tspProblem, currentPath, visited, 1, 0);

        System.out.println("Shortest Path: ");
        for (int i = 0; i < n; i++) {
            if (i == n - 1) {
                System.out.print(
                        tspProblem.getVertexNames()[bestPath[i]] + " -> " + tspProblem.getVertexNames()[bestPath[0]]);
                System.out.println();

                System.out.println("Shortest Distance: " + bestDistance + " minutes");
                return;
            }
            System.out.print(tspProblem.getVertexNames()[bestPath[i]] + " -> ");
        }
    }

    //hàm đánh giá cận
    private static int evaluateBound(TSPProblem tspProblem, int[] currentPath, int level, int currentDistance) {
        int remainingDistance = 0;
        boolean[] visited = new boolean[tspProblem.getN()];
        for (int i = level; i < tspProblem.getN(); i++) {
            visited[currentPath[i]] = true;
        }

        // Ước lượng khoảng cách tối thiểu từ đỉnh hiện tại đến đích
        int minDistance = Integer.MAX_VALUE;
        for (int i = 0; i < tspProblem.getN(); i++) {
            if (!visited[i]) {
                for (int j = 0; j < tspProblem.getN(); j++) {
                    if (!visited[j] && tspProblem.getWeightMatrix()[i][j] > 0
                            && tspProblem.getWeightMatrix()[i][j] < minDistance) {
                        minDistance = tspProblem.getWeightMatrix()[i][j];
                    }
                }
            }
        }

        // Kiểm tra xem có tắc đường trên đoạn đường hiện tại hay không
        for (int i = level - 1; i < tspProblem.getN() - 1; i++) {
            String segment = tspProblem.getVertexNames()[currentPath[i]]
                    + tspProblem.getVertexNames()[currentPath[i + 1]];
            for (TrafficJam trafficJam : tspProblem.getTrafficJams()) {
                if (trafficJam.getSegment().equals(segment)) {
                    LocalTime currentStartTime = tspProblem.getStartTime().plusMinutes(currentDistance);
                    if (currentStartTime.isAfter(trafficJam.getStartTime())
                            && currentStartTime.isBefore(trafficJam.getEndTime())) {
                        int estimatedDelay = trafficJam.getDelay();
                        remainingDistance += estimatedDelay;
                        break;
                    }
                }
            }
        }

        return currentDistance + minDistance + remainingDistance;
    }

    private static void tspRecursive(TSPProblem tspProblem, int[] currentPath, boolean[] visited,
            int level, int currentDistance) {
        if (level == tspProblem.getN()) {
            // Kiểm tra xem đường đi hiện tại có ngắn hơn đường đi ngắn nhất hiện tại không
            if (tspProblem.getWeightMatrix()[currentPath[level - 1]][currentPath[0]] > 0
                    && currentDistance
                            + tspProblem.getWeightMatrix()[currentPath[level - 1]][currentPath[0]] < bestDistance) {
                System.arraycopy(currentPath, 0, bestPath, 0, tspProblem.getVertexNames().length);
                bestDistance = currentDistance + tspProblem.getWeightMatrix()[currentPath[level - 1]][currentPath[0]];
            }
            return;
        }

        // Xét các đỉnh kề của đỉnh hiện tại
        for (int i = 0; i < tspProblem.getN(); i++) {
            if (!visited[i] && tspProblem.getWeightMatrix()[currentPath[level - 1]][i] > 0) {
                // Kiểm tra xem có tắc đường nào trên đoạn đường hiện tại hay không
                boolean hasTrafficJam = false;
                for (TrafficJam trafficJam : tspProblem.getTrafficJams()) {
                    if (trafficJam.getSegment().equals(
                            tspProblem.getVertexNames()[currentPath[level - 1]] + tspProblem.getVertexNames()[i])) {
                        LocalTime startTime = tspProblem.getStartTime();
                        LocalTime currentStartTime = startTime.plusMinutes(currentDistance);
                        if (currentStartTime.isAfter(trafficJam.getStartTime())
                                && currentStartTime.isBefore(trafficJam.getEndTime())) {
                            currentDistance += trafficJam.getDelay();
                            hasTrafficJam = true;
                            break;
                        }
                    }
                }

                if (!hasTrafficJam) {
                    currentPath[level] = i;
                    visited[i] = true;
                    int bound = evaluateBound(tspProblem, currentPath, level + 1,
                            currentDistance + tspProblem.getWeightMatrix()[currentPath[level - 1]][i]);
                    if (bound < bestDistance) {
                        tspRecursive(tspProblem, currentPath, visited, level + 1,
                                currentDistance + tspProblem.getWeightMatrix()[currentPath[level - 1]][i]);
                    }
                    visited[i] = false;
                }
            }
        }
    }
}
