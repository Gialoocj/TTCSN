package data;

import java.time.LocalTime;
import java.util.List;

public class TSPProblem {
    private int n;// số lượng đỉnh
    private String[] vertexNames; // tên các đỉnh
    private int[][] weightMatrix; // ma trận trọng số thời gian
    private List<TrafficJam> trafficJams;
    private String startPoint;
    private LocalTime startTime;

    public TSPProblem(int n, String[] vertexNames, int[][] weightMatrix, List<TrafficJam> trafficJams,
            String startPoint, LocalTime startTime) {
        this.n = n;
        this.vertexNames = vertexNames;
        this.weightMatrix = weightMatrix;
        this.trafficJams = trafficJams;
        this.startPoint = startPoint;
        this.startTime = startTime;
    }

    public int getN() {
        return n;
    }

    public String[] getVertexNames() {
        return vertexNames;
    }

    public int[][] getWeightMatrix() {
        return weightMatrix;
    }

    public List<TrafficJam> getTrafficJams() {
        return trafficJams;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public class TSPProblemConverter {
        public static void convertSegmentToPoints(String segment, String[] vertexNames, int[][] weightMatrix) {
            int startPointIndex = -1;
            int endPointIndex = -1;

            // Tìm chỉ số của các đỉnh trong tên đoạn đường
            for (int i = 0; i < vertexNames.length; i++) {
                if (vertexNames[i].equals(segment.substring(0, 1))) {
                    startPointIndex = i;
                }
                if (vertexNames[i].equals(segment.substring(1, 2))) {
                    endPointIndex = i;
                }
            }
        }
    }

    public static int getVertexId(String vertexName, String[] vertexNames) {
        for (int i = 0; i < vertexNames.length; i++) {
            if (vertexNames[i].equals(vertexName)) {
                return i;
            }
        }
        return -1; // Trả về -1 nếu không tìm thấy
    }

    public static void displayTSPProblem(TSPProblem tspProblem) {
        System.out.println("Number of vertices: " + tspProblem.getN());// hiển thị số đỉnh
        System.out.println("Vertex names: " + String.join(", ", tspProblem.getVertexNames()));// hiển thị tên đỉnh

        // hiển thị ma trận trọng số
        System.out.println("Weight matrix:");
        int[][] weightMatrix = tspProblem.getWeightMatrix();
        for (int i = 0; i < tspProblem.getN(); i++) {
            for (int j = 0; j < tspProblem.getN(); j++) {
                System.out.print(weightMatrix[i][j] + "\t");
            }
            System.out.println();
        }

        // hiển thị tên đỉnh bắt đầu
        System.out.println("Start point: " + tspProblem.getStartPoint() + " - "
                + getVertexId(tspProblem.getStartPoint(), tspProblem.getVertexNames()));
        // hiển thị thời gian bắt đầu
        System.out.println("Start time: " + tspProblem.getStartTime());

        System.out.println("Traffic jams:");
        List<TrafficJam> trafficJams = tspProblem.getTrafficJams();
        for (TrafficJam trafficJam : trafficJams) {
            System.out.println("Segment: " + trafficJam.getSegment());
            System.out.println("Start time: " + trafficJam.getStartTime());
            System.out.println("End time: " + trafficJam.getEndTime());
            TSPProblemConverter.convertSegmentToPoints(trafficJam.getSegment(), tspProblem.getVertexNames(),
                    tspProblem.getWeightMatrix());
            System.out.println();
        }
    }

   
}