package bo.buffalo.rest;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SyncLink{
    private ReentrantLock lock = new ReentrantLock();
    public Lock getLock(){
       return lock;
    }
}
