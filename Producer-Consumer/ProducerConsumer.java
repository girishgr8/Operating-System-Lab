import java.util.*;
import java.io.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ProducerConsumer{
	public static void main(String[] args) {
		System.out.println("\t\tProducer-Consumer Problem using Semaphores\n");
		try{	
			SharedResource src = new SharedResource();
			Producer producer = new Producer(src);
			Consumer consumer = new Consumer(src);
			producer.setName("Producer");
			consumer.setName("Consumer");
			producer.start();
			Thread.currentThread().sleep(1000);
			consumer.start();
		}catch(Exception e){}	
	}
}

class Producer extends Thread implements Runnable{
	SharedResource src;
	public Producer(SharedResource src) {
		this.src = src;
	}

	public void run() {
		while(true){
			try{
				System.out.println("Producer Thread Running");
				Semaphore.waitSemaphore("Consumer", src);
				System.out.println("Producer acquired a lock !");
				src.produceItem();
				Semaphore.signalSemaphore(src);
				System.out.println("Lock released, Semaphore value from Producer = "+Semaphore.semaphore+"\n");
				Thread.currentThread().sleep(1000);
			}catch(Exception e){}
		}
	}
}

class Consumer extends Thread implements Runnable{
	SharedResource src;
	public Consumer(SharedResource src) {
		this.src = src;
	}

	public void run() {
		while(true){
			try{
				System.out.println("Consumer Thread Running");
				Semaphore.waitSemaphore("Producer", src); // Acquire lock ....
				System.out.println("Consumer acquired a lock !");
				src.consumeItem();
				Semaphore.signalSemaphore(src); //release lock...
				System.out.println("Lock released, Semaphore value from Consumer = "+Semaphore.semaphore+"\n");
				Thread.currentThread().sleep(1000);
			}catch(Exception e){}
		}
	}
}

class Semaphore{
	static AtomicInteger semaphore = new AtomicInteger(1);	
	
	public Semaphore(){}
	
	synchronized public static void signalSemaphore(SharedResource src){
		try{
			//System.out.println("semaphore.intValue = "+semaphore.intValue());
			if(semaphore.intValue()==0)
				semaphore.getAndIncrement();
			else
				semaphore.getAndDecrement();
			//System.out.println("From signalSemaphore method: "+semaphore);
			src.notify();
		}catch(Exception e){}
	}
	
	synchronized public static void waitSemaphore(String name, SharedResource src){
		boolean once = true;
		try{
			while(semaphore.intValue()==0){
				if(once==true){
					System.out.println(name+" has an exclusive lock over shared resource."); 
					once=false;
				}
			}
			semaphore.getAndDecrement();
		}catch(Exception e){}
	}
}


class SharedResource extends Thread {
	int item = 1;
	SharedResource(){}

	public synchronized void produceItem(){
		System.out.println("Producer inside Critical Section.\nProducer has produced item "+item);
		item++;
	}
	public synchronized void consumeItem(){
		System.out.println("Consumer inside Critical Section.\nConsumer has consumed item "+item);
	}
}


