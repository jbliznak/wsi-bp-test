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
package org.jboss.test.ws.jaxws.samples.wsse.policy.trust;

import junit.framework.Test;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.jboss.test.ws.jaxws.samples.wsse.policy.trust.actas.ActAsServiceIface;
import org.jboss.wsf.test.JBossWSTest;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import java.net.URL;

/**
 * A demo of using WS-Trust ActAs extension.
 *
 * User: rsearls@redhat.com
 * Date: 1/26/14
 */
public class WSTrustActAsTestCase extends JBossWSTest
{
   private final String serviceURL = "http://" + getServerHost() + ":8080/jaxws-samples-wsse-policy-trust-actas/ActAsService";

   public static Test suite()
   {
      //deploy client, STS and service; start a security domain to be used by the STS for authenticating client
      return WSTrustTestUtils.getTestSetup(WSTrustActAsTestCase.class,
            DeploymentArchives.CLIENT_JAR + " " + DeploymentArchives.STS_WAR + " " + DeploymentArchives.SERVER_WAR + " " + DeploymentArchives.SERVER_ACTAS_WAR);
   }

   /**
    *  Request a security token that allows it to act as if it were somebody else.
    *
    * @throws Exception
    */
   public void testActAs() throws Exception
   {
      Bus bus = BusFactory.newInstance().createBus();
      try
      {
         BusFactory.setThreadDefaultBus(bus);

         final QName serviceName = new QName("http://www.jboss.org/jbossws/ws-extensions/actaswssecuritypolicy", "ActAsService");
         final URL wsdlURL = new URL(serviceURL + "?wsdl");
         Service service = Service.create(wsdlURL, serviceName);
         ActAsServiceIface proxy = (ActAsServiceIface) service.getPort(ActAsServiceIface.class);
         ((BindingProvider)proxy).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceURL.replaceFirst("8080", "7070"));

         WSTrustTestUtils.setupWsseAndSTSClientActAs((BindingProvider) proxy, bus);

         assertEquals("ActAs WS-Trust Hello World!", proxy.sayHello());
      }
      finally
      {
         bus.shutdown(true);
      }
   }

}
