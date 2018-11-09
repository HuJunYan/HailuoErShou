package com.huaxi.hailuo.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaxi.hailuo.R;
import com.huaxi.hailuo.base.App;
import com.huaxi.hailuo.base.GlobalParams;
import com.huaxi.hailuo.model.bean.CreditAssessBean;
import com.huaxi.hailuo.ui.activity.AuthExtroContactsActivity;
import com.huaxi.hailuo.ui.activity.AuthInfoActivity;
import com.huaxi.hailuo.ui.activity.IdentityActivity;
import com.huaxi.hailuo.ui.activity.WebVerifyActivity;
import com.huaxi.hailuo.util.ImageUtil;
import com.huaxi.hailuo.util.ToastUtil;
import com.huaxi.hailuo.util.UserUtil;
import com.moxie.client.manager.MoxieCallBack;
import com.moxie.client.manager.MoxieCallBackData;
import com.moxie.client.manager.MoxieContext;
import com.moxie.client.manager.MoxieSDK;
import com.moxie.client.model.MxParam;
import com.moxie.client.model.TitleParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wang on 2018/1/29.
 */

public class CreditAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public interface ItemType {
        int TYPE_REQUIRED = 3;
        int TYPE_NOT_REQUIRED = 4;
    }

    private boolean isAuthIdentity = false;
    private boolean isCommit = false;
    private List<CreditAssessBean.CreditAssessItemBean> mHideData;
    private Activity mActivity;
    private List<CreditAssessBean.CreditAssessItemBean> mData;

    public CreditAdapter(Activity activity, List<CreditAssessBean.CreditAssessItemBean> data) {
        this.mActivity = activity;
        this.mData = data;
    }

    public void refreshData(boolean isCommit, ArrayList<CreditAssessBean.CreditAssessItemBean> hideData, boolean isAuthIdentity) {
        this.isAuthIdentity = isAuthIdentity;
        this.isCommit = isCommit;
        mHideData = hideData;
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ItemType.TYPE_REQUIRED || viewType == ItemType.TYPE_NOT_REQUIRED) {
            return new RequiredViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_required_holder, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int local_item_type = mData.get(position).getLocal_item_type();
        if (local_item_type == ItemType.TYPE_REQUIRED || local_item_type == ItemType.TYPE_NOT_REQUIRED) {
            ((RequiredViewHolder) holder).onBindViewHolder(position);
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mData == null) {
            return super.getItemViewType(position);
        } else {
            int local_item_type = mData.get(position).getLocal_item_type();
            if (local_item_type == ItemType.TYPE_NOT_REQUIRED) {
                return ItemType.TYPE_NOT_REQUIRED;
            } else if (local_item_type == ItemType.TYPE_REQUIRED) {
                return ItemType.TYPE_REQUIRED;
            } else {
                return super.getItemViewType(position);
            }
        }
    }

    class RequiredViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_required_holder_title;
        TextView tv_required_title;
        TextView tv_required_status;

        public RequiredViewHolder(View itemView) {
            super(itemView);
            iv_required_holder_title = itemView.findViewById(R.id.iv_required_holder_title);
            tv_required_title = itemView.findViewById(R.id.tv_required_title);
            tv_required_status = itemView.findViewById(R.id.tv_required_status);
        }

        void onBindViewHolder(final int position) {
            final CreditAssessBean.CreditAssessItemBean creditAssessItemBean = mData.get(position);
            String item_name = creditAssessItemBean.getItem_name();
            tv_required_title.setText(item_name);
            if ("1".equals(creditAssessItemBean.getItem_status())) {
                tv_required_status.setText("已认证");
                tv_required_status.setBackgroundResource(R.drawable.shape_orange_conrner_circle);
            } else if ("2".equals(creditAssessItemBean.getItem_status())) {
                tv_required_status.setText("去认证");
                tv_required_status.setBackgroundResource(R.drawable.global_button_selector_circle);
            } else {
                tv_required_status.setText("");
            }
            ImageUtil.INSTANCE.load(mActivity, creditAssessItemBean.getItem_icon(), iv_required_holder_title);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkToJump(creditAssessItemBean);
                }
            });
        }
    }

    /**
     * 判断是否满足跳转条件
     */
    public void checkToJump(CreditAssessBean.CreditAssessItemBean creditAssessItemBean) {
        //判断是否满足条件去认证  暂时注释掉
        if (!isAuthIdentity && !"1".equals(creditAssessItemBean.getItem_num())) {
            ToastUtil.showToast(mActivity, "请先进行身份认证", ToastUtil.SHOW);
            return;
        }
        String item_is_click = creditAssessItemBean.getItem_is_click();
        if ("2".equals(item_is_click)) {
            ToastUtil.showToast(mActivity, "已认证，无需再认证", ToastUtil.SHOW);
        } else {
            String item_num = creditAssessItemBean.getItem_num();
            switch (item_num) {
                case "1"://1身份认证
                    gotoActivity(mActivity, IdentityActivity.class, null);
                    mNewActivityListener.onClick(null);
                    break;
//                case "2"://2银行卡
//                    gotoActivity(mActivity, BindBankCardActivity.class, null);
//                    break;
                case "3"://3运营商
                    Intent intent = new Intent(mActivity, WebVerifyActivity.class);
                    intent.putExtra(WebVerifyActivity.Companion.getWEB_URL_KEY(), creditAssessItemBean.getJump_url());
                    intent.putExtra(WebVerifyActivity.Companion.getWEB_URL_TITLE(), "手机运营商");
                    gotoActivity(intent, mActivity);
                    mNewActivityListener.onClick(null);
                    break;
                case "4"://4联系人
                    gotoActivity(mActivity, AuthExtroContactsActivity.class, null);
                    mNewActivityListener.onClick(null);
                    break;
                case "5":// 5个人信息
                    gotoActivity(mActivity, AuthInfoActivity.class, null);
                    mNewActivityListener.onClick(null);
                    break;
                case "6"://6芝麻信用
                    Intent intent2 = new Intent(mActivity, WebVerifyActivity.class);
                    intent2.putExtra(WebVerifyActivity.Companion.getWEB_URL_KEY(), creditAssessItemBean.getJump_url());
                    intent2.putExtra(WebVerifyActivity.Companion.getWEB_URL_TITLE(), "芝麻信用");
                    gotoActivity(intent2, mActivity);
                    mNewActivityListener.onClick(null);
                    break;
                case "7"://7淘宝
                    gotoMoXieActivity(creditAssessItemBean, true);
                    mNewActivityListener.onClick(null);
                    break;
                case "8"://8京东
                case "9"://9信用卡
                case "10"://10社保
                case "11"://11公积金
                case "12"://12学信网
                case "13"://13央行征信
                    gotoMoXieActivity(creditAssessItemBean, false);
                    mNewActivityListener.onClick(null);
                    break;
                case "14"://14微博
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 跳转到某个Activity
     */
    protected void gotoActivity(Activity mContext, Class toActivityClass, Bundle bundle) {
        Intent intent = new Intent(mContext, toActivityClass);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        mContext.startActivity(intent);
        mContext.overridePendingTransition(R.anim.push_right_in, R.anim.not_exit_push_left_out);
    }

    /**
     * 跳转到某个Activity
     */
    protected void gotoActivity(Intent intent, Activity activity) {
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.push_right_in, R.anim.not_exit_push_left_out);
    }

    private void gotoMoXieActivity(final CreditAssessBean.CreditAssessItemBean creditAssessItemBean, final boolean isTaobao) {

        String item_num = creditAssessItemBean.getItem_num();
        String apiKey = "";
        switch (App.Companion.getInstance().getMCurrentHost()) {
            //测试key
            case DEV:
                apiKey = "c916e9aac6a244c2aa47552669c5a1e0";
                break;
            //正式key
            case PRE:
                apiKey = "012a5b3a9bf94ac984fbb7c400c460aa";
                break;
            //正式key
            case PRO:
                apiKey = "012a5b3a9bf94ac984fbb7c400c460aa";
                break;
            default:
                apiKey = "c916e9aac6a244c2aa47552669c5a1e0";
        }
        final MxParam mxParam = new MxParam();
        mxParam.setUserId(UserUtil.INSTANCE.getUserId(mActivity) + "-" + GlobalParams.PLATFORM_FLAG);
        mxParam.setApiKey(apiKey);

        TitleParams titleParams = new TitleParams.Builder()
                //标题字体颜色
                .titleColor(mActivity.getResources().getColor(R.color.white))
                //title背景色
                .backgroundDrawable(R.drawable.bg_gradient)
                //title返回按钮旁边的文字（关闭）的颜色
                .leftTextColor(mActivity.getResources().getColor(R.color.white))
                //title返回按钮旁边的文字，一般为关闭
                .leftText("关闭")
                //是否支持沉浸式
                .immersedEnable(true)
                .build();
        mxParam.setTitleParams(titleParams);

        //,7淘宝，8京东，9信用卡 ，10社保 ,11公积金 ,12学信网
        switch (item_num) {
            case "7":
                mxParam.setTaskType(MxParam.PARAM_TASK_TAOBAO);
                break;
            case "8":
                mxParam.setTaskType(MxParam.PARAM_TASK_JINGDONG);
                break;
            case "9": // 这个并不是信用卡
                mxParam.setTaskType(MxParam.PARAM_TASK_EMAIL);
                break;
            case "10":
                mxParam.setTaskType(MxParam.PARAM_TASK_SECURITY);
                break;
            case "11":
                mxParam.setTaskType(MxParam.PARAM_TASK_FUND);
                break;
            case "12":
                mxParam.setTaskType(MxParam.PARAM_TASK_CHSI);
                break;
            case "13":
//                mxParam.setTaskType(MxParam.PARAM_FUNCTION_ZHENGXIN);
                break;
            default:
                break;
        }
        MoxieSDK.getInstance().start(mActivity, mxParam, new MoxieCallBack() {
            @Override
            public boolean callback(MoxieContext moxieContext, MoxieCallBackData moxieCallBackData) {
                if (moxieCallBackData != null) {
                    switch (moxieCallBackData.getCode()) {
                        case MxParam.ResultCode.IMPORTING:
                        case MxParam.ResultCode.IMPORT_UNSTART:
                            break;
                        case MxParam.ResultCode.THIRD_PARTY_SERVER_ERROR:
                        case MxParam.ResultCode.MOXIE_SERVER_ERROR:
                        case MxParam.ResultCode.USER_INPUT_ERROR:
                        case MxParam.ResultCode.IMPORT_FAIL:
                            ToastUtil.showToast(mActivity, "认证失败!");
                            moxieContext.finish();
                            break;
                        case MxParam.ResultCode.IMPORT_SUCCESS:
                            notifyDataSetChanged();
                            /*
                            if (isTaobao) {
                                //如果是淘宝  发送消息
                                EventBus.getDefault().post(new RefreshCreditStatusEvent());
                            } else {
                                //不是淘宝则 不需要自动跳转 发送另一个event 刷新接口 暂时不需要做任何事
                                // do nothing
                                //                            EventBus.getDefault().post(new RefreshCreditStatusEvent());
                            }
                            */
                            //刷新接口
                            moxieContext.finish();
                            break;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

    }


    private View.OnClickListener mNewActivityListener;

    public void setStartNewActivityListener(View.OnClickListener mListener) {
        this.mNewActivityListener = mListener;
    }

}
