package de.uk.java.feader.data;

import java.util.Collections;

public class SortableListModel<T extends Comparable<T>> extends CustomListModel<T> {
	
	/**
	 * Sorts this list model using the Comparable implementation of type T
	 * and broadcasts a list data change event to all registered listeners.
	 */
	public void sort() {
		Collections.sort(listData);
		broadcastCompleteListDataChange();
	}

}
