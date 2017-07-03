package verifysystem.company.com.verifysystem.adapter;

import android.content.Context;
import android.view.ViewGroup;

import verifysystem.company.com.verifysystem.adapter.viewHolder.VerifyDeviceViewHolder;
import verifysystem.company.com.verifysystem.model.DeviceBean;

import java.util.List;

import verifysystem.company.com.verifysystem.R;

/**
 * Created by Michael on 4/16/2017.
 */

public class VerifyDeviceAdapter
        extends StandardRecyclerAdapter<DeviceBean, VerifyDeviceViewHolder> {

    public VerifyDeviceAdapter(Context context, List<DeviceBean> mDataList) {
        super(context, mDataList);
    }

    @Override public VerifyDeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VerifyDeviceViewHolder(
                layoutInflater.inflate(R.layout.item_verify_device, parent, false));
    }

    @Override public void onBindViewHolder(VerifyDeviceViewHolder holder, int position) {
        DeviceBean deviceBean = mDataList.get(position);
        holder.tv_device_sn.setText(deviceBean.getSnNo());
        holder.tv_deivce_checkUnit.setText(deviceBean.getCheckUnit());
        holder.tv_deivce_types.setText(deviceBean.getTypes());
        holder.tv_device_certificateNo.setText(deviceBean.getCertificateNo());
        holder.tv_device_validDate.setText(deviceBean.getCreateDate());

        holder.verify_device_index.setText(deviceBean.getNo());
        if (deviceBean.isOnlone()) {
            holder.verify_device_status.setText(R.string.text_online);
            holder.verify_device_status.setTextColor(0xFF00FF00);
        } else {
            holder.verify_device_status.setText(R.string.text_offline);
            holder.verify_device_status.setTextColor(0xFFe6e6e6);
        }
    }
}
