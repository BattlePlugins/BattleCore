package mc.alk.battlecore.configuration;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Configuration API adapted from Bukkit & Nukkit
 */
public class ConfigurationSection extends LinkedHashMap<String, Object> {

    public ConfigurationSection() {
        super();
    }

    public ConfigurationSection(String key, Object value) {
        set(key, value);
    }

    public ConfigurationSection(Map<String, Object> map) {
        if (map == null || map.isEmpty())
            return;

        for (Map.Entry<String, Object> entrySet : map.entrySet()) {
            if (entrySet.getValue() instanceof LinkedHashMap) {
                put(entrySet.getKey(), new ConfigurationSection((LinkedHashMap) entrySet.getValue()));
            } else if (entrySet.getValue() instanceof List) {
                put(entrySet.getKey(), parseList((List<?>) entrySet.getValue()));
            } else {
                put(entrySet.getKey(), entrySet.getValue());
            }
        }
    }

    private List<?> parseList(List<?> list) {
        List<Object> newList = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof LinkedHashMap) {
                newList.add(new ConfigurationSection((LinkedHashMap) object));
            } else {
                newList.add(object);
            }
        }

        return newList;
    }

    public Map<String, Object> getAll() {
        return new LinkedHashMap<>(this);
    }

    public ConfigurationSection getAllSections() {
        return new ConfigurationSection(this);
    }

    public Object get(String key) {
        return this.get(key, null);
    }

    public <T> T get(String key, T defaultValue) {
        if (key == null || key.isEmpty())
            return defaultValue;

        if (super.containsKey(key))
            return (T) super.get(key);

        String[] keys = key.split("\\.", 2);
        if (!super.containsKey(keys[0]))
            return defaultValue;

        Object value = super.get(keys[0]);
        if (value instanceof ConfigurationSection) {
            ConfigurationSection section = (ConfigurationSection) value;
            return section.get(keys[1], defaultValue);
        }

        return defaultValue;
    }

    public void set(String key, Object value) {
        String[] subKeys = key.split("\\.", 2);
        if (subKeys.length > 1) {
            ConfigurationSection childSection = new ConfigurationSection();
            if (containsKey(subKeys[0]) && super.get(subKeys[0]) instanceof ConfigurationSection) {
                childSection = (ConfigurationSection) super.get(subKeys[0]);
            }
            childSection.set(subKeys[1], value);
            super.put(subKeys[0], childSection);
        } else {
            super.put(subKeys[0], value);
        }
    }

    public boolean isSection(String key) {
        return get(key) instanceof ConfigurationSection;
    }

    public ConfigurationSection getSection(String key) {
        return get(key, new ConfigurationSection());
    }

    public ConfigurationSection getSections() {
        return getSections(null);
    }

    public ConfigurationSection getSections(String key) {
        ConfigurationSection sections = new ConfigurationSection();
        ConfigurationSection parent = key == null || key.isEmpty() ? getAllSections() : getSection(key);
        if (parent == null) return sections;
        parent.forEach((key1, value) -> {
            if (value instanceof ConfigurationSection)
                sections.put(key1, value);
        });
        return sections;
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public int getInt(String key, int defaultValue) {
        return get(key, ((Number) defaultValue)).intValue();
    }

    public boolean isInt(String key) {
        return get(key) instanceof Integer;
    }

    public long getLong(String key) {
        return getLong(key, 0);
    }

    public long getLong(String key, long defaultValue) {
        return get(key, ((Number) defaultValue)).longValue();
    }

    public boolean isLong(String key) {
        return get(key) instanceof Long;
    }

    public double getDouble(String key) {
        return getDouble(key, 0);
    }

    public double getDouble(String key, double defaultValue) {
        return get(key, ((Number) defaultValue)).doubleValue();
    }

    public boolean isDouble(String key) {
        return get(key) instanceof Double;
    }

    public String getString(String key) {
        return getString(key, "");
    }

    public String getString(String key, String defaultValue) {
        Object result = get(key, defaultValue);
        return String.valueOf(result);
    }

    public boolean isString(String key) {
        return get(key) instanceof String;
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return get(key, defaultValue);
    }

    public boolean isBoolean(String key) {
        return get(key) instanceof Boolean;
    }

    public List getList(String key) {
        return getList(key, null);
    }

    public List getList(String key, List defaultList) {
        return get(key, defaultList);
    }

    public boolean isList(String key) {
        return get(key) instanceof List;
    }

    public List<String> getStringList(String key) {
        List value = this.getList(key);
        if (value == null) {
            return new ArrayList<>(0);
        }
        List<String> result = new ArrayList<>();
        for (Object o : value) {
            if (o instanceof String || o instanceof Number || o instanceof Boolean || o instanceof Character) {
                result.add(String.valueOf(o));
            }
        }
        return result;
    }

    public List<Integer> getIntegerList(String key) {
        List<?> list = getList(key);
        if (list == null) {
            return new ArrayList<>(0);
        }
        List<Integer> result = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof Integer) {
                result.add((Integer) object);
            } else if (object instanceof String) {
                try {
                    result.add(Integer.valueOf((String) object));
                } catch (Exception ex) {
                    /* do nothing */
                }
            } else if (object instanceof Character) {
                result.add((int) (Character) object);
            } else if (object instanceof Number) {
                result.add(((Number) object).intValue());
            }
        }
        return result;
    }

    public List<Boolean> getBooleanList(String key) {
        List<?> list = getList(key);
        if (list == null) {
            return new ArrayList<>(0);
        }
        List<Boolean> result = new ArrayList<>();
        for (Object object : list) {
            if (object instanceof Boolean) {
                result.add((Boolean) object);
            } else if (object instanceof String) {
                if (Boolean.TRUE.toString().equals(object)) {
                    result.add(true);
                } else if (Boolean.FALSE.toString().equals(object)) {
                    result.add(false);
                }
            }
        }
        return result;
    }

    public List<Double> getDoubleList(String key) {
        List<?> list = getList(key);
        if (list == null) {
            return new ArrayList<>(0);
        }
        List<Double> result = new ArrayList<>();
        for (Object object : list) {
            if (object instanceof Double) {
                result.add((Double) object);
            } else if (object instanceof String) {
                try {
                    result.add(Double.valueOf((String) object));
                } catch (Exception ex) {
                    /* do nothing */
                }
            } else if (object instanceof Character) {
                result.add((double) (Character) object);
            } else if (object instanceof Number) {
                result.add(((Number) object).doubleValue());
            }
        }
        return result;
    }

    public List<Float> getFloatList(String key) {
        List<?> list = getList(key);
        if (list == null) {
            return new ArrayList<>(0);
        }
        List<Float> result = new ArrayList<>();
        for (Object object : list) {
            if (object instanceof Float) {
                result.add((Float) object);
            } else if (object instanceof String) {
                try {
                    result.add(Float.valueOf((String) object));
                } catch (Exception ex) {
                    /* do nothing */
                }
            } else if (object instanceof Character) {
                result.add((float) (Character) object);
            } else if (object instanceof Number) {
                result.add(((Number) object).floatValue());
            }
        }
        return result;
    }

    public List<Long> getLongList(String key) {
        List<?> list = getList(key);
        if (list == null) {
            return new ArrayList<>(0);
        }
        List<Long> result = new ArrayList<>();
        for (Object object : list) {
            if (object instanceof Long) {
                result.add((Long) object);
            } else if (object instanceof String) {
                try {
                    result.add(Long.valueOf((String) object));
                } catch (Exception ex) {
                    /* do nothing */
                }
            } else if (object instanceof Character) {
                result.add((long) (Character) object);
            } else if (object instanceof Number) {
                result.add(((Number) object).longValue());
            }
        }
        return result;
    }

    public List<Byte> getByteList(String key) {
        List<?> list = getList(key);

        if (list == null) {
            return new ArrayList<>(0);
        }

        List<Byte> result = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof Byte) {
                result.add((Byte) object);
            } else if (object instanceof String) {
                try {
                    result.add(Byte.valueOf((String) object));
                } catch (Exception ex) {
                    /* do nothing */
                }
            } else if (object instanceof Character) {
                result.add((byte) ((Character) object).charValue());
            } else if (object instanceof Number) {
                result.add(((Number) object).byteValue());
            }
        }

        return result;
    }

    public List<Character> getCharacterList(String key) {
        List<?> list = getList(key);

        if (list == null) {
            return new ArrayList<>(0);
        }

        List<Character> result = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof Character) {
                result.add((Character) object);
            } else if (object instanceof String) {
                String str = (String) object;

                if (str.length() == 1) {
                    result.add(str.charAt(0));
                }
            } else if (object instanceof Number) {
                result.add((char) ((Number) object).intValue());
            }
        }

        return result;
    }

    public List<Short> getShortList(String key) {
        List<?> list = getList(key);

        if (list == null) {
            return new ArrayList<>(0);
        }

        List<Short> result = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof Short) {
                result.add((Short) object);
            } else if (object instanceof String) {
                try {
                    result.add(Short.valueOf((String) object));
                } catch (Exception ex) {
                    /* do nothing */
                }
            } else if (object instanceof Character) {
                result.add((short) ((Character) object).charValue());
            } else if (object instanceof Number) {
                result.add(((Number) object).shortValue());
            }
        }

        return result;
    }

    public List<Map> getMapList(String key) {
        List<Map> list = getList(key);
        List<Map> result = new ArrayList<>();

        if (list == null) {
            return result;
        }

        for (Object object : list) {
            if (object instanceof Map) {
                result.add((Map) object);
            }
        }

        return result;
    }

    public boolean contains(String key) {
        return contains(key, false);
    }

    public boolean contains(String key, boolean ignoreCase) {
        if (ignoreCase) key = key.toLowerCase();
        for (String existKey : getKeys(true)) {
            if (ignoreCase) existKey = existKey.toLowerCase();
            if (existKey.equals(key)) return true;
        }
        return false;
    }

    public void remove(String key) {
        if (key == null || key.isEmpty())
            return;

        if (super.containsKey(key))
            super.remove(key);

        else if (containsKey(".")) {
            String[] keys = key.split("\\.", 2);
            if (super.get(keys[0]) instanceof ConfigurationSection) {
                ConfigurationSection section = (ConfigurationSection) super.get(keys[0]);
                section.remove(keys[1]);
            }
        }
    }

    public Set<String> getKeys(boolean child) {
        Set<String> keys = new LinkedHashSet<>();
        this.forEach((key, value) -> {
            keys.add(key);
            if (value instanceof ConfigurationSection) {
                if (child)
                    ((ConfigurationSection) value).getKeys(true).forEach(childKey -> keys.add(key + "." + childKey));
            }
        });
        return keys;
    }

    public Set<String> getKeys() {
        return this.getKeys(true);
    }
}
