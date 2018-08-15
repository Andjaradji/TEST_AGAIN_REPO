package com.vexanium.vexgift.widget.select;

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
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.fixture.FixtureData;
import com.vexanium.vexgift.bean.model.SortFilterCondition;
import com.vexanium.vexgift.util.ClickUtil;
import com.vexanium.vexgift.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

@ActivityFragmentInject(contentViewId = R.layout.activity_multi_select, toolbarTitle = R.string.filter_payment)
public class MultiSelectActivity extends BaseActivity {

    private ListView listView;
    private List<String> rawList;
    private List<String> selectedItems;
    private List<SingleSelectItem> itemList;
    private SortFilterCondition sortFilterCondition;
    private MultiSelectAdapter adapter;
    String listType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().hasExtra("condition")) {
            KLog.v("HPtes condition "+getIntent().getStringExtra("condition"));
            sortFilterCondition = (SortFilterCondition) JsonUtil.toObject(getIntent().getStringExtra("condition"), SortFilterCondition.class);
        }

        if (getIntent().hasExtra("type")) {
            listType = getIntent().getStringExtra("type");
            ((TextView) findViewById(R.id.tv_toolbar_title)).setText(listType.toUpperCase());
        }

        if (listType != null) {
            if (listType.equalsIgnoreCase("member")) {
                selectedItems = sortFilterCondition.getMemberType();
                rawList = FixtureData.categoryList;
            } else if (listType.equalsIgnoreCase("location")) {
                selectedItems = sortFilterCondition.getLocation();
                rawList = FixtureData.locationList;
            } else if (listType.equalsIgnoreCase("payment")) {
                selectedItems = sortFilterCondition.getPaymentType();
                rawList = FixtureData.typeList;
            }

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
                    KLog.v("HPtes filter "+rawList.get(i));

                    itemList.add(singleSelectItem);
                }
            }

            adapter = new MultiSelectAdapter(itemList);
            listView = findViewById(R.id.listview);
            listView.setAdapter(adapter);
        }
    }

    @Override
    protected void initView() {


    }

    @Override
    public void finish() {
        if (sortFilterCondition != null && listType != null) {
            int resultCode;
            switch (listType) {
                case "category":
                    resultCode = ConstantGroup.EDIT_FILTER_CATEGORY_RESULT_CODE;
                    break;
                case "type":
                    resultCode = ConstantGroup.EDIT_FILTER_TYPE_RESULT_CODE;
                    break;
                case "location":
                    resultCode = ConstantGroup.EDIT_FILTER_LOCATION_RESULT_CODE;
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

    public class MultiSelectAdapter extends BaseAdapter {
        List<SingleSelectItem> arrMenu;

        MultiSelectAdapter(List<SingleSelectItem> arrOptions) {
            this.arrMenu = arrOptions;
        }

        public void updateListView(List<SingleSelectItem> mArray) {
            this.arrMenu = mArray;
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
                        sortFilterCondition.setMemberType(selectedItems);
                    } else if (listType.equalsIgnoreCase("location")) {
                        sortFilterCondition.setLocation(selectedItems);
                    } else if (listType.equalsIgnoreCase("payment")) {
                        sortFilterCondition.setPaymentType(selectedItems);
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
