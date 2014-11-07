//@author A0116713M
package cs2103.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.api.client.util.IOUtils;
import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.google.api.client.util.Preconditions;
import com.google.api.client.util.store.AbstractDataStore;
import com.google.api.client.util.store.AbstractDataStoreFactory;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.DataStoreUtils;
import com.google.api.client.util.store.FileDataStoreFactory;

/**
 * @author Yuri
 * This class extends AbstractDataStoreFactory in Google client API library.
 * Used to create a qualified local data store for Google authentication
 */
public class AuthStoreFactory extends AbstractDataStoreFactory {
	private final File dataDirectory;
	
	public AuthStoreFactory() {
		this.dataDirectory = new File(".");
	}
	
	public final File getDataDirectory() {
	    return dataDirectory;
	}
	
	@Override
	protected <V extends Serializable> DataStore<V> createDataStore(String id) throws IOException {
		return new FileDataStore<V>(this, dataDirectory, id);
	}
	
	static class FileDataStore<V extends Serializable> extends AbstractDataStore<V> {
		private final Lock lock = new ReentrantLock();
		HashMap<String, byte[]> keyValueMap = Maps.newHashMap();
	    private final File dataFile;

	    FileDataStore(AuthStoreFactory dataStore, File dataDirectory, String id) throws IOException {
	    	super(dataStore, id);
	    	this.dataFile = new File(dataDirectory, id);
	    	if (dataFile.createNewFile()) {
	    		keyValueMap = Maps.newHashMap();
	    		save();
	    	} else {
	    		keyValueMap = IOUtils.deserialize(new FileInputStream(dataFile));
	    	}
	    }

	    void save() throws IOException {
	    	IOUtils.serialize(keyValueMap, new FileOutputStream(dataFile));
	    }

	    @Override
	    public FileDataStoreFactory getDataStoreFactory() {
	    	return (FileDataStoreFactory) super.getDataStoreFactory();
	    }

		@Override
		public Set<String> keySet() throws IOException {
			lock.lock();
		    try {
		    	return Collections.unmodifiableSet(keyValueMap.keySet());
		    } finally {
		    	lock.unlock();
		    }
		}

		@Override
		public Collection<V> values() throws IOException {
			lock.lock();
		    try {
		    	List<V> result = Lists.newArrayList();
		    	for (byte[] bytes : keyValueMap.values()) {
		    		result.add(IOUtils.<V>deserialize(bytes));
		    	}
		    	return Collections.unmodifiableList(result);
		    } finally {
		    	lock.unlock();
		    }
		}

		@Override
		public V get(String key) throws IOException {
			if (key == null) {
				return null;
			}
			lock.lock();
			try {
				return IOUtils.deserialize(keyValueMap.get(key));
			} finally {
			    lock.unlock();
			}
		}

		@Override
		public DataStore<V> set(String key, V value) throws IOException {
			Preconditions.checkNotNull(key);
		    Preconditions.checkNotNull(value);
		    lock.lock();
		    try {
		    	keyValueMap.put(key, IOUtils.serialize(value));
		    	save();
		    } finally {
		    	lock.unlock();
		    }
		    return this;
		}

		@Override
		public DataStore<V> clear() throws IOException {
			lock.lock();
		    try {
		    	keyValueMap.clear();
		    	save();
		    } finally {
		    	lock.unlock();
		    }
		    return this;
		}

		@Override
		public DataStore<V> delete(String key) throws IOException {
			if (key == null) {
				return this;
			}
			lock.lock();
			try {
				keyValueMap.remove(key);
				save();
			} finally {
				lock.unlock();
			}
			return this;
		}
		
		@Override
		public boolean containsKey(String key) throws IOException {
		  	if (key == null) {
		    	return false;
		    }
		    lock.lock();
		    try {
		    	return keyValueMap.containsKey(key);
		    } finally {
		    	lock.unlock();
		    }
		}

		@Override
		public boolean containsValue(V value) throws IOException {
			if (value == null) {
				return false;
			}
			lock.lock();
			try {
				byte[] serialized = IOUtils.serialize(value);
				for (byte[] bytes : keyValueMap.values()) {
					if (Arrays.equals(serialized, bytes)) {
						return true;
					}
				}
				return false;
			} finally {
				lock.unlock();
			}
		}

		@Override
		public boolean isEmpty() throws IOException {
			lock.lock();
			try {
				return keyValueMap.isEmpty();
			} finally {
				lock.unlock();
			}
		}

		@Override
		public int size() throws IOException {
			lock.lock();
			try {
				return keyValueMap.size();
			} finally {
				lock.unlock();
			}
		}

		@Override
		public String toString() {
			return DataStoreUtils.toString(this);
		}
	}
}
