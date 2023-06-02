import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.lang.Integer;
import java.time.Duration;

public class BrandAndBound{
    private static int[] bestPath; // mảng lưu đường đi ngắn nhất
    private static int bestDistance; // khoảng cách ngắn nhất
    public static void main(String[] args) {
        String filename = "D:\\BrandAndBound\\data.txt"; //Gán địa chỉ tệp cho filename, lấy đường dẫn tuyệt đối hoặc tương đối

        try {
            //Đọc dữ liệu từ tệp tin
            TSPProblem tspProblem = readTSPProblemFromFile(filename);
            //hiển thị thông tin dữ liệu từ file
            displayTSPProblem(tspProblem);
            //Giải quyết bài toán
            solveTSP(tspProblem);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Invalid data format in the file: " + e.getMessage());
        }
    }

    private static TSPProblem readTSPProblemFromFile(String filename) throws IOException {
        BufferedReader reader = null;
        try {
            //Mở tệp tin để đọc
            reader = new BufferedReader(new FileReader(filename));
            //Đọc số đỉnh từ tệp tin
            int n = Integer.parseInt(reader.readLine());
            
            //Đọc tên các đỉnh từ tệp và lưu vào mảng 
            String[] vertexNames = reader.readLine().split(" ");

            //Gán id cho các đỉnh
            assignIDs(vertexNames);
            //Đọc ma trận trọng số và lưu vào mảng weightMatrix
            int[][] weightMatrix = new int[n][n];

            for (int i = 0; i < n; i++) {
                String[] row = reader.readLine().split(" ");
                for (int j = 0; j < n; j++) {
                    weightMatrix[i][j] = Integer.parseInt(row[j]);
                }
            }

            //Đọc điểm bắt đầu và lưu vào biến poinStart
            String startPoint = reader.readLine();

            //Đọc thời gian bắt đầu và lưu vào các biến giờ và phút tương ứng
            // System.out.println("0: lấy thời gian thực \n 1: Lấy từ file")

            //Lấy từ file
            String[] startTimeV2 = reader.readLine().split(":");
            int startHour = Integer.parseInt(startTimeV2 [0]);
            int startMinute = Integer.parseInt(startTimeV2 [startTimeV2 .length-1]);
            
            List<TrafficJam> trafficJams = new ArrayList<>();
            String line = reader.readLine();
            while (line != null) {
                String[] parts = line.split(" ");
                String segment = parts[0];
                LocalTime startTime = LocalTime.parse(parts[1]);
                LocalTime endTime = LocalTime.parse(parts[3]);
                Duration duration = Duration.between(startTime, endTime);
                int delay = (int) duration.toMinutes();
                trafficJams.add(new TrafficJam(segment, startTime, endTime, delay));
                line = reader.readLine();
            }

            //tạo và trả về một đối tượng TSPProblem chứa các thông tin đã đọc
            return new TSPProblem(n, vertexNames, weightMatrix, trafficJams,startPoint,startHour,startMinute);
        } finally {
            //Đóng tệp tin sau khi đọc hoặc khi có lỗi đọc tệp tin
            if (reader != null) {
                reader.close();
            }
        }
    }


    //Hàm gán id cho các đỉnh
    private static void assignIDs(String[] vertexNames) {
        Map<String, Integer> idMap = new HashMap<>();
        int idCounter = 0;

        for (int i = 0; i < vertexNames.length; i++) {
            String vertexName = vertexNames[i];
            if (!idMap.containsKey(vertexName)) {
                idMap.put(vertexName, idCounter);
                idCounter++;
            }
        }

        // In ra kết quả gán ID cho các đỉnh
        for (Map.Entry<String, Integer> entry : idMap.entrySet()) {
            System.out.println("Vertex: " + entry.getKey() + ", ID: " + entry.getValue());
        }
    }

    
    private static int getVertexId(String vertexName, String[] vertexNames) {
        for (int i = 0; i < vertexNames.length; i++) {
            if (vertexNames[i].equals(vertexName)) {
                return i; 
            }
        }
        return -1; // Trả về -1 nếu không tìm thấy
    }



    private static void displayTSPProblem(TSPProblem tspProblem) {
        System.out.println("Number of vertices: " + tspProblem.getN());//hiển thị số đỉnh
        System.out.println("Vertex names: " + String.join(", ", tspProblem.getVertexNames()));//hiển thị tên đỉnh
        
        //hiển thị ma trận trọng số
        System.out.println("Weight matrix:");
        int[][] weightMatrix = tspProblem.getWeightMatrix();
        for (int i = 0; i < tspProblem.getN(); i++) {
            for (int j = 0; j < tspProblem.getN(); j++) {
                System.out.print(weightMatrix[i][j] + "\t");
            }
            System.out.println();
        }

        //hiển thị tên đỉnh bắt đầu
        System.out.println("Start point: " + tspProblem.getStartPoint() + " - " + getVertexId(tspProblem.getStartPoint(),tspProblem.getVertexNames()));
        //hiển thị thời gian bắt đầu
        System.out.println("Start time: "+tspProblem.startHour() + ":" + tspProblem.startMinute());


        System.out.println("Traffic jams:");
        List<TrafficJam> trafficJams = tspProblem.getTrafficJams();
        for (TrafficJam trafficJam : trafficJams) {
            System.out.println("Segment: " + trafficJam.getSegment());
            System.out.println("Start time: " + trafficJam.getStartTime());
            System.out.println("End time: " + trafficJam.getEndTime());
            TSPProblemConverter.convertSegmentToPoints(trafficJam.getSegment(), tspProblem.getVertexNames(), tspProblem.getWeightMatrix());
            System.out.println();
        }
    }

    class TSPProblemConverter {
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


    private static void solveTSP(TSPProblem tspProblem) {
        // Sử dụng dữ liệu trong tspProblem để giải quyết bài toán TSP
        int n = tspProblem.getVertexNames().length;
        bestPath = new int[n];
        bestDistance = Integer.MAX_VALUE;

        int[] currentPath = new int[n];
        boolean[] visited = new boolean[n];

        currentPath[0] = getVertexId(tspProblem.getStartPoint(), tspProblem.getVertexNames());
        visited[getVertexId(tspProblem.getStartPoint(), tspProblem.getVertexNames())] = true;
        tspRecursive(tspProblem, currentPath, visited, 1, 0);

        System.out.println("Shortest Path: ");
        for (int i = 0; i < n; i++) {
            if(i == n-1){
                System.out.print(tspProblem.getVertexNames()[bestPath[i]] + " -> " +tspProblem.getVertexNames()[bestPath[0]]);
                System.out.println();
                int startMinute = tspProblem.startMinute();
                startMinute+=bestDistance;
                int startHour = tspProblem.startHour();
                if( startMinute>=60)
                {
                    startMinute -= 60;
                    startHour+=1;
                }
                LocalTime currentStartTime = LocalTime.of(startHour, startMinute);
                System.out.println("Shortest Distance: " + currentStartTime);
                return;
            }
            System.out.print(tspProblem.getVertexNames()[bestPath[i]] + " -> ");            
        }    
    }

    private static void tspRecursive(TSPProblem tspProblem, int[] currentPath, boolean[] visited,
                                 int level, int currentDistance) {
    if (level == tspProblem.getVertexNames().length) {
        // Kiểm tra xem đường đi hiện tại có ngắn hơn đường đi ngắn nhất hiện tại không
        if (tspProblem.getWeightMatrix()[currentPath[level - 1]][currentPath[0]] >0
                && currentDistance + tspProblem.getWeightMatrix()[currentPath[level - 1]][currentPath[0]] < bestDistance) {
            System.arraycopy(currentPath, 0, bestPath, 0, tspProblem.getVertexNames().length);
            bestDistance = currentDistance + tspProblem.getWeightMatrix()[currentPath[level - 1]][currentPath[0]];
        }
        return;
    }

    // Xét các đỉnh kề của đỉnh hiện tại
    for (int i = 0; i < tspProblem.getVertexNames().length; i++) {
        if (!visited[i] && tspProblem.getWeightMatrix()[currentPath[level - 1]][i] > 0 ) {
            // Kiểm tra xem có tắc đường nào trên đoạn đường hiện tại hay không
            boolean hasTrafficJam = false;
            for (TrafficJam trafficJam : tspProblem.getTrafficJams()) {
                if (trafficJam.getSegment().equals(tspProblem.getVertexNames()[currentPath[level - 1]] + tspProblem.getVertexNames()[i])) {
                    int startHour = tspProblem.startHour();
                    int startMinute = tspProblem.startMinute();
                    LocalTime currentStartTime = LocalTime.of(startHour, startMinute).plusMinutes(currentDistance);
                    if (currentStartTime.isAfter(trafficJam.getStartTime()) && currentStartTime.isBefore(trafficJam.getEndTime())) {
                        currentDistance += trafficJam.getDelay();
                        hasTrafficJam = true;
                        break;
                    }
                }
            } 

            if (!hasTrafficJam) {
                currentPath[level] = i;
                visited[i] = true;
                tspRecursive(tspProblem, currentPath, visited, level + 1, currentDistance + tspProblem.getWeightMatrix()[currentPath[level - 1]][i]);
                visited[i] = false;
            }
        }
    }
}
    

}


class TSPProblem {
    private int n;//số lượng đỉnh
    private String[] vertexNames; // tên các đỉnh
    private int[][] weightMatrix; // ma trận trọng số thời gian
    private List<TrafficJam> trafficJams;
    private String startPoint; // điểm bắt đầu
    private int startHour;// giờ bắt đầu
    private int startMinute; // phút bắt đầu
    private int[] bestPath; // mảng lưu đường đi ngắn nhất
    private int bestDistance; // khoảng cách ngắn nhất

    public TSPProblem(int n, String[] vertexNames, int[][] weightMatrix, List<TrafficJam> trafficJams, String startPoint,int startHour, int startMinute) {
        this.n = n;
        this.vertexNames = vertexNames;
        this.weightMatrix = weightMatrix;
        this.trafficJams = trafficJams;
        this.startPoint = startPoint;
        this.startHour = startHour;
        this.startMinute = startMinute;
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

    public String getStartPoint()
    {
        return startPoint;
    }

    public int startHour()
    {
        return startHour;
    }

    public int startMinute()
    {
        return startMinute;
    }
}

class TrafficJam {
    private String segment;
    private LocalTime startTime;
    private LocalTime endTime;
    private int delay; // Thêm thuộc tính delay

    public TrafficJam(String segment, LocalTime startTime, LocalTime endTime, int delay) {
        this.segment = segment;
        this.startTime = startTime;
        this.endTime = endTime;
        this.delay = delay;
    }
    public String getSegment() {
        return segment;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public int getDelay() {
        return delay;
    }
}






