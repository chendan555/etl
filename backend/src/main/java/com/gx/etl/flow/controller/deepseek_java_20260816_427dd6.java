import java.util.ArrayList;
import java.util.List;

/**
 * 轨迹航程计算器
 * 文件名可任意命名（如 Test.java、Main.java 等）
 */
class TrajectoryRangeCalculator {

    // 地球平均半径（单位：米）
    private static final double EARTH_RADIUS = 6371000.0;

    /**
     * 轨迹点实体
     */
    static class TrajectoryPoint {
        double lat;      // 纬度（度）
        double lon;      // 经度（度）
        double alt;      // 海拔高度（米）
        double speed;    // 速度（米/秒）
        double heading;  // 航向（度，正北为0顺时针）

        public TrajectoryPoint(double lat, double lon, double alt, double speed, double heading) {
            this.lat = lat;
            this.lon = lon;
            this.alt = alt;
            this.speed = speed;
            this.heading = heading;
        }

        @Override
        public String toString() {
            return String.format("(%.4f, %.4f, %.1fm)", lat, lon, alt);
        }
    }

    /**
     * 角度转弧度
     */
    private static double toRadians(double deg) {
        return deg * Math.PI / 180.0;
    }

    /**
     * 计算两点间的水平大圆距离（米），使用Haversine公式
     */
    private static double horizontalDistance(TrajectoryPoint p1, TrajectoryPoint p2) {
        double lat1 = toRadians(p1.lat);
        double lon1 = toRadians(p1.lon);
        double lat2 = toRadians(p2.lat);
        double lon2 = toRadians(p2.lon);

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    /**
     * 计算相邻两点间的三维斜距（米）
     */
    private static double threeDDistance(TrajectoryPoint p1, TrajectoryPoint p2) {
        double hDist = horizontalDistance(p1, p2);
        double vDist = Math.abs(p2.alt - p1.alt);
        return Math.sqrt(hDist * hDist + vDist * vDist);
    }

    /**
     * 计算整条轨迹的总航程（米）
     */
    public static double calculateTotalRange(List<TrajectoryPoint> points) {
        if (points == null || points.size() < 2) {
            return 0.0;
        }

        double totalRange = 0.0;
        for (int i = 0; i < points.size() - 1; i++) {
            double segDist = threeDDistance(points.get(i), points.get(i + 1));
            totalRange += segDist;
        }
        return totalRange;
    }

    /**
     * 计算分段统计信息（调试用）
     */
    public static void printSegmentStats(List<TrajectoryPoint> points) {
        if (points == null || points.size() < 2) {
            System.out.println("数据点不足，无法统计");
            return;
        }

        double totalRange = 0.0;
        System.out.println("========== 分段位移统计 ==========");
        for (int i = 0; i < points.size() - 1; i++) {
            double seg = threeDDistance(points.get(i), points.get(i + 1));
            totalRange += seg;

            double speedMps = points.get(i).speed;
            double expectedDist = speedMps * 1.0; // 1秒间隔
            double error = seg - expectedDist;
            double errorPercent = (expectedDist > 0) ? (error / expectedDist * 100) : 0;

            System.out.printf("第 %d 秒: 位移=%.2f m, 速度=%.1f m/s, 理论=%.2f m, 误差=%.2f m (%.1f%%)\n",
                    i + 1, seg, speedMps, expectedDist, error, errorPercent);
        }

        System.out.println("========== 总航程统计 ==========");
        System.out.printf("总三维航程: %.2f 米 = %.3f 公里 = %.2f 海里\n",
                totalRange, totalRange / 1000.0, totalRange / 1852.0);

        // 额外统计：水平射程和垂直爬升
        double totalHorizontal = 0.0;
        double totalVertical = 0.0;
        for (int i = 0; i < points.size() - 1; i++) {
            totalHorizontal += horizontalDistance(points.get(i), points.get(i + 1));
            totalVertical += Math.abs(points.get(i + 1).alt - points.get(i).alt);
        }
        System.out.printf("水平射程: %.2f 米, 垂直爬升总量: %.2f 米\n", totalHorizontal, totalVertical);
    }

    // ---------- 测试入口 ----------
    public static void main(String[] args) {
        // 使用 ArrayList 替代 List.of()，兼容 JDK 7/8
        List<TrajectoryPoint> points = new ArrayList<TrajectoryPoint>();
        points.add(new TrajectoryPoint(30.0, 120.0, 1000.0, 250.0, 45.0));
        points.add(new TrajectoryPoint(30.005, 120.008, 1050.0, 260.0, 47.0));
        points.add(new TrajectoryPoint(30.010, 120.015, 1100.0, 255.0, 50.0));

        printSegmentStats(points);
    }
}