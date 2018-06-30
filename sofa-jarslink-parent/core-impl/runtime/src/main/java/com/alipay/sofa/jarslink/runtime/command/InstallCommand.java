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

import com.alipay.sofa.ark.common.thread.ThreadPoolManager;
import com.alipay.sofa.ark.common.util.AssertUtils;
import com.alipay.sofa.ark.common.util.EnvironmentUtils;
import com.alipay.sofa.ark.common.util.StringUtils;
import com.alipay.sofa.ark.spi.model.Biz;
import com.alipay.sofa.jarslink.spi.command.CommandOption;
import com.alipay.sofa.jarslink.spi.command.CommandType;
import com.alipay.sofa.jarslink.spi.constant.Constants;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author qilong.zql
 * @since 0.1.0
 */
public class InstallCommand extends AbstractCommand {

    private static final AtomicBoolean INSTALLING = new AtomicBoolean(false);
    private static String              preBiz     = StringUtils.EMPTY_STRING;

    public InstallCommand(CommandOption[] commandOptions) {
        super(commandOptions);
    }

    @Override
    public CommandType supportCommandType() {
        return CommandType.INSTALL;
    }

    @Override
    public boolean validate() {
        boolean invalid = super.validate();
        invalid = invalid && commandOptions.length == 1 && nameCommandOption == null
                  && versionCommandOption == null && typeCommandOption.getArgs().length == 1;
        return invalid;
    }

    @Override
    public String process() throws Throwable {
        File bizFile = createBizFile();
        final Biz biz = bizFactoryService.createBiz(bizFile);
        if (bizManagerService.getBizByIdentity(biz.getIdentity()) != null) {
            return String.format("Biz:\'%s\' has been installed.", biz.getIdentity());
        }

        if (INSTALLING.compareAndSet(false, true)) {
            preBiz = biz.getIdentity();
            ThreadPoolExecutor executor = ThreadPoolManager.getThreadPool(
                Constants.JARSLINK_COMMAND_THREAD_POOL_NAME).getExecutor();
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        bizManagerService.registerBiz(biz);
                        biz.start(new String[] {});
                        LOGGER.info(String.format("Install Biz:\'%s\' success.", biz.getIdentity()));
                    } catch (Throwable throwable) {
                        LOGGER.error(String.format("Install Biz:\'%s\' fail.", biz.getIdentity()),
                            throwable);
                        bizManagerService.unRegisterBizStrictly(biz.getBizName(),
                            biz.getBizVersion());
                        try {
                            biz.stop();
                        } catch (Throwable e) {
                            LOGGER.error(
                                String.format("UnInstall Biz:\'%s\' fail.", biz.getIdentity()),
                                throwable);
                        }
                    } finally {
                        INSTALLING.compareAndSet(true, false);
                    }
                }
            });
            return String.format("Biz:\'%s\' is installing.", preBiz);
        } else {
            return String.format("Biz:\'%s\' is installing, please wait and retry later!", preBiz);
        }

    }

    private File createBizFile() throws IOException {
        String url = typeCommandOption.getArgs()[0];
        String fileName = parseFileName(url);
        URL bizUrl = new URL(url);
        File workingDir = new File(EnvironmentUtils.getProperty(Constants.JARSLINK_WORKING_DIR));
        File bizFile = new File(workingDir, fileName);
        FileUtils.copyInputStreamToFile(bizUrl.openStream(), bizFile);
        return bizFile;
    }

    private String parseFileName(String url) {
        int index = -1;
        if (url.lastIndexOf('/') != -1) {
            index = url.lastIndexOf('/');
        } else {
            index = url.lastIndexOf(File.separator);
        }
        AssertUtils.isTrue(index != -1, "Format of biz file location is error.");
        return url.substring(index + 1);
    }
}