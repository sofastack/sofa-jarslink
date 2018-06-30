/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.jarslink.spi.command;

/**
 * @author qilong.zql
 * @since 0.1.0
 */
public enum CommandType {
    INSTALL("install", "install    <-b>|<-biz>  <url> 安装 ark-biz, 指定 biz 包的地址。"), UNINSTALL(
                                                                                            "uninstall",
                                                                                            "uninstall  <-b>|<-biz>  <-n><name>  <-v><version> 卸载 ark-biz, 指定 biz 名字和版本号。"), SWITCH(
                                                                                                                                                                                    "switch",
                                                                                                                                                                                    "switch     <-b>|<-biz>  <-n><name> <-v><version> 切换 ark-biz 为激活状态, 指定 biz 名字和版本号。"), CHECK(
                                                                                                                                                                                                                                                                                "check",
                                                                                                                                                                                                                                                                                "check      <-b>|<-biz>  [<-n><name>]  [<-v><version>] 查询 ark-biz 信息。"), UNKNOWN(
                                                                                                                                                                                                                                                                                                                                                                 "unknown",
                                                                                                                                                                                                                                                                                                                                                                 "unknown");

    private final String commandMarker;

    private final String commandHelp;

    CommandType(String marker, String help) {
        this.commandMarker = marker;
        this.commandHelp = help;
    }

    public String getCommandMarker() {
        return commandMarker;
    }

    public String getCommandHelp() {
        return commandHelp;
    }

    public static CommandType getCommandType(String commandMarker) {
        if (INSTALL.getCommandMarker().equals(commandMarker)) {
            return INSTALL;
        } else if (UNINSTALL.getCommandMarker().equals(commandMarker)) {
            return UNINSTALL;
        } else if (CHECK.getCommandMarker().equals(commandMarker)) {
            return CHECK;
        } else if (SWITCH.getCommandMarker().equals(commandMarker)) {
            return SWITCH;
        } else {
            return UNKNOWN;
        }
    }
}