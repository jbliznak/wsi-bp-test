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

import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;

import junit.framework.Test;

import org.apache.cxf.ws.security.SecurityConstants;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.wsf.test.JBossWSCXFTestSetup;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestHelper;
import org.jboss.wsf.test.JBossWSTestHelper.BaseDeployment;

/**
 * WS-Security Policy username test case
 *
 * @author alessio.soldano@jboss.com
 * @since 29-Apr-2011
 */
public final class UsernameTestCase extends JBossWSTest
{
   private final String serviceURL = "http://" + getServerHost() + ":8080/jaxws-samples-wsse-policy-username-unsecure-transport/service";
   private final String javaFirstServiceURL = "http://" + getServerHost() + ":8080/jaxws-samples-wsse-policy-username-unsecure-transport/javafirst-service";

   public static BaseDeployment<?>[] createDeployments() {
      List<BaseDeployment<?>> list = new LinkedList<BaseDeployment<?>>();
      list.add(new JBossWSTestHelper.WarDeployment("jaxws-samples-wsse-policy-username-unsecure-transport.war") { {
         archive
               .setManifest(new StringAsset("Manifest-Version: 1.0\n"
                     + "Dependencies: org.jboss.ws.cxf.jbossws-cxf-client\n"))
               .addClass(org.jboss.test.ws.jaxws.samples.wsse.policy.basic.JavaFirstServiceIface.class)
               .addClass(org.jboss.test.ws.jaxws.samples.wsse.policy.basic.JavaFirstServiceImpl.class)
               .addClass(org.jboss.test.ws.jaxws.samples.wsse.policy.basic.ServerUsernamePasswordCallback.class)
               .addClass(org.jboss.test.ws.jaxws.samples.wsse.policy.basic.ServiceIface.class)
               .addClass(org.jboss.test.ws.jaxws.samples.wsse.policy.basic.ServiceImpl.class)
               .addClass(org.jboss.test.ws.jaxws.samples.wsse.policy.jaxws.SayHello.class)
               .addClass(org.jboss.test.ws.jaxws.samples.wsse.policy.jaxws.SayHelloResponse.class)
               .addAsWebInfResource(new File(JBossWSTestHelper.getTestResourcesDir() + "/jaxws/samples/wsse/policy/basic/username-unsecure-transport/JavaFirstPolicy.xml"), "classes/JavaFirstPolicy.xml")
               .addAsWebInfResource(new File(JBossWSTestHelper.getTestResourcesDir() + "/jaxws/samples/wsse/policy/basic/username-unsecure-transport/WEB-INF/jaxws-endpoint-config.xml"), "jaxws-endpoint-config.xml")
               .addAsWebInfResource(new File(JBossWSTestHelper.getTestResourcesDir() + "/jaxws/samples/wsse/policy/basic/username-unsecure-transport/WEB-INF/wsdl/SecurityService.wsdl"), "wsdl/SecurityService.wsdl")
               .addAsWebInfResource(new File(JBossWSTestHelper.getTestResourcesDir() + "/jaxws/samples/wsse/policy/basic/username-unsecure-transport/WEB-INF/wsdl/SecurityService_schema1.xsd"), "wsdl/SecurityService_schema1.xsd")
               .setWebXML(new File(JBossWSTestHelper.getTestResourcesDir() + "/jaxws/samples/wsse/policy/basic/username-unsecure-transport/WEB-INF/web.xml"));
         }
      });
      return list.toArray(new BaseDeployment<?>[list.size()]);
   }

   public static Test suite()
   {
      return new JBossWSCXFTestSetup(UsernameTestCase.class, JBossWSTestHelper.writeToFile(createDeployments()));
   }

   public void test() throws Exception
   {
      QName serviceName = new QName("http://www.jboss.org/jbossws/ws-extensions/wssecuritypolicy", "SecurityService");
      URL wsdlURL = new URL(serviceURL + "?wsdl");
      Service service = Service.create(wsdlURL, serviceName);
      ServiceIface proxy = (ServiceIface)service.getPort(ServiceIface.class);
      setupWsse((BindingProvider)proxy, "kermit");
      ((BindingProvider)proxy).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceURL.replaceFirst("8080", "7070"));
      assertEquals("Secure Hello World!", proxy.sayHello());
   }

   public void testWrongPassword() throws Exception
   {
      QName serviceName = new QName("http://www.jboss.org/jbossws/ws-extensions/wssecuritypolicy", "SecurityService");
      URL wsdlURL = new URL(serviceURL + "?wsdl");
      Service service = Service.create(wsdlURL, serviceName);
      ServiceIface proxy = (ServiceIface)service.getPort(ServiceIface.class);
      setupWsse((BindingProvider)proxy, "snoopy");
      ((BindingProvider)proxy).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceURL.replaceFirst("8080", "7070"));
      try
      {
         proxy.sayHello();
         fail("User snoopy shouldn't be authenticated.");
      }
      catch (Exception e)
      {
         //OK
      }
   }

   public void testNoCBH() throws Exception
   {
      QName serviceName = new QName("http://www.jboss.org/jbossws/ws-extensions/wssecuritypolicy", "SecurityService");
      URL wsdlURL = new URL(serviceURL + "?wsdl");
      Service service = Service.create(wsdlURL, serviceName);
      ServiceIface proxy = (ServiceIface)service.getPort(ServiceIface.class);
      setupWsseNoCBH((BindingProvider)proxy, "kermit", "thefrog");
      assertEquals("Secure Hello World!", proxy.sayHello());
      setupWsseNoCBH((BindingProvider)proxy, "kermit", "wrongpassword");
      ((BindingProvider)proxy).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceURL.replaceFirst("8080", "7070"));
      try
      {
         proxy.sayHello();
         fail("User snoopy shouldn't be authenticated.");
      }
      catch (Exception e)
      {
         //OK
      }
   }

   public void testJavaFirst() throws Exception
   {
      QName serviceName = new QName("http://www.jboss.org/jbossws/ws-extensions/wssecuritypolicy", "JavaFirstSecurityService");
      URL wsdlURL = new URL(javaFirstServiceURL + "?wsdl");
      Service service = Service.create(wsdlURL, serviceName);
      JavaFirstServiceIface proxy = (JavaFirstServiceIface)service.getPort(JavaFirstServiceIface.class);
      ((BindingProvider)proxy).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, javaFirstServiceURL.replaceFirst("8080", "7070"));
      setupWsse((BindingProvider)proxy, "kermit");
      assertEquals("Secure Hello World!", proxy.sayHello());
   }

   public void testJavaFirstWrongPassword() throws Exception
   {
      QName serviceName = new QName("http://www.jboss.org/jbossws/ws-extensions/wssecuritypolicy", "JavaFirstSecurityService");
      URL wsdlURL = new URL(javaFirstServiceURL + "?wsdl");
      Service service = Service.create(wsdlURL, serviceName);
      JavaFirstServiceIface proxy = (JavaFirstServiceIface)service.getPort(JavaFirstServiceIface.class);
      setupWsse((BindingProvider)proxy, "snoopy");
      ((BindingProvider)proxy).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, javaFirstServiceURL.replaceFirst("8080", "7070"));
      try
      {
         proxy.sayHello();
         fail("User snoopy shouldn't be authenticated.");
      }
      catch (Exception e)
      {
         //OK
      }
   }

   private void setupWsse(BindingProvider proxy, String username)
   {
      proxy.getRequestContext().put(SecurityConstants.USERNAME, username);
      proxy.getRequestContext().put(SecurityConstants.CALLBACK_HANDLER, "org.jboss.test.ws.jaxws.samples.wsse.policy.basic.UsernamePasswordCallback");
   }
   
   private void setupWsseNoCBH(BindingProvider proxy, String username, String password)
   {
      proxy.getRequestContext().put(SecurityConstants.USERNAME, username);
      proxy.getRequestContext().put(SecurityConstants.PASSWORD, password);
   }
}
