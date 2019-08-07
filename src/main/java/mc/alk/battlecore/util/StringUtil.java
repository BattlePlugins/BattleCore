package mc.alk.battlecore.util;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.text.StrBuilder;

import java.util.Collection;
import java.util.Iterator;

public class StringUtil {

    public static String join(Iterator iterator, String separator) {
        if (iterator == null) {
            return null;
        } else if (!iterator.hasNext()) {
            return "";
        } else {
            Object first = iterator.next();
            if (!iterator.hasNext()) {
                return ObjectUtils.toString(first);
            } else {
                StrBuilder buf = new StrBuilder(256);
                if (first != null) {
                    buf.append(first);
                }

                while(iterator.hasNext()) {
                    if (separator != null) {
                        buf.append(separator);
                    }

                    Object obj = iterator.next();
                    if (obj != null) {
                        buf.append(obj);
                    }
                }

                return buf.toString();
            }
        }
    }

    public static String join(Collection collection, String separator) {
        return collection == null ? null : join(collection.iterator(), separator);
    }
}
