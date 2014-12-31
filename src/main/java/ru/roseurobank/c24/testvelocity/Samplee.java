/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.roseurobank.c24.testvelocity;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kononov446
 */
public class Samplee {

    static final Logger LOG = LoggerFactory.getLogger(Samplee.class);

    public static void main(String[] args) throws Exception {

        ClassLoader cl = ClassLoader.getSystemClassLoader();

        URL[] urls = ((URLClassLoader) cl).getURLs();

        for (URL url : urls) {
            System.out.println(url.getFile());
        }
        System.out.println("-----------------");
        System.out.println(new File(".").getAbsolutePath());
System.out.println(new File("log4j.properties").getAbsolutePath());

      //  System.out.println(Thread.currentThread().getContextClassLoader().getResource("./log4j.properties").getPath());
System.out.println("------ -------------------");
        
        LOG.trace("trace123");
        LOG.debug("debug2");
        final CountDownLatch c = new CountDownLatch(1);
        CamelContext context = new DefaultCamelContext();
        context.getManagementStrategy().addEventNotifier(new MyLoggingSentEventNotifer());
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("file://in?noop=true")
                       // .log("1-" + System.currentTimeMillis())
                        //.setHeader("t1", simple(Long.toString(System.currentTimeMillis()), Long.class))
                        .process(new Processor() {
                            long t1;
                            public void process(Exchange exch) throws Exception {
                                t1 = System.currentTimeMillis();
                                exch.getIn().setHeader("t1", t1);                                
                            }
                        })
                        .setHeader("t1a", simple(Long.toString(System.currentTimeMillis()), Long.class))
                        .to("file://out")
                        //        .setHeader("dt", constant(Long.toString(new Long(System.currentTimeMillis())-"${headers.t1}")))//"${headers.t1}")))                
                        .process(new Processor() {
                            long dt;

                            public void process(Exchange exch) throws Exception {
                                dt = (System.currentTimeMillis() - exch.getIn().getHeader("t1", Long.class));
                                //Logger.getLogger(this.getClass()).info("duration dt=" + dt);                                  
                                exch.getIn().setHeader("t1", exch.getIn().getHeader("t1"));
                                exch.getIn().setHeader("dt", new Long(dt));
                                //Thread.currentThread().sleep(5000);
                                exch.getIn().setHeader("t2", System.currentTimeMillis());
                            }
                        })
                        //.log("t1: ${headers.t1}")
                        //.log("t1a: ${headers.t1a}")
                        //.log("t2: ${headers.t2}")
                        .log("duration: ${headers.dt}ms")
                        //.to("log:2-" + System.currentTimeMillis())
                        //.log("arbaitennnnnnnnnnnnnnnnnnnnnnnnnnnnn duration aa ${headers.t1} ${headers.t1}-1000 :"+(System.currentTimeMillis()-Long.parseLong("${headers.t1}"))+"ms")
                        //${headers.t1}
                        .log("!!!!!!!!!!!!1hello Camel")
                        .process(new Processor() {

                            @Override
                            public void process(Exchange exchng) throws Exception {
                                c.countDown();
                            }
                        });
//                                
            }
        });
       // Thread.currentThread().sleep(5000);
        context.start();
        c.await();
//        TimeUnit.SECONDS.sleep(10);
        context.stop();
       
    }

}
