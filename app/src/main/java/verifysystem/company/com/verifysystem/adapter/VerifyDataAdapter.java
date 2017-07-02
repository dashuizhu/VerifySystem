package verifysystem.company.com.verifysystem.adapter;

import android.content.Context;
import android.view.ViewGroup;

import verifysystem.company.com.verifysystem.adapter.viewHolder.VerifyDataViewHolder;
import verifysystem.company.com.verifysystem.model.RecordBean;

import java.util.List;

import verifysystem.company.com.verifysystem.R;

/**
 * Created by Michael on 4/16/2017.
 */

public class VerifyDataAdapter extends StandardRecyclerAdapter<RecordBean,VerifyDataViewHolder>{

    private Context mContext;

    private float minTemp, maxTemp, minHum, maxHum;

    public VerifyDataAdapter(Context context, List<RecordBean> mDataList) {
        super(context, mDataList);
        mContext = context;
    }

    @Override
    public VerifyDataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VerifyDataViewHolder(layoutInflater.inflate(R.layout.item_verify_data,parent,false));
    }

    @Override
    public void onBindViewHolder(VerifyDataViewHolder holder, final int position) {
        RecordBean data = mDataList.get(position);
        holder.verify_data_index.setText(""+(position+1));
        holder.verify_data_no.setText(data.getReportNo());
        holder.verify_data_device_id.setText(data.getSnNo());
        holder.verify_data_temp.setText(String.valueOf(data.getTemperature()));
        holder.verify_data_hum.setText(String.valueOf(data.getHumidity()));
        holder.verify_data_time.setText(String.valueOf(data.getDate()));
        //// TODO: 2017/5/21 暂时没有电压， 如果是0，就显示ERR
        if (data.getVoltage() ==0) {
            holder.verify_data_voltage.setText(R.string.text_voltage_err);
        } else {
            holder.verify_data_voltage.setText(String.valueOf(data.getVoltage()));
        }
        int textColor = getTextColor(data);
        holder.verify_data_index.setTextColor(textColor);
        holder.verify_data_time.setTextColor(textColor);
        holder.verify_data_no.setTextColor(textColor);
        holder.verify_data_device_id.setTextColor(textColor);
        holder.verify_data_temp.setTextColor(textColor);
        holder.verify_data_hum.setTextColor(textColor);
        holder.verify_data_voltage.setTextColor(textColor);

    }

    /**
     * 根据温度  和 湿度， 获得显示color
     * @param recordBean
     * @return
     */
    private int getTextColor(RecordBean recordBean) {
        int textColor;
        boolean isTempNormal =true , isHumNormal = true;
        if (recordBean.getTemperature() <minTemp || recordBean.getTemperature() > maxTemp) {
            isTempNormal = false;
        }
        if (recordBean.getHumidity() <minHum || recordBean.getHumidity() > maxHum) {
            isHumNormal = false;
        }
        if (isTempNormal && isHumNormal) { //都正常
            textColor = mContext.getResources().getColor(R.color.colorWhite);
        } else if(isTempNormal && !isHumNormal) { // 温度正常  湿度异常
            textColor = mContext.getResources().getColor(R.color.colorTempWarm);
        } else if (!isTempNormal && isHumNormal){ // 温度异常  湿度正常
            textColor = mContext.getResources().getColor(R.color.colorHumWarm);
        } else { //都异常 报警
            textColor = mContext.getResources().getColor(R.color.colorRed);
        }
        return textColor;
    }

    public void setTempRange(float minTemp, float maxTemp) {
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
    }

    public void setHumRange(float minHum, float maxHum) {
        this.maxHum =maxHum;
        this.minHum = minHum;
    }

}
