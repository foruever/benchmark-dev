package com.ruc.model;
/**
 * 记录时间点
 * @author sxg
 *
 */
public class RecordTime {
	private long startTime=System.currentTimeMillis();//开始时间  出事后不变化
	private long endTime=System.currentTimeMillis()+1000*3600*24;//结束时间
	private long currentTime=startTime;//模拟当前时间
	private long timestep=1000L;//步长
	private long cacheLine=512L;//缓存行数
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	public long getCurrentTime() {
		return currentTime;
	}
	public void setCurrentTime(long currentTime) {
		this.currentTime = currentTime;
	}
	public long getTimestep() {
		return timestep;
	}
	public void setTimestep(long timestep) {
		this.timestep = timestep;
	}
	public long getCacheLine() {
		return cacheLine;
	}
	public void setCacheLine(long cacheLine) {
		this.cacheLine = cacheLine;
	}
	/**
	 * 加一个步长
	 */
	public synchronized void  addStep(){
		this.currentTime=getCurrentTime()+getTimestep();
	}
	/**
	 * 获取当前相对时间 ，当前绝对时间减去相对时间，统一处理，方便修改
	 * 注意序列化
	 */
	public Long getRaletiveTime() {
//		Long relativeNum=getCurrentTime()-getStartTime(); //FIXME 改为针对真正时间的函数
		Long relativeNum=getCurrentTime();
		return relativeNum;
	}
	/**
	 * 
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @param currentTime 当前时间
	 * @param timestep 步长
	 * @param cacheLine 缓存行数
	 */
	public RecordTime(long startTime, long endTime, long currentTime,
			long timestep, long cacheLine) {
		super();
		this.startTime = startTime;
		this.endTime = endTime;
		this.currentTime = currentTime;
		this.timestep = timestep;
		this.cacheLine = cacheLine;
	}
	public RecordTime(long startTime, long endTime,
			long timestep, long cacheLine) {
		super();
		this.startTime = startTime;
		this.endTime = endTime;
		this.currentTime = startTime;
		this.timestep = timestep;
		this.cacheLine = cacheLine;
	}
	/**
	 * 开始时间和当前时间使用系统时间
	 * @param endTime
	 * @param timestep
	 * @param cacheLine
	 */
	public RecordTime(long endTime,
			long timestep, long cacheLine) {
		super();
		this.endTime = endTime;
		this.timestep = timestep;
		this.cacheLine = cacheLine;
	}
	public RecordTime() {
		super();
	}
}
