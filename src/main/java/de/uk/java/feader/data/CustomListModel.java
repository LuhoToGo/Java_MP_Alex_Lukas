package de.uk.java.feader.data;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class CustomListModel<T> implements ListModel<T> {
	
	protected List<T> listData;
	private List<ListDataListener> listeners;
	
	
	public CustomListModel() {
		listData = new ArrayList<T>();
		listeners = new ArrayList<ListDataListener>();
	}

	@Override
	public int getSize() {
		return listData.size();
	}

	@Override
	public T getElementAt(int index) {
		return listData.get(index);
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		listeners.add(l);
		//l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED,0 , 3));
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listeners.remove(l);
	}
	
	/**
	 * Clears the data model
	 */
	public void clear() {
		int size = listData.size();
		listData.clear();
		broadcastListDataChange(ListDataEvent.INTERVAL_REMOVED, 0, size - 1);
	}
	
	/**
	 * Adds an element at the specified index
	 * @param index
	 * @param element
	 */
	public void add(int index, T element) {
		listData.add(index, element);
		broadcastListDataChange(ListDataEvent.INTERVAL_ADDED, index, index);
	}
	
	/**
	 * Adds an element to the end of the list
	 * @param element
	 */
	public void addElement(T element) {
		listData.add(element);
		broadcastListDataChange(ListDataEvent.INTERVAL_ADDED, listData.size() - 1, listData.size() - 1);
	}
	
	/**
	 * Removes a specific element from the list
	 * @param element
	 */
	public void removeElement(T element) {
		int index = listData.indexOf(element);
		if (index == -1) return;
		listData.remove(index);
		broadcastListDataChange(ListDataEvent.INTERVAL_REMOVED, index, index);
	}
	
	/**
	 * Broadcasts a complete data model update event to all registered ListDataListeners
	 */
	protected void broadcastCompleteListDataChange() {
		broadcastListDataChange(ListDataEvent.CONTENTS_CHANGED, 0, listData.size() - 1);
	}
	
	private void broadcastListDataChange(int eventType, int index0, int index1) {
		for (ListDataListener l : listeners) {
			l.contentsChanged(new ListDataEvent(this, eventType, index0, index1));
		}
	}
	
}
