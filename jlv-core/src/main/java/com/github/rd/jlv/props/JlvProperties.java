package com.github.rd.jlv.props;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;

public class JlvProperties {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final PropertyConverter<List<LoglistColumn>> LOGLIST_COLUMN_CONVERTER = new LoglistColumnConverter();
	private static final PropertyConverter<List<LoglistLevelColor>> LOGLIST_LEVEL_COLOR_CONVERTER = new LoglistLevelColorConverter();
	private static final PropertyConverter<Integer> INTEGER_CONVERTER = new IntegerConverter();
	private static final PropertyConverter<Boolean> BOOLEAN_CONVERTER = new BooleanConverter();
	private static final Map<PropertyKey, PropertyConverter<?>> CONVERTERS = new EnumMap<>(PropertyKey.class);

	private final EventBus eventBus = new EventBus();

	private Properties store = new Properties();
	private File propertyFile;

	static {
		CONVERTERS.put(PropertyKey.SERVER_PORT_KEY, INTEGER_CONVERTER);
		CONVERTERS.put(PropertyKey.SERVER_AUTOSTART_KEY, BOOLEAN_CONVERTER);
		CONVERTERS.put(PropertyKey.LOGLIST_BUFFER_SIZE_KEY, INTEGER_CONVERTER);
		CONVERTERS.put(PropertyKey.LOGLIST_REFRESH_TIME_KEY, INTEGER_CONVERTER);
		CONVERTERS.put(PropertyKey.LOGLIST_QUICK_SEARCH_KEY, BOOLEAN_CONVERTER);
		CONVERTERS.put(PropertyKey.LOGLIST_FONT_SIZE_KEY, INTEGER_CONVERTER);
		CONVERTERS.put(PropertyKey.LOGLIST_LEVEL_IMAGE_KEY, BOOLEAN_CONVERTER);
		CONVERTERS.put(PropertyKey.LOGLIST_LEVEL_COLOR_KEY, LOGLIST_LEVEL_COLOR_CONVERTER);
		CONVERTERS.put(PropertyKey.LOGLIST_COLUMN_KEY, LOGLIST_COLUMN_CONVERTER);
	}

	public JlvProperties(File propertyFile) {
		if (isAvailableForUsage(propertyFile)) {
			this.propertyFile = propertyFile;

			try (InputStream in = new FileInputStream(propertyFile)) {
				store.load(in);
			} catch (IOException e) {
				logger.warn("Couldn't load properties file {}. The default properties will be used instead.",
						propertyFile, e);
			}
		}
	}

	public <T> void save(PropertyKey key, T value) {
		Preconditions.checkNotNull(value, "Property value musn't be null");
		PropertyConverter<T> converter = getConverter(key);
		T oldValue = load(key);

		if (!value.equals(oldValue)) {
			eventBus.post(new PropertyChangeEvent(key, oldValue, value));
		}
		store.setProperty(key.keyName(), converter.convertToString(value));
	}

	public <T> T load(PropertyKey key) {
		PropertyConverter<T> converter = getConverter(key);
		String value = store.getProperty(key.keyName(), key.defaultValue());
		return converter.convertFromString(value);
	}

	public <T> T loadDefault(PropertyKey key) {
		PropertyConverter<T> converter = getConverter(key);
		return converter.convertFromString(key.defaultValue());
	}

	public void persist() {
		if (isAvailableForUsage(propertyFile)) {
			try (OutputStream out = new FileOutputStream(propertyFile)) {
				store.store(out, null);
			} catch (IOException e) {
				logger.error("Couldn't persist properties file {}.", propertyFile, e);
			}
		}
	}

	public void addPropertyChangeListener(Object listener) {
		Preconditions.checkNotNull(listener, "Listener object mustn't be null.");
		eventBus.register(listener);
	}

	public void removePropertyChangeListener(Object listener) {
		Preconditions.checkNotNull(listener, "Listener object mustn't be null.");
		eventBus.unregister(listener);
	}

	private boolean isAvailableForUsage(File file) {
		boolean available = true;

		if (file == null) {
			logger.warn("Properties file is null. The in-memory storage will be used instead.");
			available = false;
		} else {
			try {
				if (!file.exists()) {
					logger.debug("Properties file '{}' wasn't found. Trying to create an empty properties file.", file);

					if (file.createNewFile()) {
						logger.debug("Empty properties file '{}' was created.", file);
					}
				}
			} catch (IOException e) {
				logger.error("Properties file '{}' can not be used due to: {}", file, e);
				available = false;
			}
		}
		return available;
	}

	@SuppressWarnings("unchecked")
	private static <T> PropertyConverter<T> getConverter(PropertyKey key) {
		return (PropertyConverter<T>) CONVERTERS.get(key);
	}
}
