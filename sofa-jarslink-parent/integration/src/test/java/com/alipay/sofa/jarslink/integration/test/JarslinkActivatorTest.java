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
package com.alipay.sofa.jarslink.integration.test;

import com.alipay.sofa.ark.common.util.EnvironmentUtils;
import com.alipay.sofa.ark.spi.model.PluginContext;
import com.alipay.sofa.ark.spi.service.PluginActivator;
import com.alipay.sofa.jarslink.integration.activator.JarslinkActivator;
import com.alipay.sofa.jarslink.spi.constant.Constants;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;

/**
 * @author qilong.zql
 * @since 0.1.0
 */
public class JarslinkActivatorTest {

    @Test
    public void test() {
        PluginContext pluginContext = Mockito.mock(PluginContext.class);
        PluginActivator pluginActivator = new JarslinkActivator();
        pluginActivator.start(pluginContext);

        String tempPath = FileUtils.getTempDirectoryPath() + File.separator
                          + Constants.JARSLINK_IDENTITY;
        Assert.assertTrue(tempPath.equals(EnvironmentUtils
            .getProperty(Constants.JARSLINK_WORKING_DIR)));
    }

}