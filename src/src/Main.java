package src;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        Semaphore sem = new Semaphore(2); // 1 разрешение
        CommonResource res = new CommonResource();

        (new  CountThread(res, sem,"Клинет 1")).start();
        (new CountThread(res, sem, "Клиент 2")).start();
        (new CountThread(res, sem, "Клиент 3")).start();
    }
}

class CommonResource {
    int x = 100;
}
class CountThread extends Thread {
    CommonResource res;
    Semaphore sem;
    String name;

    public CountThread(CommonResource res, Semaphore sem, String name) {
        this.res = res;
        this.sem = sem;
        this.name = name;
    }

    public void run() {
        try {
            System.out.println(name + " ожидает разрешение");
            sem.acquire();

            {
                System.out.println(name + " работает");
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        System.out.println(name + " освобождает разрешение");
        sem.release();
    }
}



