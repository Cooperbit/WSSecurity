/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */
package com.quarxs.wssecurity;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.configuration.jsse.TLSParameterJaxBUtils;
import org.apache.cxf.configuration.security.KeyManagersType;
import org.apache.cxf.configuration.security.KeyStoreType;
import org.apache.cxf.configuration.security.TrustManagersType;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.ext.logging.LoggingInInterceptor;
import org.apache.cxf.ext.logging.LoggingOutInterceptor;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.ws.addressing.AddressingProperties;
import org.apache.cxf.ws.addressing.AttributedURIType;
import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.apache.cxf.ws.addressing.JAXWSAConstants;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.wss4j.dom.handler.WSHandlerConstants;
import uy.com.antel.servicio.epagos.inicio_solicitud_v6.IniciarSolicitud;
import uy.com.antel.servicio.epagos.inicio_solicitud_v6.SolicitudConcepto;
import uy.com.antel.servicio.epagos.inicio_solicitud_v6.SolicitudDTO;
import uy.com.antel.servicio.epagos.inicio_solicitud_v6.SolicitudDetalleItem;
import uy.com.antel.servicio.epagos.inicio_solicitud_v6.SolicitudItem;
import uy.com.antel.servicio.epagos.inicio_solicitud_v6.SolicitudOfertaProducto;
import uy.com.antel.servicio.epagos.inicio_solicitud_v6.SolicitudOfertaProducto.Conceptos;
import uy.com.antel.servicio.epagos.inicio_solicitud_v6_0.InicioSolicitudServiceFinalService;
import uy.com.antel.servicio.epagos.inicio_solicitud_v6_0.Request;

/**
 *
 * @author shamp
 */
public class WSSecurity {

    public static void main(String[] args) throws URISyntaxException, IOException, GeneralSecurityException {
        InicioSolicitudServiceFinalService service = new InicioSolicitudServiceFinalService();
        Request inicioSolicitudRequest = service.getInicioSolicitudPort(new WSAddressingFeature());
        Client client = ClientProxy.getClient(inicioSolicitudRequest);
        HTTPConduit conduit = (HTTPConduit) client.getConduit();
        TLSClientParameters tlsParams = new TLSClientParameters();
        KeyStoreType keyStoreType = new KeyStoreType();
        keyStoreType.setFile("C:\\Program Files\\Java\\jdk1.8.0_341\\jre\\lib\\security\\cacerts");
        keyStoreType.setPassword("changeit");
        keyStoreType.setType("jks");
        KeyManagersType keyManagerType = new KeyManagersType();
        keyManagerType.setKeyStore(keyStoreType);
        TrustManagersType trustManagersType = new TrustManagersType();
        trustManagersType.setKeyStore(keyStoreType);
        tlsParams.setKeyManagers(TLSParameterJaxBUtils.getKeyManagers(keyManagerType));
        tlsParams.setTrustManagers(TLSParameterJaxBUtils.getTrustManagers(trustManagersType, false));
        conduit.setTlsClientParameters(tlsParams);
        
        Endpoint endpoint = client.getEndpoint();
        
        WSAddressingFeature addressingFeature = new WSAddressingFeature();
        endpoint.getActiveFeatures().add(addressingFeature);
        AddressingProperties addressingProperties = new AddressingProperties();
        addressingProperties.setTo(new AttributedURIType());
        addressingProperties.getTo().setValue("urn:antel:mdm:system:epagos");
        addressingProperties.setFrom(new EndpointReferenceType());
        addressingProperties.getFrom().setAddress(new AttributedURIType());
        addressingProperties.getFrom().getAddress().setValue("urn:antel:mdm:system:epagos:inacoop");
        client.getRequestContext().put(JAXWSAConstants.CLIENT_ADDRESSING_PROPERTIES, addressingProperties);
        
        Map<String, Object> outProps = new HashMap<>();
        outProps.put(WSHandlerConstants.ACTION, WSHandlerConstants.ENCRYPT + " " + WSHandlerConstants.SIGNATURE);
        
        outProps.put(WSHandlerConstants.ENCRYPTION_USER, "antelepagos_testing_server");
        File antelProperties = new File("src/main/resources/properties/antel.properties");
        outProps.put(WSHandlerConstants.ENC_PROP_FILE, antelProperties.getPath());
        
        File quarxsProperties = new File("src/main/resources/properties/quarxs.properties");
        outProps.put(WSHandlerConstants.SIGNATURE_USER, "antelepagos_testing");
        outProps.put(WSHandlerConstants.PW_CALLBACK_CLASS, QuarxsPWCallbackHandler.class.getName());
        outProps.put(WSHandlerConstants.SIG_PROP_FILE, quarxsProperties.getPath());
        
        WSS4JOutInterceptor wssOut = new WSS4JOutInterceptor(outProps);
        endpoint.getOutInterceptors().add(wssOut);
        
        client.getInInterceptors().add(new LoggingInInterceptor());
        client.getInFaultInterceptors().add(new LoggingInInterceptor());
        client.getOutInterceptors().add(new LoggingOutInterceptor());
        client.getOutFaultInterceptors().add(new LoggingOutInterceptor());
        
        IniciarSolicitud iniciarSolicitud = new IniciarSolicitud();
        SolicitudDTO solicitudDTO = new SolicitudDTO();
        //Generar id
        solicitudDTO.setIdExterno("1"); 
        solicitudDTO.setCodigoComercio("epagos:comercio:inacoop");
        solicitudDTO.setFechaHoraVencimiento(null);
        solicitudDTO.setCodigoMoneda("UYU");
        
        SolicitudDTO.Productos productos = new SolicitudDTO.Productos();
        SolicitudOfertaProducto ofertaProducto = new SolicitudOfertaProducto();
        ofertaProducto.setCodigo("epagos:convenio:inacoop:pago_prestamo_coactiva_freecoop");
        Conceptos conceptos = new Conceptos();
        SolicitudConcepto solicitudConcepto = new SolicitudConcepto();
        solicitudConcepto.setCodigo("epagos:concepto:inacoop:pago_prestamo_coactiva_freecoop");
        conceptos.getConcepto().add(solicitudConcepto);
        ofertaProducto.setConceptos(conceptos);
        productos.getProducto().add(ofertaProducto);
        solicitudDTO.setProductos(productos);
        
        SolicitudDetalleItem solicitudDetalleItem = new SolicitudDetalleItem();
        SolicitudDetalleItem.Items items = new SolicitudDetalleItem.Items();
        SolicitudItem item = new SolicitudItem();
        item.setCodigo("Prestamo Frecoop");
        item.setDescripcion("Prestamo Frecoop");
        item.setCantidad(BigDecimal.ONE);
        item.setImporteGravado(BigDecimal.ZERO);
        item.setPorcentajeTasa(BigDecimal.ZERO);
        items.getItem().add(item);
        solicitudDetalleItem.setItems(items);
        solicitudDTO.setDetalleItems(solicitudDetalleItem);
        
        inicioSolicitudRequest.iniciarSolicitud(iniciarSolicitud);
    }
}
