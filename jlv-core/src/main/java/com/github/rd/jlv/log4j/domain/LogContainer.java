package com.github.rd.jlv.log4j.domain;


public final class LogContainer {

	// Generics for CircularFifoBuffer will be provided in commons-collections v.4.0
	// TODO: use replace current 3.2.1 commons-collections version to 4.0 when it releases
//	private final CircularFifoBuffer container;
//
//	public LogContainer(int bufferSize) {
//		container = new CircularFifoBuffer(bufferSize);
//	}
//
//	public int size() {
//		return container.size();
//	}
//
//	public void add(Log log) {
//		container.add(log);
//	}
//
//	public Log get() {
//		return (Log) container.get();
//	}
//
//	public Collection<Log> getLogs() {
//		return container;
//	}
//
//	public Iterator<Log> iterator() {
//		return container.iterator();
//	}
//
//	public void clear() {
//		container.clear();
//	}
//
//	public boolean contains(Log log) {
//		return container.contains(log);
//	}
}
