package uber.phone.ParkingLot;

import java.util.*;

// https://www.1point3acres.com/bbs/thread-905395-1-1.html
public class ParkingLot {

    public static void main(String[] args) {
        try {
            ParkingLot lot = new ParkingLot(2);
            Vehicle v1 = new Vehicle(Size.LARGE, Type.VAN);
            Vehicle c1 = new Vehicle(Size.COMPACT, Type.Car);
            Vehicle m1 = new Vehicle(Size.SMALL, Type.MOTOR);

            System.out.println(lot.park(v1));
            System.out.println(lot.park(c1));
            System.out.println(lot.park(m1));
            System.out.println("=====");
            System.out.println(v1.getSpots());
            System.out.println(m1.getSpots());
            System.out.println(c1.getSpots());
            System.out.println("=====");
            m1.leave();
            c1.leave();
            v1.leave();
            System.out.println(v1.getSpots());
            System.out.println(m1.getSpots());
            System.out.println(c1.getSpots());
            System.out.println("=====");
        }catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    Queue<ParkingSpot> compactSpots, largeSpots, smallSpots;
    int availableSpots;
    public ParkingLot(int n) {
        initSpots(n);
        this.availableSpots = n;
    }

    private void initSpots(int n) {
        this.largeSpots = new LinkedList<>();
        this.compactSpots = new LinkedList<>();
        this.smallSpots = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (i % 3 == 0) {
                Size size = Size.COMPACT;
                compactSpots.add(new ParkingSpot(i, size));
            } else if (i % 3 == 1){
                Size size = Size.LARGE;
                largeSpots.add(new ParkingSpot(i, size));
            } else {
                Size size = Size.SMALL;
                smallSpots.add(new ParkingSpot(i, size));
            }
        }
    }

    public List<ParkingSpot> park(Vehicle vehicle) {
        switch (vehicle.vehicleType) {
            case Car:
                return parkCar(vehicle);
            case VAN:
                return parkVan(vehicle);
            case MOTOR:
                return parkMotor(vehicle);
            default:
                return null;
        }
    }

    private List<ParkingSpot> parkCar(Vehicle vehicle) {
        if (compactSpots.size() +  largeSpots.size() < 1) {
            throw new IllegalStateException("NO SPACE FOR CAR");
        }
        availableSpots--;
        if (compactSpots.size() > 0) {
            ParkingSpot spot = compactSpots.poll();
            List<ParkingSpot> spots = Arrays.asList(spot);
            vehicle.park(spots);
            return spots;
        } else {
            ParkingSpot spot = largeSpots.poll();
            List<ParkingSpot> spots = Arrays.asList(spot);
            vehicle.park(spots);
            return spots;
        }
    }

    private List<ParkingSpot> parkVan(Vehicle vehicle) {
        if (largeSpots.isEmpty() && compactSpots.size() < 3) {
            throw new IllegalStateException("NO SPACE FOR VAN");
        }
        if (!largeSpots.isEmpty()) {
            availableSpots--;
            ParkingSpot spot = largeSpots.poll();
            List<ParkingSpot> spots = Arrays.asList(spot);
            vehicle.park(spots);
            return spots;
        } else {
            availableSpots -= 3;
            List<ParkingSpot> spots = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                spots.add(compactSpots.poll());
            }
            vehicle.park(spots);
            return spots;
        }
    }

    private List<ParkingSpot> parkMotor(Vehicle vehicle) {
        if (availableSpots <= 0) {
            throw new IllegalStateException("NO SPACE FOR MOTOR");
        }
        availableSpots--;
        if (!smallSpots.isEmpty()) {
            ParkingSpot spot = smallSpots.poll();
            List<ParkingSpot> spots = Arrays.asList(spot);
            vehicle.park(spots);
            return spots;
        } else if (compactSpots.size() > 0) {
            ParkingSpot spot = compactSpots.poll();
            List<ParkingSpot> spots = Arrays.asList(spot);
            vehicle.park(spots);
            return spots;
        } else {
            ParkingSpot spot = largeSpots.poll();
            List<ParkingSpot> spots = Arrays.asList(spot);
            vehicle.park(spots);
            return spots;
        }
    }

    public void remove(Vehicle vehicle) {
        List<ParkingSpot> spots = vehicle.leave();
        availableSpots += spots.size();
        for (ParkingSpot spot : spots) {
            switch (spot.size) {
                case COMPACT:
                    compactSpots.add(spot);
                case LARGE:
                    largeSpots.add(spot);
            }
        }
    }
}

class Vehicle {
    Size size;
    Type vehicleType;
    List<ParkingSpot> spots;
    public Vehicle(Size size, Type type) {
        this.size = size;
        this.vehicleType = type;
    }

    public void park(List<ParkingSpot> spots) {
        this.spots = spots;
    }

    public List<ParkingSpot> leave() {
        List<ParkingSpot> leaveSpots = spots;
        spots = new ArrayList<>();
        return leaveSpots;
    }

    public List<ParkingSpot> getSpots() {
        return spots;
    }
}

enum Type {
    Car, MOTOR, VAN
}

class ParkingSpot {
    Size size;
    int id;
    public ParkingSpot(int id, Size size) {
        this.size = size;
        this.id = id;
    }

    @Override
    public String toString() {
        return "ParkingSpot{" +
                "size=" + size +
                ", id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParkingSpot)) return false;
        ParkingSpot spot = (ParkingSpot) o;
        return id == spot.id && size == spot.size;
    }

    @Override
    public int hashCode() {
        return Objects.hash(size, id);
    }
}

enum Size {
    LARGE, COMPACT, SMALL;
}
