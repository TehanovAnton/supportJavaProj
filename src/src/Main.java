package src;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        LinkedList<Connect> list = new LinkedList<Connect>(){
            {
                this.add(new Connect(1));
                this.add(new Connect(2));
                this.add(new Connect(3));
            }
        };

        ConnectionPool<Connect> pool = new ConnectionPool<>(list);
        for (int i = 0; i < 10; i++)
            new  Client(pool).start();
    }
}

class ConnectionPool <T> {
    private final static int POOL_SIZE = 3; // размер пула
    private final Semaphore semaphore = new Semaphore(POOL_SIZE, true);
    private final Queue<T> resources = new LinkedList<T>();
    public ConnectionPool(Queue<T> source){
        resources.addAll(source);
    }

    public T getResource(long maxWaitMillis) throws Exception {
        try {
            if (semaphore.tryAcquire(maxWaitMillis, TimeUnit.MILLISECONDS)) {
                T  res  =  resources.poll(); return res;
            }
        } catch (InterruptedException e) {
            throw new Exception(e);
        }

        throw new Exception(":превышено время ожидания");
    }

    public void returnResource(T res){
        resources.add(res); // возвращение экземпляра в пул
        semaphore.release();
    }
}

class Connect {
    private int connectID;
    public Connect(int id) {
        super(); this.connectID = id;
    }
    public int getConnectID() {
        return connectID;
    }

    public void setConnectID(int id) {
        this.connectID = id;
    }

    public void using() {
        try {
            // использование соединения
            Thread.sleep(new Random().nextInt(500));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Client extends Thread {
    private boolean reading = false;
    private ConnectionPool<Connect> pool;

    public Client (ConnectionPool<Connect> pool) {
        this.pool = pool;
    }

    public void run() {
        Connect connect = null;
        try {
            connect  =  pool.getResource(500);  // изменить на 100
            reading  =  true;

            System.out.println("Соединение Клиент #" + this.getId() + "  соединение #" + connect.getConnectID());
            connect.using();
        }
        catch (Exception e) {
            System.out.println("Клиент #" + this.getId() + " отказано в соединении ->" +  e.getMessage());
        }
        finally {
            if (connect != null) {
                reading  =  false;
                System.out.println("Соединение Клиент #" + this.getId() + " : " + connect.getConnectID() + " отсоединился");
                pool.returnResource(connect);
            }
        }
    }

    public boolean isReading() {
        return reading;
    }
}


