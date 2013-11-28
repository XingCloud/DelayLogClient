package com.xingcloud.delay;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * User: IvyTang
 * Date: 13-1-8
 * Time: 下午5:46
 */
public class DelayTail extends Tail {

    private static final Log LOG = LogFactory.getLog(DelayTail.class);

    public DelayTail(String configPath) {
        super(configPath);
        setLogProcessPerBatch(true);
    }

    @Override
    public void send(List<String> logs, long day) {
        try {
            LogServiceClient.getInstance().send(logs, this.dayStartTime);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
