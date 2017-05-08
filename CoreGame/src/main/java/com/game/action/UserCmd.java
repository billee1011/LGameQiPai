/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.game.action;


import com.game.core.constant.GameConst;
import com.lsocket.handler.ModuleCmd;
import com.lsocket.module.HttpRequestType;

/**
 */
public enum UserCmd implements ModuleCmd {

    /**
     * 未定义
     */
    NULL(-1,true,HttpRequestType.tcp),
    ;

    private final int value;
    private final boolean isRequireOnline;//是否需要登录
    private final HttpRequestType requetType;//请求方式

    UserCmd(int value, boolean isRequireOnline, HttpRequestType requetType) {
        this.value = value;
        this.requetType = requetType;
        this.isRequireOnline = isRequireOnline;
    }

    public int getValue() {
        return value;
    }

    public boolean isRequireOnline() {
        return isRequireOnline;
    }

    public HttpRequestType getRequetType() {
        return requetType;
    }

    @Override
    public int getModule() {
        return GameConst.MOUDLE_User;
    }
}
