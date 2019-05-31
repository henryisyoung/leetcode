package google;

import java.util.*;

public class CarFleet {
    public static int carFleet(int target, int[] position, int[] speed) {
        int result = 0;
        if (position == null || speed == null || position.length == 0 || speed.length == 0) {
            return result;
        }
        List<Car> cars = new ArrayList<>();
        for (int i = 0; i < position.length; i++) {
            cars.add(new Car(target - position[i], speed[i]));
        }
        Collections.sort(cars, new Comparator<Car>() {
            @Override
            public int compare(Car o1, Car o2) {
                return o1.leftDist - o2.leftDist;
            }
        });
        int i = 0, j = 0;
        while (j < position.length) {
            result++;
            while (j < position.length && cars.get(j).time <= cars.get(i).time) {
                j++;
            }
            i = j;
        }
        return result;
    }

    static class Car{
        int speed, leftDist;
        double time;
        public Car(int leftDist, int speed) {
            this.leftDist = leftDist;
            this.speed = speed;
            this.time = 1.0 * leftDist / speed;
        }
    }

    public static void main(String[] args) {
        int target = 12;
        int[] position = {10,8,0,5,3}, speed = {2,4,1,1,3};
        System.out.println(carFleet(target, position, speed));
    }
}
