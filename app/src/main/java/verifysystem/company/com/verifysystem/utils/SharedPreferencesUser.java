package verifysystem.company.com.verifysystem.utils;

import android.content.Context;
import android.content.SharedPreferences;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UnknownFormatConversionException;
import verifysystem.company.com.verifysystem.model.RecordBean;

/**
 * 用来保存用户相关的缓存， 登出、被踢下线、版本升级  会全部清空
 */
public class SharedPreferencesUser {
  public static final String KEY_MIN_TEMP = "key_min_temp_position";//最小报警温度
  public static final String KEY_MAX_TEMP = "key_max_temp_position"; //最大报警温度
  public static final String KEY_MIN_HUM = "key_min_hum_position";//最小报警湿度
  public static final String KEY_MAX_HUM = "key_max_hum_position";//最大报警湿度
  /**
   * 保存在手机里面的文件名
   */
  public static final String FILE_NAME = "verifySystem_share_data";
  public static final String KEY_LAST_BLUETOOTH = "key_last_address";
  /**
   * 采集数据时间 下标
   */
  public static final String KEY_TIME_COLLECT_POSITION = "key_time_collect_index";
  /**
   * 数据存储时间 下标
   */
  public static final String KEY_TIME_SAVE_POSITION = "key_save_collect_index";
  /**
   * 数据采集延时 下标
   */
  public static final String KEY_TIME_DELAY_POSITION = "key_delay_collect_index";

  /**
   * 采集数据时间 单位分钟
   */
  public static final String KEY_TIME_COLLECT_MINUTE = "key_time_collect";
  /**
   * 数据存储时间 单位分钟
   */
  public static final String KEY_TIME_SAVE_MINUTE = "key_save_collect";
  /**
   * 数据采集延时 单位分钟
   */
  public static final String KEY_TIME_DELAY_MINUTE = "key_delay_collect";
  /**
   * 数据类型 {@link RecordBean.RECORD_TYPE}
   */
    public static final String KEY_RECORD_TYPE = "record_type";
    public static final String KEY_MAC = "key_mac";
  /**
   * 最后一次发送的设备白名单
   */
  public static final String KEY_LAST_DEVICE = "last_device_list";


    /**
   * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
   *  @param context
   * @param key
   * @param object
   */
  public static String put(Context context, String key, Object object) {
    if (object == null){
      return key;
    }

    SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sp.edit();

    if (object instanceof String) {
      editor.putString(key, (String) object);
    } else if (object instanceof Integer) {
      editor.putInt(key, (Integer) object);
    } else if (object instanceof Boolean) {
      editor.putBoolean(key, (Boolean) object);
    } else if (object instanceof Float) {
      editor.putFloat(key, (Float) object);
    } else if (object instanceof Long) {
      editor.putLong(key, (Long) object);
    } else {
      throw new UnknownFormatConversionException("cannot save!");
    }

    //SharedPreferencesCompat.apply(editor);
    editor.commit();
    return key;
  }

  /**
   * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
   *
   * @param context
   * @param key
   * @param defaultObject
   * @return
   */
  public static Object get(Context context, String key, Object defaultObject) {
    SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
        Context.MODE_PRIVATE);

    if (defaultObject instanceof String) {
      return sp.getString(key, (String) defaultObject);
    } else if (defaultObject instanceof Integer) {
      return sp.getInt(key, (Integer) defaultObject);
    } else if (defaultObject instanceof Boolean) {
      return sp.getBoolean(key, (Boolean) defaultObject);
    } else if (defaultObject instanceof Float) {
      return sp.getFloat(key, (Float) defaultObject);
    } else if (defaultObject instanceof Long) {
      return sp.getLong(key, (Long) defaultObject);
    }
    return null;
  }

  /**
   * 移除某个key值已经对应的值
   * @param context
   * @param key
   */
  public static void remove(Context context, String key) {
    SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
        Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sp.edit();
    editor.remove(key);
    SharedPreferencesCompat.apply(editor);
  }

  /**
   * 清除所有数据
   * @param context
   */
  public static void clear(Context context) {
    SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
        Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sp.edit();
    editor.clear();
    SharedPreferencesCompat.apply(editor);
  }

  /**
   * 查询某个key是否已经存在
   * @param context
   * @param key
   * @return
   */
  public static boolean contains(Context context, String key) {
    SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
        Context.MODE_PRIVATE);
    return sp.contains(key);
  }

  /**
   * 返回所有的键值对
   *
   * @param context
   * @return
   */
  public static Map<String, ?> getAll(Context context) {
    SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
        Context.MODE_PRIVATE);
    return sp.getAll();
  }

  /**
   * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
   *
   * @author zhy
   *
   */
  private static class SharedPreferencesCompat {
    private static final Method sApplyMethod = findApplyMethod();

    /**
     * 反射查找apply的方法
     *
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static Method findApplyMethod() {
      try {
        Class clz = SharedPreferences.Editor.class;
        return clz.getMethod("apply");
      } catch (NoSuchMethodException e) {
      }

      return null;
    }

    /**
     * 如果找到则使用apply执行，否则使用commit
     *
     * @param editor
     */
    public static void apply(SharedPreferences.Editor editor) {
      try {
        if (sApplyMethod != null) {
          sApplyMethod.invoke(editor);
          return;
        }
      } catch (IllegalArgumentException | IllegalAccessException e) {
      } catch (InvocationTargetException e) {
      }
      editor.commit();
    }
  }
}
