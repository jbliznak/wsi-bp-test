/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.test.ws.jaxws.samples.wsse.policy.basic;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPFaultException;

import junit.framework.Test;

import org.apache.cxf.ws.security.SecurityConstants;
import org.jboss.wsf.test.JBossWSCXFTestSetup;
import org.jboss.wsf.test.JBossWSTest;

/**
 * WS-Security Policy sign & encrypt test case
 * (multiple clients)
 *
 * @author alessio.soldano@jboss.com
 * @since 13-Jan-2012
 */
public final class MultipleClientsSignEncryptTestCase extends JBossWSTest
{
   private final String serviceURL = "http://" + getServerHost() + ":8080/jaxws-samples-wsse-policy-sign-encrypt-mc/SecurityService";
   
   public static Test suite()
   {
      return new JBossWSCXFTestSetup(MultipleClientsSignEncryptTestCase.class,
            "jaxws-samples-wsse-policy-sign-encrypt-mc-client.jar jaxws-samples-wsse-policy-sign-encrypt-mc.war");
   }

   public void testAlice() throws Exception
   {
      QName serviceName = new QName("http://www.jboss.org/jbossws/ws-extensions/wssecuritypolicy", "SecurityService");
      URL wsdlURL = new URL(serviceURL + "?wsdl");
      Service service = Service.create(wsdlURL, serviceName);
      ServiceIface proxy = (ServiceIface)service.getPort(ServiceIface.class);
      setupWsse(proxy, "alice");
      try
      {
         assertEquals("Multiple Clients Secure Hello World!", proxy.sayHello());
      }
      catch (SOAPFaultException e)
      {
         throw new Exception("Please check that the Bouncy Castle provider is installed.", e);
      }
   }
   
   public void testJohn() throws Exception
   {
      QName serviceName = new QName("http://www.jboss.org/jbossws/ws-extensions/wssecuritypolicy", "SecurityService");
      URL wsdlURL = new URL(serviceURL + "?wsdl");
      Service service = Service.create(wsdlURL, serviceName);
      ServiceIface proxy = (ServiceIface)service.getPort(ServiceIface.class);
      setupWsse(proxy, "john");
      try
      {
         assertEquals("Multiple Clients Secure Hello World!", proxy.sayHello());
      }
      catch (SOAPFaultException e)
      {
         throw new Exception("Please check that the Bouncy Castle provider is installed.", e);
      }
   }
   
   private void setupWsse(ServiceIface proxy, String client)
   {
      ((BindingProvider)proxy).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://localhost:7070/jaxws-samples-wsse-policy-sign-encrypt-mc/SecurityService");
      ((BindingProvider)proxy).getRequestContext().put(SecurityConstants.CALLBACK_HANDLER, new KeystorePasswordCallback());
      ((BindingProvider)proxy).getRequestContext().put(SecurityConstants.SIGNATURE_PROPERTIES, Thread.currentThread().getContextClassLoader().getResource("META-INF/" + client + ".properties"));
      ((BindingProvider)proxy).getRequestContext().put(SecurityConstants.ENCRYPT_PROPERTIES, Thread.currentThread().getContextClassLoader().getResource("META-INF/" + client + ".properties"));
      ((BindingProvider)proxy).getRequestContext().put(SecurityConstants.SIGNATURE_USERNAME, client);
      ((BindingProvider)proxy).getRequestContext().put(SecurityConstants.ENCRYPT_USERNAME, "bob");
   }
}
