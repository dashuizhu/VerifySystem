package verifysystem.company.com.verifysystem;

import com.google.gson.Gson;
import dalvik.annotation.TestTargetClass;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import verifysystem.company.com.verifysystem.model.RecordBean;
import verifysystem.company.com.verifysystem.utils.MyDateUtils;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
      System.out.println(sdf.format(new Date()));



      List<RecordBean> list = new ArrayList<>();
      for (int i=0; i<3; i++){
        RecordBean recordBean = new RecordBean();
        recordBean.setSnNo("sn");
        recordBean.setTemperature(33.3f);
        recordBean.setDate(MyDateUtils.getTime(System.currentTimeMillis() - 70000*i));
        recordBean.setReportNo("reportNo");
        list.add(recordBean);
      }

      JSONArray array = new JSONArray();
      JSONObject obj;
      RecordBean bean;
      long time = getDate(list);
      for (int i=0; i<list.size(); i++) {
        bean = list.get(i);
        try {
          obj = new JSONObject();
          obj.put("snNo", bean.getSnNo());
          obj.put("reportNo", bean.getReportNo());
          obj.put("temperature", bean.getTemperature());
          obj.put("humidity", bean.getHumidity());
          obj.put("voltage", bean.getVoltage());
          obj.put("date", MyDateUtils.getTime(time));
          obj.put("style", bean.getStyle());
          array.put(obj);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

      Gson gson = new Gson();
      String jsonArray = gson.toJson(array);
      System.out.println(jsonArray);
    }

  private long getDate(List<RecordBean> list) {
    long time = 0L;
    if (list == null || list.size()==0) {
      time = System.currentTimeMillis();
    } else if (list.size() == 1) {
      time = list.get(0).getDateMiss();
    } else if (list.size() == 2) {
      time += list.get(0).getDateMiss();
      time += list.get(1).getDateMiss();
      time = time/2;
    } else {
      time += list.get(0).getDateMiss();
      time += list.get(list.size()-1).getDateMiss();
      time += list.get(list.size()/2).getDateMiss();
      time = time/3;
    }
    return time;
  }

    @Test
    public void test() {
      byte[] data_command = new byte[114];
      for (int  i =0; i<114; i++) {
        data_command[i] = (byte)i;
      }

      int data_index = 105;
      int length = 20;
      byte[]  newArray = new byte[100];
      if (data_index +length >=114) {
        newArray = new byte[100];
        System.arraycopy(data_command, data_index-100, newArray, 0, 100);
        data_command = new byte[114];
        System.arraycopy(newArray, 0, data_command, 0, 100);
        data_index = 105;
      }
    }

    @Test
    public void rxTest() {
       Subscription sub = Observable.interval(1, TimeUnit.SECONDS)
               .flatMap(new Func1<Long, Observable<Long>>() {
                 @Override public Observable<Long> call(Long aLong) {
                   if (aLong>= 3) {
                     try {
                       throw  new IOException("xxx");
                     } catch (IOException e) {
                       e.printStackTrace();
                     }
                     return null;
                   } else {
                     return Observable.just(aLong);
                   }
                 }
               }).subscribe(new Subscriber<Long>() {
            @Override public void onCompleted() {

            }

            @Override public void onError(Throwable e) {
                System.out.println("onerror " + e.getMessage());
            }

            @Override public void onNext(Long aLong) {
                System.out.println(""+aLong);
            }
        });

        try {
            Thread.sleep(2500);
          Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}