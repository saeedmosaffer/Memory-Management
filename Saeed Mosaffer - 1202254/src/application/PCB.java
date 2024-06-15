package application;

public class PCB {
    int id;
    int size;
    int time;
    int arrivalTime;
    int base;
    int limit;

    public PCB(int id, int size, int time, int arrivalTime, int base, int limit) {
        this.id = id;
        this.size = size;
        this.time = time;
        this.arrivalTime = arrivalTime;
        this.base = base;
        this.limit = limit;
    }
}
