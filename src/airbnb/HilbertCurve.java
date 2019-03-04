package airbnb;

public class HilbertCurve {
    public static int hilbertCurve(int x, int y, int iter) {
        if (iter == 0) return 1;
        int len = 1 << (iter - 1); // 区域移动的长度
        int num = 1 << (2 * (iter - 1)); // 每一块区域边长的边界值

        if (x >= len && y >= len) { //右上角区域 = 前一阶往右上角移动borderLen
            // 3 Shape is identical with previous iteration
            return 2 * num + hilbertCurve(x - len, y - len, iter - 1);
        } else if (x < len && y >= len) { //左上角区域 = 前一阶往上移动borderLen
            // 2 Shape is identical with previous iteration
            return num + hilbertCurve(x, y - len, iter - 1);
        } else if (x < len && y < len) {  //左下角区域 = 前一阶按照y=x对称
            // 1 Clock-wise rotate 90
            return hilbertCurve(y, x, iter - 1);
        } else {
            // 4 Anti-Clockwise rotate 90
            //右下角区域 = 前一阶按照y=-x对称，然后右移2*borderLen - 1，上移borderLen - 1
            // 设原来坐标(a,b) => (-b, -a) => (2*borderLen - 1 - b, borderLen - 1 - a) = (x, y)
            // => a = borderLen - 1 - y, b = 2*borderLen - 1 - x
            return 3 * num + hilbertCurve(len - y - 1, 2 * len - x - 1, iter - 1);
        }
    }

    public static int hilbertCurve2(int x, int y, int iter) {
        if (iter == 0 ){
            return 1;
        }
        int len = 1 << (iter - 1);       // 一个象限的宽度
        int num = 1 << (2 * (iter - 1)); // 一个象限走多少步 初始为1步
        if (x < len && y < len) {
            return 0 * num + hilbertCurve2(y, x, iter - 1);
        } else if (x < len && y >= len) {
            return 1 * num + hilbertCurve2(x, y - len, iter - 1);
        } else if (x >= len && y >= len) {
            return 2 * num + hilbertCurve2(x - len, y - len, iter - 1);
        } else {
            return 3 * num + hilbertCurve2(len - 1 - y, 2 * len - 1 - x, iter - 1);
        }
    }

    public static int hilbertCurve3(int x, int y, int iter) {
        if (iter == 0) {
            return 1;
        }
        int len = 1 << (iter - 1), num = 1 << (2 * iter - 2);
        if (x < len && y < len) {
            return 0 * num + hilbertCurve3(y, x, iter - 1);
        } else if (x < len && y >= len) {
            return 1 * num + hilbertCurve3(x, y - len, iter - 1);
        } else if (x >= len && y >= len) {
            return 2 * num + hilbertCurve3(x - len, y - len, iter - 1);
        } else {
            return 3 * num + hilbertCurve3(len - y - 1, 2 * len - x - 1, iter - 1);
        }
    }
    
    public static void main(String[] args) {
        int x = 1, y = 1, iter = 3;
        int x1 = 0, y1 = 1, iter1 = 1;
        System.out.println(hilbertCurve(x, y, iter) == hilbertCurve2(x, y, iter));
        System.out.println(hilbertCurve(x1, y1, iter1) == hilbertCurve2(x1, y1, iter1));
    }
}
