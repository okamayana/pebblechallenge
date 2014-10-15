package com.okamayana.pebblechallenge.adapters;

import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.okamayana.pebblechallenge.R;
import com.okamayana.pebblechallenge.models.Command;

import java.util.List;

public class CommandsAdapter extends ArrayAdapter<Command> {

    private List<Command> mItems;
    private SparseBooleanArray mChecked;

    public CommandsAdapter(Context context, int resource, List<Command> items) {
        super(context, resource);
        mItems = items;
        mChecked = new SparseBooleanArray();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_command, null);

            viewHolder = new ViewHolder();
            viewHolder.commandTypeTextView = (TextView) convertView.findViewById(R.id.text_view_command_type);
            viewHolder.commandArgsTextView = (TextView) convertView.findViewById(R.id.text_view_command_args);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox_command);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Command command = getItem(position);
        viewHolder.commandTypeTextView.setText(command.getCommandType().getResId());
        viewHolder.commandArgsTextView.setText(String.format("Red: %d, Green: %d, Blue: %d",
                command.getRed(), command.getGreen(), command.getBlue()));

        viewHolder.checkBox.setChecked(mChecked.get(position, true));
        mChecked.put(position, viewHolder.checkBox.isChecked());

        return convertView;
    }

    @Override
    public Command getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    public void setItems(List<Command> items) {
        mItems = items;
    }

    public void uncheckAllItems() {
        for (int i = 0; i < mItems.size(); i++) {
            mChecked.put(i, false);
        }
    }

    public void uncheckAllItemsExcept(int position) {
        for (int i = 0; i < mItems.size(); i++) {
            if (i != position) {
                mChecked.put(i, false);
            }
        }
    }

    public void checkItem(int position, boolean check) {
        mChecked.put(position, check);
    }

    private class ViewHolder {
        TextView commandTypeTextView;
        TextView commandArgsTextView;
        CheckBox checkBox;
    }
}
