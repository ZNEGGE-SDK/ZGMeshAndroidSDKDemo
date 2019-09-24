package com.example.meshdemo.module;

import java.io.Serializable;

public abstract class CommandAble implements Serializable {
    public abstract int getCommandType();

    public abstract int getCommandAddress();

    public abstract boolean isOpen();
}
