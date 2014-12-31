package ru.roseurobank.c24.testvelocity;

import java.util.Date;
import java.util.EventObject;
import org.apache.camel.Exchange;
import org.apache.camel.management.EventNotifierSupport;
import org.apache.camel.management.event.ExchangeCompletedEvent;
import org.apache.camel.management.event.ExchangeSentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zadorozhniy276
 */
public class MyLoggingSentEventNotifer extends EventNotifierSupport {

    Logger logger = LoggerFactory.getLogger(MyLoggingSentEventNotifer.class);

    @Override
    public void notify(EventObject event) throws Exception {
//        if (event instanceof ExchangeSentEvent) {
//            ExchangeSentEvent sent = (ExchangeSentEvent) event;
//            logger.info(">>> Took " + sent.getTimeTaken() + " millis to send to external system : " + sent.getEndpoint());
//        }

        if (event instanceof ExchangeCompletedEvent) {
            ExchangeCompletedEvent exchangeCompletedEvent = (ExchangeCompletedEvent) event;
            Exchange exchange = exchangeCompletedEvent.getExchange();
            String routeId = exchange.getFromRouteId();
//            Date created = ((ExchangeCompletedEvent) event).getExchange().getProperty(Exchange.CREATED_TIMESTAMP, Date.class);
            Date created = ((ExchangeCompletedEvent) event).getExchange().getProperty(Exchange.CREATED_TIMESTAMP, Date.class);
            // calculate elapsed time
            Date now = new Date();
            long elapsed = now.getTime() - created.getTime();
            logger.info(">>>  created.getTime() " + created.getTime());
            logger.info(">>> Took " + elapsed + " millis for the exchange on the route : " + routeId);
        }

    }

    @Override
    public boolean isEnabled(EventObject eo) {
        return true;
    }

    @Override
    protected void doStart() throws Exception {
        logger.info( " dostart  ");
        // filter out unwanted events
        setIgnoreCamelContextEvents(true);
        setIgnoreServiceEvents(true);
        setIgnoreRouteEvents(true);
        setIgnoreExchangeCreatedEvent(true);
        setIgnoreExchangeCompletedEvent(false);
        setIgnoreExchangeFailedEvents(true);
        setIgnoreExchangeRedeliveryEvents(true);
        setIgnoreExchangeSentEvents(false);

    }

    @Override
    protected void doStop() throws Exception {
        logger.info( " dostop  ");
    }

}
