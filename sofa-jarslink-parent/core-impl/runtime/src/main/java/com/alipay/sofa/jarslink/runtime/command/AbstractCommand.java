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

import com.alipay.sofa.ark.spi.service.biz.BizFactoryService;
import com.alipay.sofa.ark.spi.service.biz.BizManagerService;
import com.alipay.sofa.jarslink.common.log.JSKLogger;
import com.alipay.sofa.jarslink.common.log.JSKLoggerFactory;
import com.alipay.sofa.jarslink.runtime.service.BizServiceHolder;
import com.alipay.sofa.jarslink.spi.command.Command;
import com.alipay.sofa.jarslink.spi.command.CommandOption;

/**
 * @author qilong.zql
 * @since 0.1.0
 */
public abstract class AbstractCommand implements Command {

    protected static final JSKLogger LOGGER             = JSKLoggerFactory.getDefaultLogger();

    /**
     * Jarslink management command must bring with this type option.
     */
    public final static String       typeOption         = "-biz";
    public final static String       typeOptionAlias    = "-b";

    public final static String       nameOption         = "-name";
    public final static String       nameOptionAlias    = "-n";

    public final static String       versionOption      = "-version";
    public final static String       versionOptionAlias = "-v";

    protected CommandOption[]        commandOptions;

    protected CommandOption          typeCommandOption;
    protected CommandOption          nameCommandOption;
    protected CommandOption          versionCommandOption;

    protected BizManagerService      bizManagerService;
    protected BizFactoryService      bizFactoryService;

    AbstractCommand(CommandOption[] commandOptions) {
        this.commandOptions = commandOptions;
        bizManagerService = BizServiceHolder.getBizManagerService();
        bizFactoryService = BizServiceHolder.getBizFactoryService();
    }

    @Override
    public boolean validate() {
        boolean valid = true;
        for (int i = 0; i < commandOptions.length && valid; ++i) {
            CommandOption option = commandOptions[i];
            if (typeOption.equals(option.getOption()) || typeOptionAlias.equals(option.getOption())) {
                valid = valid && validateTypeOption(option);
            } else if (nameOption.equals(option.getOption())
                       || nameOptionAlias.equals(option.getOption())) {
                valid = valid && validateNameOption(option);
            } else if (versionOption.equals(option.getOption())
                       || versionOptionAlias.equals(option.getOption())) {
                valid = valid && validateVersionOption(option);
            } else {
                valid = false;
            }
        }
        return valid && typeCommandOption != null;
    }

    protected boolean validateTypeOption(CommandOption option) {
        typeCommandOption = option;
        return option.getArgs().length < 2;
    }

    protected boolean validateNameOption(CommandOption option) {
        nameCommandOption = option;
        return option.getArgs().length == 1;
    }

    protected boolean validateVersionOption(CommandOption option) {
        versionCommandOption = option;
        return option.getArgs().length == 1;
    }
}