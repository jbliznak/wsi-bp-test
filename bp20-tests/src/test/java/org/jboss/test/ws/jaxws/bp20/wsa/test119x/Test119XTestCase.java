/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.test.ws.jaxws.bp20.wsa.test119x;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.AddressingFeature;

import org.apache.cxf.helpers.IOUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.test.ws.jaxws.bp.common.BPTest;
import org.jboss.wsf.test.JBossWSTestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
@RunWith(Arquillian.class)
public class Test119XTestCase extends BPTest
{

   @ArquillianResource
   private URL baseURL;

   @Deployment(testable = false)
   public static WebArchive createDeployment()
   {
      WebArchive archive = ShrinkWrap.create(WebArchive.class, "jaxws-bp20test119x.war");
      archive.setManifest(new StringAsset("Manifest-Version: 1.0\n" + "Dependencies: org.apache.cxf\n"))
            .addPackages(false, Filters.exclude(Test119XTestCase.class), Test119XTestCase.class.getPackage().getName())
            .setWebXML(new File(JBossWSTestHelper.getTestResourcesDir() + "/jaxws/bp20/wsa/test119x/WEB-INF/web.xml"));
      addResroucesToWebInf(JBossWSTestHelper.getTestResourcesDir() + "/jaxws/bp20/wsa/test119x/WEB-INF", archive, "wsdl", "xsd");
      return archive;
   }
   @Test
   @RunAsClient 
   public void testWSA() throws Exception
   {
      // construct proxy
      QName serviceName = new QName("http://example.org/wsaTestService", "wsaTestService");
      URL wsdlURL = new URL(baseURL + "/Test119x" + "?wsdl");
      Service service = Service.create(wsdlURL, serviceName);
      WsaTestPortType port = (WsaTestPortType) service.getPort(WsaTestPortType.class);
      // invoke method
      ((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
            getProxyAddress(baseURL) + "/Test119x");

      System.out.println("Invoking echo1...");
      String response = port.echo1("input string");
      System.out.println("echo1.result: " + response);
      
      
      System.out.println("Invoking echo2...");
      port.echo2("input string");
      
   }
   
   //Test1191
   @Test
   @RunAsClient
   public void testMessageIdMissed() throws Exception
   {
      // construct proxy
      QName serviceName = new QName("http://example.org/wsaTestService", "wsaTestService");
      QName portName = new QName("http://example.org/wsaTestService", "wsaTestPort");
      URL wsdlURL = new URL(baseURL + "/Test119x" + "?wsdl");
      Service service = Service.create(wsdlURL, serviceName);
           
      Dispatch<SOAPMessage> disp = service.createDispatch(portName, SOAPMessage.class,
            Service.Mode.MESSAGE,
            new AddressingFeature(false, false));
      ((BindingProvider) disp).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
            getProxyAddress(baseURL) + "/Test119x");
      InputStream msgIns = getClass().getResourceAsStream("./wsa-without-messageid.xml");
      String msg = new String(IOUtils.readBytesFromStream(msgIns));
      msg = msg.replaceAll("\\$PORT", PROXY_PORT);
      
      ByteArrayInputStream bout = new ByteArrayInputStream(msg.getBytes());
      
      SOAPMessage soapReqMsg = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage(null, bout);
      assertNotNull(soapReqMsg);
      
      try {
          disp.invoke(soapReqMsg);
          fail("WebServiceException is expected");
      } catch (WebServiceException ex) {
          assertTrue("A required header representing a Message Addressing Property is not present is expected", 
                ex.getMessage().contains("A required header representing a Message Addressing Property is not present"));
      }        
   }

   @Test
   @RunAsClient
   public void testVersionMisMatch() throws Exception
   {
      //test1194-version mismatch  
      // construct proxy 
      URL url = new URL(baseURL + "/Test119x");
      HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
      InputStream msgIns = getClass().getResourceAsStream("./wsa-version-mismatch.xml");
      String msg = new String(IOUtils.readBytesFromStream(msgIns));
      httpConn.setRequestProperty("Content-Length", String.valueOf(msg.length()));
      httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
      httpConn.setRequestMethod("POST");
      httpConn.setDoOutput(true);
      httpConn.setDoInput(true);

      OutputStream out = httpConn.getOutputStream();
      out.write(msg.getBytes());
      out.close();

      if (httpConn.getResponseCode() >= 500)
      {
         InputStreamReader isr = new InputStreamReader(httpConn.getErrorStream());
         BufferedReader in = new BufferedReader(isr);

         String inputLine;

         StringBuffer buffer = new StringBuffer();
         while ((inputLine = in.readLine()) != null)
         {
            buffer.append(inputLine);
         }

         in.close();
         System.out.println(buffer.toString());
         assertTrue("Expected version mismatch error", buffer.toString().contains("VersionMismatch"));

      }
      else
      {
         fail("Expected version mismatch error");
      }
      
      
      /* Test1194 : Application fault sent to a non-anonymous FaultTo address.
       * There is such test in apache cxf: look at org.apache.cxf.systest.ws.addr_feature.WSAFaultToClientServerTest
       */

   }
   
}