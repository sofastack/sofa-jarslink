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
package com.alipay.sofa.jarslink.integration.activator;

import com.alipay.sofa.ark.common.util.AssertUtils;
import com.alipay.sofa.ark.common.util.EnvironmentUtils;
import com.alipay.sofa.ark.common.util.StringUtils;
import com.alipay.sofa.ark.spi.model.PluginContext;
import com.alipay.sofa.ark.spi.service.PluginActivator;
import com.alipay.sofa.ark.spi.service.biz.BizDeployer;
import com.alipay.sofa.ark.spi.service.session.CommandProvider;
import com.alipay.sofa.jarslink.common.log.JSKLogger;
import com.alipay.sofa.jarslink.common.log.JSKLoggerFactory;
import com.alipay.sofa.jarslink.integration.command.JSKCommandProvider;
import com.alipay.sofa.jarslink.integration.deployer.JarslinkDeployer;
import com.alipay.sofa.jarslink.spi.constant.Constants;
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * SOFAJarslink Activator, integration with SOFAArk
 * {@link }
 *
 * @author qilong.zql
 * @since 0.1.0
 */
public class JarslinkActivator implements PluginActivator {

    private static final JSKLogger LOGGER          = JSKLoggerFactory.getDefaultLogger();
    private BizDeployer            bizDeployer     = new JarslinkDeployer();
    private CommandProvider        commandProvider = new JSKCommandProvider();

    @Override
    public void start(PluginContext context) {
        LOGGER.info("Jarslink is activating!");
        initWorkingDir();
        context.publishService(BizDeployer.class, bizDeployer);
        context.publishService(CommandProvider.class, commandProvider);
    }

    @Override
    public void stop(PluginContext context) {
        LOGGER.info("Jarslink is stopping");
    }

    public void initWorkingDir() {
        String workingDir = EnvironmentUtils.getProperty(Constants.JARSLINK_WORKING_DIR);
        File dirFile = StringUtils.isEmpty(workingDir) ? null : new File(workingDir);
        if (StringUtils.isEmpty(workingDir)) {
            workingDir = FileUtils.getTempDirectoryPath() + File.separator
                         + Constants.JARSLINK_IDENTITY;
            dirFile = new File(workingDir);

            FileUtils.deleteQuietly(dirFile);

            dirFile.mkdir();
            dirFile.deleteOnExit();

            EnvironmentUtils.setProperty(Constants.JARSLINK_WORKING_DIR, workingDir);
        }
        AssertUtils.isTrue(dirFile.exists() && dirFile.isDirectory(),
            "Jarslink Working directory must exist.");
    }

}