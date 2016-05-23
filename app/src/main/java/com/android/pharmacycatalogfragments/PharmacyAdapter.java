package com.android.pharmacycatalogfragments;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.pharmacycatalogfragments.DatabasePart.PharmacyContract.CatalogEntry;

public class PharmacyAdapter extends CursorAdapter {

    public static class ViewHolder {

        public final TextView nameView;
        public final TextView sectionView;
        public final TextView priceView;
        public final TextView quantityView;

        public ViewHolder(View view) {

            nameView = (TextView)view.findViewById(R.id.itemNameView);
            sectionView = (TextView)view.findViewById(R.id.sectionView);
            priceView = (TextView)view.findViewById(R.id.itemPriceView);
            quantityView = (TextView)view.findViewById(R.id.quantityView);

        }
    }

    public PharmacyAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_catalog, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder)view.getTag();

        String itemName = cursor.getString(CatalogEntry.COL_INDEX_ITEM_NAME);
        String vendorName = cursor.getString(CatalogEntry.COL_INDEX_VENDOR_NAME);

        viewHolder.nameView.setText(itemName + ", " + vendorName);

        double itemPrice = cursor.getDouble(CatalogEntry.COL_INDEX_ITEM_PRICE);
        viewHolder.priceView.setText(itemPrice + " руб.");

        int quantity = cursor.getInt(CatalogEntry.COL_INDEX_QUANTITY);
        viewHolder.quantityView.setText(quantity + " шт.");

        String section = cursor.getString(CatalogEntry.COL_INDEX_SECTION);
        viewHolder.sectionView.setText(section);
    }
}
