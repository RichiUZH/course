package com.agentecon.web;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;

public class SoftCache<K, V> {

	private HashMap<K, SoftReference<V>> simulations;

	public SoftCache() {
		this.simulations = new HashMap<>();
	}

	public synchronized V getOrSetCachedItem(K key, V value) {
		V prev = get(key);
		if (prev == null) {
			put(key, value);
			return value;
		} else {
			return prev;
		}
	}

	public synchronized void put(K key, V value) {
		if (value == null) {
			this.simulations.remove(key);
		} else {
			this.simulations.put(key, new SoftReference<V>(value));
		}
	}

	public synchronized V get(K handle) {
		SoftReference<V> ref = simulations.get(handle);
		if (ref != null) {
			V stepper = ref.get();
			if (stepper == null) {
				cleanup();
			}
			return stepper;
		} else {
			return null;
		}
	}

	private void cleanup() {
		Iterator<SoftReference<V>> iter = simulations.values().iterator();
		while (iter.hasNext()) {
			if (iter.next().get() == null) {
				iter.remove();
			}
		}
	}

}
