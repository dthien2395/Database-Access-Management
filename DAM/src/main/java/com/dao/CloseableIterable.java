package com.dao;

/**
 * Created by HienNguyen on 7/3/2017.
 */
public interface CloseableIterable<T> extends Iterable<T> {

	public CloseableIterator<T> iterator();
}
