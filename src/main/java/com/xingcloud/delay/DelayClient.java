package com.xingcloud.delay;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * User: IvyTang
 * Date: 13-1-8
 * Time: 下午5:30
 */
public class DelayClient {

    private static final Log LOG = LogFactory.getLog(DelayClient.class);

    public static void main(String[] args) throws Exception {
        DelayTail delayTail = new DelayTail(Constants.DELAY_CONF_DIR);
        LOG.info("delay tail start...");
        delayTail.start();
    }
}

