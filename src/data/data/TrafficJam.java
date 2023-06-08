    package data;

    import java.time.LocalTime;

    public class TrafficJam {
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
