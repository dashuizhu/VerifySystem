package verifysystem.company.com.verifysystem.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import io.realm.RealmResults;
import java.util.ArrayList;
import java.util.List;
import rx.Subscriber;
import verifysystem.company.com.verifysystem.database.RecordDao;
import verifysystem.company.com.verifysystem.model.NetworkResult;
import verifysystem.company.com.verifysystem.model.RecordBean;
import verifysystem.company.com.verifysystem.network.AppModel;
import verifysystem.company.com.verifysystem.utils.LogUtils;

/**
 * @author zhuj 2017/7/3 下午3:06.
 */
public class UploadService extends IntentService {

  private final String TAG = UploadService.class.getSimpleName();

  private AppModel mAppModel;

  public UploadService() {
    super("upload");
  }


  @Override protected void onHandleIntent(@Nullable Intent intent) {
    if (mAppModel == null) {
      mAppModel = new AppModel();
    }
    RealmResults<RecordDao> result = RecordDao.getList();
    if (result!=null && result.isValid()) {
      if (result.size() == 0) {
        return;
      }
      List<RecordBean> recordBeanList = new ArrayList<>();
      for (RecordDao dao : result) {
        recordBeanList.add(dao.castBean());
      }
      LogUtils.d(TAG, "开始补充上传  个数" + recordBeanList.size());
      mAppModel.uploadData(recordBeanList).subscribe(new Subscriber<NetworkResult>() {
        @Override public void onCompleted() {

        }

        @Override public void onError(Throwable throwable) {
          throwable.printStackTrace();
        }

        @Override public void onNext(NetworkResult networkResult) {
          LogUtils.d(TAG, "上传结果 " + networkResult.toString());
          if (networkResult.isNetworkSuccess()) {
            RecordDao.cleanAll();
          }
        }
      });
    }
  }


}
