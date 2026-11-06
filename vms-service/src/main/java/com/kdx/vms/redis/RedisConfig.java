package com.kdx.vms.redis;

import java.util.Collections;
import java.util.Set;

import redis.clients.jedis.HostAndPort;

/**
 * 
 * @author zhangguodong
 *
 */
public class RedisConfig {

	/** 配置信息 127.0.0.1 **/
//	private String address;
	/**端口 6379**/
//	private Integer port;
	
	
	/**密码**/
	private String passWord;
	/** 最大空闲连接数, 默认8个 **/
	private int maxIdle = 8;
	
	private Set<HostAndPort> hostAndPortSet = Collections.emptySet();
	
	public Set<HostAndPort> getHostAndPortSet() {
		return hostAndPortSet;
	}
	public void setHostAndPortSet(Set<HostAndPort> hostAndPortSet) {
		this.hostAndPortSet = hostAndPortSet;
	}
	
	public String getPassWord() {
		return passWord;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	public Integer getMaxRedirections() {
		return maxRedirections;
	}
	public void setMaxRedirections(Integer maxRedirections) {
		this.maxRedirections = maxRedirections;
	}
	/** 最小空闲连接数, 默认0 **/
	private int minIdle = 1;
	/** 最大连接数, 默认8个 **/
	private int maxTotal = 8;
	/** 每个节点最大连接超时时间, 默认1000秒 **/
	private int timeout = 1000;
	/** 获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间, 默认-1 **/
	private long maxWaitMillis = -1;
	/** 逐出连接的最小空闲时间 默认60000毫秒(1分钟) **/
	private long minEvictableIdleTimeMillis = 60000;
	/** 每次逐出检查时 逐出的最大数目 如果为负数就是 : 1/abs(n), 默认-1 **/
	private int numTestsPerEvictionRun = -1;
	/**
	 * 对象空闲多久后逐出, 当空闲时间>该值 且 空闲连接>最大空闲数 时直接逐出,不再根据MinEvictableIdleTimeMillis判断
	 * (默认逐出策略)
	 **/
	private long softMinEvictableIdleTimeMillis = 1000L * 60L * 30L;
	/** 在获取连接的时候检查有效性, 默认false **/
	private boolean testOnBorrow;
	/** 在返回连接给pool的时候，检查有效性, 默认false **/
	private boolean testOnReturn;
	/** 在空闲时检查有效性, 默认true **/
	private boolean testWhileIdle = true;
	/** 逐出扫描的时间间隔(毫秒) 如果为负数,则不运行逐出线程, 默认 30000 **/
	private long timeBetweenEvictionRunsMillis = 30000;
	/** 连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true **/
	private boolean blockWhenExhausted = true;
	/** 是否启用后进先出, 默认true **/
	private boolean lifo = true;
	/** 是否开启cluster集群模式 **/
	private boolean cluster;
	/** **/
	private Integer maxRedirections;
	
//	public String getAddress() {
//		return address;
//	}
//	public void setAddress(String address) {
//		this.address = address;
//	}
	public int getMaxIdle() {
		return maxIdle;
	}
	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}
	public int getMinIdle() {
		return minIdle;
	}
	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}
	public int getMaxTotal() {
		return maxTotal;
	}
	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	public long getMaxWaitMillis() {
		return maxWaitMillis;
	}
	public void setMaxWaitMillis(long maxWaitMillis) {
		this.maxWaitMillis = maxWaitMillis;
	}
	public long getMinEvictableIdleTimeMillis() {
		return minEvictableIdleTimeMillis;
	}
	public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
		this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
	}
	public int getNumTestsPerEvictionRun() {
		return numTestsPerEvictionRun;
	}
	public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
		this.numTestsPerEvictionRun = numTestsPerEvictionRun;
	}
	public long getSoftMinEvictableIdleTimeMillis() {
		return softMinEvictableIdleTimeMillis;
	}
	public void setSoftMinEvictableIdleTimeMillis(
			long softMinEvictableIdleTimeMillis) {
		this.softMinEvictableIdleTimeMillis = softMinEvictableIdleTimeMillis;
	}
	public boolean isTestOnBorrow() {
		return testOnBorrow;
	}
	public void setTestOnBorrow(boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}
	public boolean isTestOnReturn() {
		return testOnReturn;
	}
	public void setTestOnReturn(boolean testOnReturn) {
		this.testOnReturn = testOnReturn;
	}
	public boolean isTestWhileIdle() {
		return testWhileIdle;
	}
	public void setTestWhileIdle(boolean testWhileIdle) {
		this.testWhileIdle = testWhileIdle;
	}
	public long getTimeBetweenEvictionRunsMillis() {
		return timeBetweenEvictionRunsMillis;
	}
	public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
		this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
	}
	public boolean isBlockWhenExhausted() {
		return blockWhenExhausted;
	}
	public void setBlockWhenExhausted(boolean blockWhenExhausted) {
		this.blockWhenExhausted = blockWhenExhausted;
	}
	public boolean isLifo() {
		return lifo;
	}
	public void setLifo(boolean lifo) {
		this.lifo = lifo;
	}
	public boolean isCluster() {
		return cluster;
	}
	public void setCluster(boolean cluster) {
		this.cluster = cluster;
	}
//	public Integer getPort() {
//		return port;
//	}
//	public void setPort(Integer port) {
//		this.port = port;
//	}
}
