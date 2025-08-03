package com.example.lengthconverter;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Html;
import android.view.*;
import android.widget.*;
import java.util.*;

public class HistoryAdapter extends BaseAdapter {
    private final Context context;
    private final List<String> historyList;
    private final LayoutInflater inflater;

    public HistoryAdapter(Context context, List<String> historyList) {
        this.context = context;
        this.historyList = historyList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return historyList.size();
    }

    @Override
    public Object getItem(int position) {
        return historyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void removeItem(int position) {
        historyList.remove(position);
        saveUpdatedHistory();
        notifyDataSetChanged();
    }

    private void saveUpdatedHistory() {
        SharedPreferences preferences = context.getSharedPreferences("LengthConverterPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Set<String> updatedSet = new LinkedHashSet<>(historyList); // maintain order
        editor.putStringSet("history_set", updatedSet);
        editor.apply();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.history_item, parent, false);

        TextView historyText = view.findViewById(R.id.historyText);
        ImageButton deleteButton = view.findViewById(R.id.deleteButton);

        historyText.setText(Html.fromHtml(historyList.get(position), Html.FROM_HTML_MODE_LEGACY));

        deleteButton.setOnClickListener(v -> {
            removeItem(position);
            Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}