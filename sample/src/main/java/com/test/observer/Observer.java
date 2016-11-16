package com.test.observer;

/**
 * 观察者 接口
 */
public interface Observer {
	/**
	 * @param o
	 *            被观察者对象
	 * @param arg
	 *            notifyObservers方法的参数.传递给 观察者
	 */
	void update(Observable o, Object arg);
}
