/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.test.ws.jaxws.samples.wsse.policy.trust;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;

import junit.framework.Test;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.jboss.test.ws.jaxws.samples.wsse.policy.trust.holderofkey.HolderOfKeyIface;
import org.jboss.wsf.test.JBossWSCXFTestSetup;
import org.jboss.wsf.test.JBossWSTest;

/**
 * A demo of using SAML Holder-of-key
 *
 * User: rsearls@redhat.com
 * Date: 3/14/14
 */
public class WSTrustHolderOfKeyTestCase extends JBossWSTest
{
   public static Test suite()
   {
      //deploy client, STS and service; start a security domain to be used by the STS for authenticating client
      JBossWSCXFTestSetup testSetup = WSTrustTestUtils.getTestSetup(WSTrustHolderOfKeyTestCase.class,
         DeploymentArchives.CLIENT_JAR + " " + DeploymentArchives.STS_HOLDEROFKEY_WAR + " " + DeploymentArchives.SERVER_HOLDEROFKEY_WAR);

      // setup the https connector in the server config file.
      Map<String, String> sslOptions = new HashMap<String, String>();
      sslOptions.put("server-identity.ssl.keystore-path", System.getProperty("org.jboss.ws.testsuite.server.keystore"));
      sslOptions.put("server-identity.ssl.keystore-password", "changeit");
      sslOptions.put("server-identity.ssl.alias", "tomcat");

      testSetup.setHttpsConnectorRequirement(sslOptions);
      return testSetup;

   }

   public void testBearer() throws Exception
   {

      Bus bus = BusFactory.newInstance().createBus();
      try
      {

         BusFactory.setThreadDefaultBus(bus);

         final QName serviceName = new QName("http://www.jboss.org/jbossws/ws-extensions/holderofkeywssecuritypolicy", "HolderOfKeyService");
         final URL wsdlURL = new URL("https://" + getServerHost() + ":8443/jaxws-samples-wsse-policy-trust-holderofkey/HolderOfKeyService?wsdl");
         Service service = Service.create(wsdlURL, serviceName);
         HolderOfKeyIface proxy = (HolderOfKeyIface) service.getPort(HolderOfKeyIface.class);

         WSTrustTestUtils.setupWsseAndSTSClientHolderOfKey((BindingProvider) proxy, bus);
         assertEquals("Holder-Of-Key WS-Trust Hello World!", proxy.sayHello());

      } finally
      {
         bus.shutdown(true);
      }
   }

}
