package com.okamayana.pebblechallenge.models;

import com.okamayana.pebblechallenge.R;

public class Command {

    private CommandType mCommandType;
    private int mRed;
    private int mGreen;
    private int mBlue;

    public Command(CommandType commandType, int red, int green, int blue) {
        mCommandType = commandType;
        mRed = red;
        mGreen = green;
        mBlue = blue;
    }

    public CommandType getCommandType() {
        return mCommandType;
    }

    public int getRed() {
        return mRed;
    }

    public int getGreen() {
        return mGreen;
    }

    public int getBlue() {
        return mBlue;
    }

    public enum CommandType {
        ABSOLUTE(R.string.command_absolute), RELATIVE(R.string.command_relative);

        private int mResId;

        private CommandType(int resId) {
            mResId = resId;
        }

        public int getResId() {
            return mResId;
        }
    }
}
