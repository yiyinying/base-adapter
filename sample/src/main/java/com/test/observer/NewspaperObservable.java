package com.test.observer;

/**
 * 报纸：被观察者
 */
public class NewspaperObservable extends Observable {
	public void notifyAllMan(String info) {
		setChanged();
		notifyObservers(info);
	}
}
