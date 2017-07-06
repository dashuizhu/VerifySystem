package verifysystem.company.com.verifysystem.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Set;
import verifysystem.company.com.verifysystem.AppApplication;
import verifysystem.company.com.verifysystem.activity.MainActivity;
import verifysystem.company.com.verifysystem.activity.PictureShowActivity;
import verifysystem.company.com.verifysystem.adapter.viewHolder.VerifyProjectViewHolder;
import verifysystem.company.com.verifysystem.global.Constant;

import verifysystem.company.com.verifysystem.model.VerifyPorjectBean;
import java.util.List;

import verifysystem.company.com.verifysystem.R;

/**
 * Created by Michael on 4/16/2017.
 */

public class VerifyProjectAdapter extends StandardRecyclerAdapter<VerifyPorjectBean,VerifyProjectViewHolder>{

    private Context mContext;
    private FragmentManager mFrgmMgr;
    private FragmentTransaction mFrgmTransaction;
    private MainActivity mMainActivity;
    private VerifyProjectClickListener mListener;

    public VerifyProjectAdapter(Context context, List<VerifyPorjectBean> mDataList) {
        super(context, mDataList);
        mContext = context;
        mMainActivity = ((MainActivity)mContext);
    }

    @Override
    public VerifyProjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VerifyProjectViewHolder(layoutInflater.inflate(R.layout.item_verify_project,parent,false));
    }

    @Override
    public void onBindViewHolder(final VerifyProjectViewHolder holder, final int position) {
        final VerifyPorjectBean project = mDataList.get(position);
        holder.verify_project_index.setText(String.valueOf(position +1));

        holder.verify_report_name.setText(project.getReportName());
        holder.verify_report_object.setText(project.getVerifyName());

        int attribute = project.getVerifyTypeInt();
        if (attribute == VerifyPorjectBean.TYPE_INCUBATORS) {
            holder.verify_report_attribute.setText(mContext.getResources().getString(R.string.verify_attribute_incubators));
        } else if (attribute == VerifyPorjectBean.TYPE_REFRIGERATED_CAR) {
            holder.verify_report_attribute.setText(mContext.getResources().getString(R.string.verify_attribute_refrigerated_car));
        } else if (attribute == VerifyPorjectBean.TYPE_COLD_STORES) {
            holder.verify_report_attribute.setText(mContext.getResources().getString(R.string.verify_attribute_cold_stores));
        }
        if (project.isWorked()){
            holder.verify_control_image.setImageResource(R.drawable.pause_green);
            holder.verify_control_text.setTextColor(0xFF00ff00);
            holder.verify_control_text.setText(R.string.stop_verity);
            holder.verify_status.setText(R.string.text_doing);
            holder.verify_status.setTextColor(0xFF00ff00);
        } else {
            holder.verify_control_image.setImageResource(R.drawable.play_white);
            holder.verify_control_text.setTextColor(0xFFffffff);
            holder.verify_control_text.setText(R.string.start_verify);
            holder.verify_status.setText(R.string.text_done);
            holder.verify_status.setTextColor(0xFFFFFFFF);
        }

        holder.check_layout_plan_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PictureShowActivity.class);
                intent.putExtra(Constant.KEY_PICTURE_URL, mDataList.get(position).getUrl());
                mMainActivity.startActivity(intent);
                mMainActivity.overridePendingTransition(R.anim.anim_big, R.anim.anim_null);
            }
        });

        holder.verify_control_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("test", " " + getDatas().get(position).getReportName() );
                if (getDatas().get(position).isWorked()) {
                    //project.setStatus(VerifyPorjectBean.STATUS_VERIFYED);
                    AppApplication.getDeivceManager().removeIdSet(mDataList.get(position).getReportNo());
                } else {
                    AppApplication.getDeivceManager().addIdSet(mDataList.get(position).getReportNo());
                    //project.setStatus(VerifyPorjectBean.STATUS_VERIFING);
                }
                notifyItemChanged(position);
            }
        });

        holder.check_verify_data_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(context.getString(R.string.check_verify_data)).append(position+1);
                //mMainActivity.setSelectionFragment(Constant.ITEMID_VERFIY_DATA);
                VerifyPorjectBean projectBean = mDataList.get(position);
                if (mListener!= null) {
                    mListener.onVerifyProject(projectBean);
                }
            }
        });

        holder.delete_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(context.getString(R.string.text_delete)).append(position+1);
                Toast.makeText(context,stringBuffer.toString(),Toast.LENGTH_LONG).show();
                AppApplication.getDeivceManager().removeIdSet(mDataList.get(position).getReportNo());
                mDataList.remove(position);
                notifyDataSetChanged();
            }
        });

    }


    public interface VerifyProjectClickListener {
         void onVerifyProject(VerifyPorjectBean bean);
    }

    public void onVerifyProjectClickListener(VerifyProjectClickListener listener) {
        this.mListener = listener;
    }


}
