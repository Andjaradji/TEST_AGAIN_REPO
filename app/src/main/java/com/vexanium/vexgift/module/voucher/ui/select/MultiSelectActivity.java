package com.vexanium.vexgift.module.voucher.ui.select;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.app.ConstantGroup;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.fixture.FixtureData;
import com.vexanium.vexgift.bean.model.BaseType;
import com.vexanium.vexgift.bean.model.SortFilterCondition;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.CategoryResponse;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.MemberTypeResponse;
import com.vexanium.vexgift.bean.response.PaymentTypeResponse;
import com.vexanium.vexgift.bean.response.VoucherTypeResponse;
import com.vexanium.vexgift.database.TableContentDaoUtil;
import com.vexanium.vexgift.module.voucher.presenter.IVoucherPresenter;
import com.vexanium.vexgift.module.voucher.presenter.IVoucherPresenterImpl;
import com.vexanium.vexgift.module.voucher.view.IVoucherView;
import com.vexanium.vexgift.util.ClickUtil;
import com.vexanium.vexgift.util.JsonUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ActivityFragmentInject(contentViewId = R.layout.activity_multi_select, toolbarTitle = R.string.filter_payment, withLoadingAnim = true)
public class MultiSelectActivity extends BaseActivity<IVoucherPresenter> implements IVoucherView {

    private ListView listView;
    private List<String> rawList;
    private List<String> selectedItems;
    private List<SingleSelectItem> itemList;
    private SortFilterCondition sortFilterCondition;
    private MultiSelectAdapter adapter;
    private VoucherTypeResponse voucherTypeResponse;
    private MemberTypeResponse memberTypeResponse;
    private PaymentTypeResponse paymentTypeResponse;
    private CategoryResponse categoryResponse;
    String listType;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        if (data != null) {
            if (data instanceof VoucherTypeResponse) {
                VoucherTypeResponse voucherTypeResponse = (VoucherTypeResponse) data;
                if (voucherTypeResponse != null && voucherTypeResponse.getVoucherTypes() != null) {
                    TableContentDaoUtil.getInstance().saveVoucherTypesToDb(JsonUtil.toString(voucherTypeResponse));
                    rawList = getRawListFromBaseType(voucherTypeResponse.getVoucherTypes());
                    loadData(rawList);
                }
            } else if (data instanceof MemberTypeResponse) {
                MemberTypeResponse memberTypeResponse = (MemberTypeResponse) data;
                if (memberTypeResponse != null && memberTypeResponse.getMemberTypes() != null) {
                    TableContentDaoUtil.getInstance().saveMemberTypesToDb(JsonUtil.toString(memberTypeResponse));
                    rawList = getRawListFromBaseType(memberTypeResponse.getMemberTypes());
                    loadData(rawList);
                }
            } else if (data instanceof PaymentTypeResponse) {
                PaymentTypeResponse paymentTypeResponse = (PaymentTypeResponse) data;
                if (paymentTypeResponse != null && paymentTypeResponse.getPaymentTypes() != null) {
                    TableContentDaoUtil.getInstance().savePaymentTypeToDb(JsonUtil.toString(paymentTypeResponse));
                    rawList = getRawListFromBaseType(paymentTypeResponse.getPaymentTypes());
                    loadData(rawList);
                }
            } else if (data instanceof CategoryResponse) {
                CategoryResponse categoryResponse = (CategoryResponse) data;
                if (categoryResponse != null && categoryResponse.getCategories() != null) {
                    TableContentDaoUtil.getInstance().saveCategoryToDb(JsonUtil.toString(categoryResponse));
                    rawList = getRawListFromBaseType(categoryResponse.getCategories());
                    loadData(rawList);
                }
            }

        } else if (errorResponse != null) {
            StaticGroup.showCommonErrorDialog(this, errorResponse);
        }
    }

    @Override
    protected void initView() {
        user = User.getCurrentUser(this);
        mPresenter = new IVoucherPresenterImpl(this);

        if (getIntent().hasExtra("condition")) {
            KLog.v("HPtes condition " + getIntent().getStringExtra("condition"));
            sortFilterCondition = (SortFilterCondition) JsonUtil.toObject(getIntent().getStringExtra("condition"), SortFilterCondition.class);
        }

        if (getIntent().hasExtra("type")) {
            listType = getIntent().getStringExtra("type");
            ((TextView) findViewById(R.id.tv_toolbar_title)).setText(listType.toUpperCase());
        }

        rawList = new ArrayList<>();

        if (listType != null) {
            if (listType.equalsIgnoreCase("voucher")) {
                voucherTypeResponse = TableContentDaoUtil.getInstance().getVoucherTypes();
                selectedItems = sortFilterCondition.getVoucherTypes();

                if (voucherTypeResponse != null && voucherTypeResponse.getVoucherTypes() != null) {
                    rawList = getRawListFromBaseType(voucherTypeResponse.getVoucherTypes());
                }else{
                    mPresenter.requestVoucherType(user.getId());
                }

            } else if (listType.equalsIgnoreCase("member")) {
                memberTypeResponse = TableContentDaoUtil.getInstance().getMemberTypes();
                selectedItems = sortFilterCondition.getMemberTypes();

                if (memberTypeResponse != null && memberTypeResponse.getMemberTypes() != null) {
                    rawList = getRawListFromBaseType(memberTypeResponse.getMemberTypes());
                }else{
                    mPresenter.requestMemberType(user.getId());
                }

            } else if (listType.equalsIgnoreCase("payment")) {
                paymentTypeResponse = TableContentDaoUtil.getInstance().getPaymentTypes();
                selectedItems = sortFilterCondition.getPaymentTypes();

                if (paymentTypeResponse != null && paymentTypeResponse.getPaymentTypes() != null) {
                    rawList = getRawListFromBaseType(paymentTypeResponse.getPaymentTypes());
                }else{
                    mPresenter.requestPaymentType(user.getId());
                }

            } else if (listType.equalsIgnoreCase("category")) {
                categoryResponse = TableContentDaoUtil.getInstance().getCategories();
                selectedItems = sortFilterCondition.getCategories();

                if (categoryResponse != null && categoryResponse.getCategories() != null) {
                    rawList = getRawListFromBaseType(categoryResponse.getCategories());
                }else{
                    mPresenter.requestCategories(user.getId());
                }

            } else if (listType.equalsIgnoreCase("location")) {
                selectedItems = sortFilterCondition.getLocation();
                rawList = FixtureData.countries;
            }

//
//            if (listType.equalsIgnoreCase("member")) {
//                selectedItems = sortFilterCondition.getMemberTypes();
//                rawList = FixtureData.categoryList;
//            } else if (listType.equalsIgnoreCase("location")) {
//                selectedItems = sortFilterCondition.getLocation();
//                rawList = FixtureData.locationList;
//            } else if (listType.equalsIgnoreCase("payment")) {
//                selectedItems = sortFilterCondition.getPaymentTypes();
//                rawList = FixtureData.typeList;
//            }

            loadData(rawList);

            listView = findViewById(R.id.listview);
            listView.setAdapter(adapter);
        }
    }

    @Override
    public void finish() {
        if (sortFilterCondition != null && listType != null) {
            int resultCode;
            switch (listType) {
                case "category":
                    resultCode = ConstantGroup.EDIT_FILTER_CATEGORY_RESULT_CODE;
                    break;
                case "member":
                    resultCode = ConstantGroup.EDIT_FILTER_MEMBER_TYPE_RESULT_CODE;
                    break;
                case "voucher":
                    resultCode = ConstantGroup.EDIT_FILTER_VOUCHER_TYPE_RESULT_CODE;
                    break;
                case "location":
                    resultCode = ConstantGroup.EDIT_FILTER_LOCATION_RESULT_CODE;
                    break;
                case "payment":
                    resultCode = ConstantGroup.EDIT_FILTER_PAYMENT_TYPE_RESULT_CODE;
                    break;
                default:
                    resultCode = 0;
            }
            Intent intent = getIntent();
            intent.putExtra("condition", JsonUtil.toString(sortFilterCondition));
            setResult(resultCode, intent);
        }

        super.finish();
    }

    private void loadData(List<String> list) {
        itemList = new ArrayList<>();

        if (rawList != null) {
            for (int i = 0; i < rawList.size(); i++) {
                SingleSelectItem singleSelectItem = new SingleSelectItem();
                singleSelectItem.key = rawList.get(i);
                singleSelectItem.title = rawList.get(i);

                if (selectedItems != null && selectedItems.size() > 0) {
                    if (selectedItems.contains(rawList.get(i))) {
                        singleSelectItem.isSelected = true;
                    }
                }
                KLog.v("HPtes filter " + rawList.get(i));

                itemList.add(singleSelectItem);
            }
        }

        if (adapter == null) {
            adapter = new MultiSelectAdapter(itemList);
        } else {
            adapter.setArrMenu(itemList);
        }
    }

    private List<String> getRawListFromBaseType(ArrayList<? extends BaseType> baseTypes) {
        List<String> list = new ArrayList<>();
        for (BaseType baseType : baseTypes) {
            list.add(baseType.getName());
        }
        return list;
    }

    public class MultiSelectAdapter extends BaseAdapter {
        List<SingleSelectItem> arrMenu;

        MultiSelectAdapter(List<SingleSelectItem> arrOptions) {
            this.arrMenu = arrOptions;
        }

        public void updateListView(List<SingleSelectItem> mArray) {
            this.arrMenu = mArray;
            notifyDataSetChanged();
        }

        public void setArrMenu(List<SingleSelectItem> arrMenu) {
            this.arrMenu = arrMenu;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return this.arrMenu.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_multi_select, null);
                viewHolder = new ViewHolder();
                viewHolder.mTtvName = (TextView) convertView.findViewById(R.id.tvName);
                viewHolder.mTvSelected = (ImageView) convertView.findViewById(R.id.tvSelected);
                viewHolder.mTvUnselected = (ImageView) convertView.findViewById(R.id.tvUnselected);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final SingleSelectItem selectItem = arrMenu.get(position);
            viewHolder.mTtvName.setText(selectItem.title);
            viewHolder.mTvSelected.setVisibility(selectItem.isSelected ? View.VISIBLE : View.INVISIBLE);
            viewHolder.mTvUnselected.setVisibility(!selectItem.isSelected ? View.VISIBLE : View.INVISIBLE);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ClickUtil.isFastDoubleClick()) {
                        return;
                    }
                    selectItem.isSelected = !selectItem.isSelected;
                    if (selectedItems == null) selectedItems = new ArrayList<>();
                    if (selectItem.isSelected) {
                        if (selectedItems.size() != 0) {
                            if (!selectedItems.contains(selectItem.key)) {
                                selectedItems.add(selectItem.key);
                            }
                        } else {
                            selectedItems.add(selectItem.key);
                        }
                    } else if (selectedItems.size() != 0) {
                        if (selectedItems.contains(selectItem.key)) {
                            selectedItems.remove(selectItem.key);
                        }
                    }

                    if (listType.equalsIgnoreCase("member")) {
                        sortFilterCondition.setMemberTypes(selectedItems);
                    } else if (listType.equalsIgnoreCase("location")) {
                        sortFilterCondition.setLocation(selectedItems);
                    } else if (listType.equalsIgnoreCase("payment")) {
                        sortFilterCondition.setPaymentTypes(selectedItems);
                    } else if (listType.equalsIgnoreCase("category")) {
                        sortFilterCondition.setCategories(selectedItems);
                    } else if (listType.equalsIgnoreCase("voucher")) {
                        sortFilterCondition.setVoucherTypes(selectedItems);
                    }

                    notifyDataSetChanged();
                }
            });
            App.setTextViewStyle((ViewGroup) convertView);
            return convertView;
        }

        class ViewHolder {
            TextView mTtvName;
            ImageView mTvSelected, mTvUnselected;
        }
    }

}
