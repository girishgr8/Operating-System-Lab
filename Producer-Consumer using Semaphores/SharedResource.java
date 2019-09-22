import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

class Producer extends Thread implements Runnable{
	SharedResource src;
	public Producer() {}
	public  Producer(SharedResource src){
		this.src = src;
	}
	
	Random r = new Random();
	
	public void run() {
		while(true){
		try {
			
			Thread.currentThread().sleep(1000);
			System.out.println("Producer Thread Running");
			int maxProdLimit = r.nextInt(src.n-1);
			System.out.println("Maximum Production Limit is: "+maxProdLimit);
			boolean once = true;
			while(src.semaphore.intValue()<=0){if(once==true){System.out.println("Consumer has an exclusive lock over shared resource"); once=false;}};
			src.semaphore.compareAndSet(1,0);
			System.out.println("Semaphore value from Producer is: "+src.semaphore);
			for(int i=0;i<maxProdLimit;i++) {
				if(src.empty.intValue()!=0) 
					System.out.println("Producer produced item "+src.produceItem());
				else {
					src.semaphore.compareAndSet(0,1);
					System.out.println("There is no space to store newly produced resources.");
					System.out.println("Lock released, Semaphore value from Producer = "+src.semaphore);
					src.notifyConsumer();
				}
			}
			if(src.semaphore.intValue()==0)
				src.semaphore.compareAndSet(0,1);
			System.out.println("Lock released (completed), Semaphore value from Producer = "+src.semaphore);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

class Consumer extends Thread implements Runnable{
	SharedResource src;
	
	public Consumer() {}
	public Consumer(SharedResource src) {
		this.src = src;
	}
	
	Random r = new Random();
	
	public void run() {
		while(true){
			try {
				Thread.currentThread().sleep(1000);
				System.out.println("Consumer Thread Running");
				int maxConsLimit =  r.nextInt(src.n-1);
				System.out.println("Maximum Consumption Limit is: "+maxConsLimit);
				boolean once = true;
				while(src.semaphore.intValue()<=0){if(once==true){System.out.println("Producer has an exclusive lock over shared resource"); once=false;}};
				src.semaphore.compareAndSet(1,0);
				System.out.println("Semaphore value from Consumer is: "+src.semaphore);
				for(int i=0;i<maxConsLimit;i++) {
					if(src.filled.intValue()!=0) 
						System.out.println("Consumer consumed item "+src.consumeItem());	
					else {
						src.semaphore.compareAndSet(0,1);
						System.out.println("There are no resources available to consume.");
						System.out.println("Lock released, Semaphore value from Consumer = "+src.semaphore);
						src.notifyProducer();
					}
				}
				if(src.semaphore.intValue()==0)
					src.semaphore.compareAndSet(0,1);
				System.out.println("Lock released (completed), Semaphore value from Consumer = "+src.semaphore);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}



public class SharedResource{
	static Scanner sc = new Scanner(System.in);
	static SharedResource src = new SharedResource();
	Random r = new Random();
	public int n = r.nextInt(r.nextInt(10)+1)+5;
	int queue[] = new int[n];
	AtomicInteger semaphore = new AtomicInteger(1); 
	AtomicInteger empty = new AtomicInteger(n);
	AtomicInteger filled = new AtomicInteger(0);
	int item = 1;
	int lastProducedIndex = 0;
	int lastConsumedIndex = 0;
	static Producer producer;
	static Consumer consumer; 
	public static void main(String[] args) {
		System.out.println("\t\tProducer-Consumer Problem using Semaphores\n");
		new SharedResource().printInfo();
		try{
			producer = new Producer(src);
			consumer = new Consumer(src);
			producer.setName("Producer");
			consumer.setName("Consumer");
			producer.start();
			consumer.start();
			producer.join(2000);
			consumer.join(1000);	
		}catch(Exception e){}	
		//Producer producer = new Producer();
		//Consumer consumer = new Consumer();
	}
	
	public void printInfo() {
		System.out.println("Maximum queue size = "+n);
	}
	
	public int produceItem() {
		queue[lastProducedIndex%n]=item;
		lastProducedIndex++;
		filled.incrementAndGet();
		empty.decrementAndGet();
		item++;
		return (item-1);
	}
	
	public int consumeItem() {
		int consumedItem = queue[lastConsumedIndex%n];
		lastConsumedIndex++;
		filled.decrementAndGet();
		empty.incrementAndGet();
		return (consumedItem);
	}
	
	public void notifyConsumer(){
		try{
			System.out.println("Notifying Consumer");
			consumer.notify();
			producer.wait(1000);
		}catch(Exception e){}
	}
	
	public void notifyProducer(){
		try{
			System.out.println("Notifying Producer");
			producer.notify();
			consumer.wait(1000);
		}catch(Exception e){}
	}
}
