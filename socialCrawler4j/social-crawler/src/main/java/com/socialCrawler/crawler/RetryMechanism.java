package com.socialCrawler.crawler;

public class RetryMechanism {
	private long lastSleepTime;
	private long timeFactor;
	private RETRY_TYPE retryType;
	public enum RETRY_TYPE{
		linear,
		exponential
	}
	
	public RetryMechanism(long timeFactor, long finalCap, RETRY_TYPE retryType){
		this.lastSleepTime = 0;
		this.timeFactor = timeFactor;
		this.retryType = retryType;
	}
	
	private long getPrevSleepTime(){
		return lastSleepTime;
	}
	
	private void setPrevSleepTime(long sleepTime){
		lastSleepTime = sleepTime;
	}
	
	public void BackOff() throws InterruptedException{
		long timeToSleep = getPrevSleepTime();
		if(retryType.equals(RETRY_TYPE.exponential)){
			timeToSleep +=  timeFactor;
			setPrevSleepTime(timeToSleep);
		}else{
			timeToSleep *=  timeFactor;
			setPrevSleepTime(timeToSleep);
		}
		
		Thread.sleep(timeToSleep);
	}
	
	public void reset(){
		lastSleepTime = 0;
	}

}

