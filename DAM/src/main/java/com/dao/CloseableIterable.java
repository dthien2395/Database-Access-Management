package com.dao;

/**
 * Created by HienNguyen on 7/3/2017.
 */
public interface CloseableIterable<T> extends Iterable<T> {

	/**
	 * Returns an iterator over a set of elements of type T.
	 * 
	 * @return an CloseableIterator.
	 */
	public CloseableIterator<T> iterator();
}
