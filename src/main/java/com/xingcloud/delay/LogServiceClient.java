package com.xingcloud.delay;

import com.xingcloud.delay.thrift.LogService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * User: IvyTang
 * Date: 13-1-8
 * Time: 下午5:30
 */
public class LogServiceClient {

    private static final Log LOG = LogFactory.getLog(LogServiceClient.class);

    private static final LogServiceClient instance = new LogServiceClient();

    private LogServiceClient() {
        init();
    }

    public static LogServiceClient getInstance() {
        return instance;
    }


    ClientWrap conn = null;


    public int send(List<String> logs) throws Exception {
        ClientWrap c = takeClient();
        try {
            return c.client.send(logs);
        } catch(TException e){
            destroyClientWrap(c);
            conn = null;
            throw e;
        }
    }


    public int send(List<String> logs, long daytptime) throws Exception {
        ClientWrap c = this.takeClient();
        try {
            return c.client.sendLogDay(logs, daytptime);
        } catch (TException e){
            destroyClientWrap(c);
            conn = null;
            throw e;
        }
    }


    private class CONF {
        String host = "127.0.0.1";
        int port = 10080;
        int preferSize = 16;
        int maxSize = 32;
    }


    private CONF conf = new CONF();

    private void init() {
        InputStream inputStream = getClass().getResourceAsStream("/log_client.properties");
        Properties props = new Properties();
        try {
            props.load(inputStream);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        conf.host = props.getProperty("host", "localhost");
        conf.port = Integer.valueOf(props.getProperty("port", "1080"));
        conf.maxSize = Integer.valueOf(props.getProperty("max_active"));
        conf.preferSize = Integer.valueOf(props.getProperty("prefer_active"));
    }

    private ClientWrap createClientWrap() throws TTransportException {
        TSocket transport = new TSocket(conf.host, conf.port);
        TFramedTransport frame = new TFramedTransport(transport);
        TProtocol protocol = new TBinaryProtocol(frame);
        transport.open();
        LogService.Client client = new LogService.Client(protocol);

        return new ClientWrap(client, transport);
    }

    private void destroyClientWrap(ClientWrap client) {
        client.close();
    }

    private class ClientWrap {

        LogService.Client client;
        TSocket transport;

        ClientWrap(LogService.Client client, TSocket transport) {
            this.client = client;
            this.transport = transport;
        }

        void close() {
            transport.close();
        }

    }


    public ClientWrap takeClient() throws TTransportException{
        if (conn == null) {
            conn = createClientWrap();
        }

        return conn;
    }

}
