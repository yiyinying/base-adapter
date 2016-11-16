package com.test.observer;

import java.util.ArrayList;

/**
 * 被观察者对象
 */
public class Observable {
	private boolean changed = false;
	private final ArrayList<Observer> observers;

	public Observable() {
		observers = new ArrayList<>();
	}

	public synchronized void addObserver(Observer o) {
		if (o == null)
			throw new NullPointerException();
		if (!observers.contains(o)) {
			observers.add(o);
		}
	}

	public synchronized void deleteObserver(Observer o) {
		observers.remove(o);
	}

	public void notifyObservers() {
		notifyObservers(null);
	}

	public void notifyObservers(Object arg) {
		/**
		 * a temporary array buffer, used as a snapshot of the state of current Observers.
		 * 作为当前观察者状态的快照的临时数组缓冲区。
		 */
		Observer[] arrLocal;

		synchronized (this) {
			if (!hasChanged())
				return;
			arrLocal = observers.toArray(new Observer[observers.size()]);
			clearChanged();
		}
		for (int i = arrLocal.length - 1; i >= 0; i--)
			arrLocal[i].update(this, arg);
	}

	/**
	 * Clears the observer list so that this object no longer has any observers.
	 */
	public synchronized void deleteObservers() {
		observers.clear();
	}

	protected synchronized void setChanged() {
		changed = true;
	}

	protected synchronized void clearChanged() {
		changed = false;
	}

	public synchronized boolean hasChanged() {
		return changed;
	}

	public synchronized int countObservers() {
		return observers.size();
	}
}
