/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quarxs.wssecurity;

import jakarta.xml.soap.SOAPMessage;
import java.io.StringWriter;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.binding.soap.saaj.SAAJInInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.w3c.dom.Document;

/**
 *
 * @author shamp
 */
public class LoggingInPayloadInterceptor extends AbstractSoapInterceptor {

    public static final String IN_PAYLOAD_KEY = "com.quarxs.security.WSSecurity";

    public LoggingInPayloadInterceptor() {
        super(Phase.POST_PROTOCOL);
        addAfter(SAAJInInterceptor.class.getName());
    }

    @Override
    public void handleMessage(SoapMessage message) throws Fault {
        Document document = message.getContent(SOAPMessage.class).getSOAPPart();
        StringWriter stringWriter = new StringWriter();
        try {
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(document), new StreamResult(stringWriter));
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        message.put(IN_PAYLOAD_KEY, stringWriter.toString());
        message.put(LoggingOutPayloadInterceptor.OUT_PAYLOAD_KEY, message.getExchange().get(LoggingOutPayloadInterceptor.OUT_PAYLOAD_KEY));
    }
}
