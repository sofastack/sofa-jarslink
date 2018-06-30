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
package com.alipay.sofa.jarslink.runtime.command;

import com.alipay.sofa.ark.common.util.AssertUtils;
import com.alipay.sofa.ark.common.util.StringUtils;
import com.alipay.sofa.ark.spi.constant.Constants;
import com.alipay.sofa.jarslink.spi.command.Command;
import com.alipay.sofa.jarslink.spi.command.CommandOption;
import com.alipay.sofa.jarslink.spi.command.CommandParser;
import com.alipay.sofa.jarslink.spi.command.CommandType;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link CommandParser}
 *
 * @author qilong.zql
 * @since 0.1.0
 */
public class DefaultCommandParser implements CommandParser {

    @Override
    public Command parseCommand(String telnetInput) {
        AssertUtils.isFalse(StringUtils.isEmpty(telnetInput), "Command input must not be empty");
        String commandMarker = parseCommandMarker(telnetInput.trim());
        CommandOption[] commandOptions = parseCommandOption(telnetInput.trim());
        return createCommand(commandMarker, commandOptions);
    }

    public static Command createCommand(String commandMarker, CommandOption[] commandOptions) {
        switch (CommandType.getCommandType(commandMarker)) {
            case INSTALL:
                return new InstallCommand(commandOptions);
            case UNINSTALL:
                return new UninstallCommand(commandOptions);
            case CHECK:
                return new CheckCommand(commandOptions);
            case SWITCH:
                return new SwitchCommand(commandOptions);
            default:
                return null;
        }
    }

    private String parseCommandMarker(String telnetInput) {
        return telnetInput.split(Constants.SPACE_SPLIT)[0];
    }

    private CommandOption[] parseCommandOption(String telnetInput) {
        String[] phrases = telnetInput.split(Constants.SPACE_SPLIT);
        List<CommandOption> options = new ArrayList<>();
        if (phrases.length == 1) {
            return options.toArray(new CommandOption[] {});
        }

        CommandOption temp = new CommandOption();
        for (int i = 1; i < phrases.length; ++i) {
            if (phrases[i].startsWith(CommandOption.OPTION_MARKER)) {
                if (StringUtils.isEmpty(temp.getOption()) && temp.getArgs().length != 0) {
                    temp.setOption(CommandOption.INVALID_OPTION);
                    options.add(temp);
                    temp = new CommandOption();
                } else if (!StringUtils.isEmpty(temp.getOption())) {
                    options.add(temp);
                    temp = new CommandOption();
                }
                temp.setOption(phrases[i]);
            } else {
                temp.addArgs(phrases[i]);
            }
        }
        if (temp.getArgs().length != 0 || !StringUtils.isEmpty(temp.getOption())) {
            if (StringUtils.isEmpty(temp.getOption())) {
                temp.setOption(CommandOption.INVALID_OPTION);
            }
            options.add(temp);
        }

        return options.toArray(new CommandOption[] {});
    }

}